package me.pignol.swift.client.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import me.pignol.swift.api.util.EnumHelper;
import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.MoveEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PacketFly extends Module {

    public static final PacketFly INSTANCE = new PacketFly();

    public Value<Mode> mode = new Value<>("Mode", Mode.FACTOR);
    public Value<Float> tickCountFly = new Value<>("Factor", 1f, 0.5f, 10f, v -> mode.getValue().equals(Mode.FACTOR));
    public Value<Type> typeSetting = new Value<>("Type", Type.DOWN);
    public Value<PhaseMode> phaseSetting = new Value<>("Phase", PhaseMode.FULL);
    public Value<Boolean> delayConfirmTeleport = new Value<>("Frequency", false);
    public Value<Integer> antiSetbackSpam = new Value<>("Limit", 0, 0, 10);
    //public Value<Boolean> debugMode = new Value<>("Debug", false);
    public Value<Boolean> antiKick = new Value<>("AntiKick", true);

    public PacketFly() {
        super("PacketFly", Category.MOVEMENT);
    }

    private int packetCounter = 0;
    private int currentTeleportId = 0;
    private int bypassCounter = 0;
    private int frequencyTick = 0;
    private float lastYaw, lastPitch;

    private float debugVal1 = 0f;
    private float debugVal2 = 0f;
    private float debugVal3 = 0f;

    private final Map<Integer, TimeVec3d> posLooks = new ConcurrentHashMap<Integer, TimeVec3d>();
    private final Set<CPacketPlayer> playerPackets = new ConcurrentSet<>();

    private CPacketConfirmTeleport delayedPacket = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }
    }

    @SubscribeEvent
    public void onUpdateWalking(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }
        posLooks.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getTime() > TimeUnit.SECONDS.toMillis(30L));
        if (bypassCounter >= 10) {
            bypassCounter = 0;
        }
        setSuffix(EnumHelper.getCapitalizedName(mode.getValue()));
        double ySpeed;
        if (event.getStage() != Stage.PRE) {
            return;
        }
        mc.player.motionZ = 0.0;
        mc.player.motionY = 0.0;
        mc.player.motionX = 0.0;
        if (!(mode.getValue().equals(Mode.SETBACK)) && this.currentTeleportId == 0) {
            if (iteratePacketCounterCheckMaxPackets(4)) {
                sendPackets(0.0, 0.0, 0.0, false);
            }
            return;
        }
        boolean isPhasing = isPlayerCollisionBoundingBoxEmpty();
        if (mc.player.movementInput.jump && (isPhasing || !playerMoveForwardOrStrafing())) {
            double d3;
            if (antiKick.getValue() && !isPhasing) {
                d3 = iteratePacketCounterCheckMaxPackets((mode.getValue().equals(Mode.SETBACK)) ? 10 : 20) ? -0.032 : 0.062;
            } else {
                d3 = 0.062;
            }
            ySpeed = d3;
        } else if (mc.player.movementInput.sneak) {
            ySpeed = -0.062;
        } else {
            ySpeed = !isPhasing ? (iteratePacketCounterCheckMaxPackets(4) ? ((antiKick.getValue()) ? -0.04 : 0.0) : 0.0) : 0.0;
        }
        if (phaseSetting.getValue().equals(PhaseMode.FULL) && isPhasing && playerMoveForwardOrStrafing() && ySpeed != 0.0) {
            ySpeed /= 2.5;
        }
        double[] dirSpeed = getSpeed(phaseSetting.getValue().equals(PhaseMode.FULL) && isPhasing ? 0.031 : 0.26);
        bypassCounter++;
        if (mode.getValue().equals(Mode.FACTOR)) {
            float rawFactor = tickCountFly.getValue();
            int factorInt = (int) Math.floor(rawFactor);
            float extraFactor = rawFactor - (float) factorInt;
            if (Math.random() <= extraFactor) {
                factorInt++;
            }
            for (int i = 1; i <= factorInt; ++i) {
                mc.player.motionX = dirSpeed[0] * (double) i;
                mc.player.motionY = ySpeed * (double) i;
                mc.player.motionZ = dirSpeed[1] * (double) i;
                sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ, !mode.getValue().equals(Mode.SETBACK));
            }
        } else {
            for (int i = 1; i <= 1; ++i) {
                mc.player.motionX = dirSpeed[0] * (double) i;
                mc.player.motionY = ySpeed * (double) i;
                mc.player.motionZ = dirSpeed[1] * (double) i;
                sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ, !mode.getValue().equals(Mode.SETBACK));
            }
        }
    }

    @Override
    public void onEnable() {
        clearValues();
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (!(mode.getValue().equals(Mode.SETBACK)) && this.currentTeleportId == 0) {
            return;
        }
        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }
        event.setX(mc.player.motionX);
        event.setY(mc.player.motionY);
        event.setZ(mc.player.motionZ);
        if (!(phaseSetting.getValue().equals(PhaseMode.OFF)) && (phaseSetting.getValue().equals(PhaseMode.SEMI) || isPlayerCollisionBoundingBoxEmpty())) {
            mc.player.noClip = true;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            if (mc.player == null || mc.world == null) {
                setEnabled(false);
                return;
            }
            CPacketPlayer cPacketPlayer = (CPacketPlayer) event.getPacket();
            if (playerPackets.contains(cPacketPlayer)) {
                playerPackets.remove(cPacketPlayer);
                return;
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
            if (mc.player.isEntityAlive()) {
                TimeVec3d posVec;
                float dividedYaw = MathUtil.roundFloat(sPacketPlayerPosLook.getYaw() / 2, 2);
                float lastValidYaw = MathUtil.roundFloat(lastYaw, 2);

                debugVal1 = MathUtil.roundFloat(sPacketPlayerPosLook.getYaw(), 2);
                debugVal2 = dividedYaw;
                debugVal3 = lastValidYaw;

                boolean isNormalPacket = (mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) && !(mc.currentScreen instanceof GuiDownloadTerrain) && !(mode.getValue().equals(Mode.SETBACK)) && (posVec = (TimeVec3d) posLooks.remove(sPacketPlayerPosLook.getTeleportId())) != null && posVec.x == sPacketPlayerPosLook.getX() && posVec.y == sPacketPlayerPosLook.getY() && posVec.z == sPacketPlayerPosLook.getZ());
                // lol nice pay to win patch 0x22 and bullet
                // fluffy u should quit drugs ngl
                    /*frequencyTick++;
                    boolean isFrequencyPacket = (frequency.getValue() && sPacketPlayerPosLook.getYaw() == 0.0f);
                    if(isFrequencyPacket && !((posVec = (TimeVec3d) posLooks.remove(sPacketPlayerPosLook.getTeleportId())) != null && posVec.x == sPacketPlayerPosLook.getX() && posVec.y == sPacketPlayerPosLook.getY() && posVec.z == sPacketPlayerPosLook.getZ())) {
                        event.setCanceled(true);
                        return;
                    }*/
                if (isNormalPacket) {
                    event.setCanceled(true);
                    return;
                }
            }
            sPacketPlayerPosLook.yaw = (mc.player.rotationYaw);
            sPacketPlayerPosLook.pitch = (mc.player.rotationPitch);
            this.currentTeleportId = sPacketPlayerPosLook.getTeleportId();
            this.lastYaw = sPacketPlayerPosLook.getYaw();
        }
    }

    @SubscribeEvent
    public void onPush(PlayerSPPushOutOfBlocksEvent event) {
        event.setCanceled(true);
    }

    private boolean playerMoveForwardOrStrafing() {
        return ((mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0));
    }

    public static double[] getSpeed(double d) {
        double d2;
        double d3;
        MovementInput movementInput = mc.player.movementInput;
        double d4 = movementInput.moveForward;
        double d5 = movementInput.moveStrafe;
        float f = mc.player.rotationYaw;
        if (d4 == 0.0 && d5 == 0.0) {
            d2 = d3 = 0.0;
        } else {
            if (d4 != 0.0) {
                if (d5 > 0.0) {
                    f += (float) (d4 > 0.0 ? -45 : 45);
                } else if (d5 < 0.0) {
                    f += (float) (d4 > 0.0 ? 45 : -45);
                }
                d5 = 0.0;
                if (d4 > 0.0) {
                    d4 = 1.0;
                } else if (d4 < 0.0) {
                    d4 = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(f + 90.0f));
            final double sin = Math.sin(Math.toRadians(f + 90.0f));
            d3 = d4 * d * cos + d5 * d * sin;
            d2 = d4 * d * sin - d5 * d * cos;
        }
        return new double[]{d3, d2};
    }


    private void clearValues() {
        this.packetCounter = 0;
        this.bypassCounter = 0;
        this.currentTeleportId = 0;
        this.frequencyTick = 0;
        this.playerPackets.clear();
        this.posLooks.clear();
        this.delayedPacket = null;
    }

    private boolean isPlayerCollisionBoundingBoxEmpty() {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(0.0, 0.0, 0.0)).isEmpty();
    }

    private boolean iteratePacketCounterCheckMaxPackets(int n) {
        if (++this.packetCounter >= n) {
            this.packetCounter = 0;
            return true;
        }
        return false;
    }

    private void sendPackets(double d2, double d3, double d4, boolean b1) {
        if (delayedPacket != null) {
            mc.player.connection.sendPacket(delayedPacket);
            delayedPacket = null;
        }
        Vec3d vec3d = new Vec3d(d2, d3, d4);
        Vec3d vec3d2 = mc.player.getPositionVector().add(vec3d);
        Vec3d vec3d3 = outOfBoundsVec(vec3d, vec3d2);
        this.sendPlayerPacket(new CPacketPlayer.Position(vec3d2.x, vec3d2.y, vec3d2.z, mc.player.onGround));
        this.sendPlayerPacket(new CPacketPlayer.Position(vec3d3.x, vec3d3.y, vec3d3.z, mc.player.onGround));
        for (int i = 0; i < antiSetbackSpam.getValue(); i++) {
            this.sendPlayerPacket(new CPacketPlayer.Position(vec3d2.x, vec3d2.y, vec3d2.z, mc.player.onGround));
        }
        if (b1) {
            currentTeleportId++;
            this.posLooks.put(this.currentTeleportId, new TimeVec3d(vec3d2.x, vec3d2.y, vec3d2.z, System.currentTimeMillis()));
            if (delayConfirmTeleport.getValue()) {
                delayedPacket = new CPacketConfirmTeleport(this.currentTeleportId);
            } else {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.currentTeleportId));
            }
        }
    }

    private Vec3d outOfBoundsVec(final Vec3d offset, final Vec3d position) {
        return position.add(offset.x, getOffset(typeSetting.getValue()), offset.z);
    }


    private void sendPlayerPacket(CPacketPlayer cPacketPlayer) {
        this.playerPackets.add(cPacketPlayer);
        mc.player.connection.sendPacket(cPacketPlayer);
    }

    public double getOffset(Type type) {
        switch (type) {
            case UP:
                return 1337.0D;
            case DOWN:
                return -1337.0D;
            case PRESERVE:
                int n = ThreadLocalRandom.current().nextInt(29000000);
                if (ThreadLocalRandom.current().nextBoolean()) {
                    return n;
                }
                return -n;
            case LIMITJITTER:
                int j = ThreadLocalRandom.current().nextInt(22) + 70;
                if (ThreadLocalRandom.current().nextBoolean()) {
                    return j;
                }
                return -j;
        }
        return 0.0D;
    }

    public static class TimeVec3d extends Vec3d {
        private final long time;

        public TimeVec3d(double xIn, double yIn, double zIn, long time) {
            super(xIn, yIn, zIn);
            this.time = time;
        }

        public TimeVec3d(Vec3i vector, long time) {
            super(vector);
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }


    public enum Mode {
        SETBACK,
        FAST,
        FACTOR
    }

    public enum Type {
        UP,
        DOWN,
        PRESERVE,
        LIMITJITTER;
    }

    public enum PhaseMode {
        FULL,
        SEMI,
        OFF
    }

}
