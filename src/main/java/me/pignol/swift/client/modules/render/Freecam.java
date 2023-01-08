package me.pignol.swift.client.modules.render;

import com.mojang.authlib.GameProfile;
import me.pignol.swift.api.util.MovementUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.MoveEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam extends Module {
    public static Freecam INSTANCE;

    private final Value<Float> speed = new Value<>("Speed", 1f, 0.1f, 5.0f);
    private final Value<Boolean> cancelPackets = new Value<>("CancelPackets", true);

    private Entity riding;
    private EntityOtherPlayerMP cam;
    private Vec3d position;
    private float yaw = -1;
    private float pitch = -1;

    public Freecam() {
        super("Freecam", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (mc.world == null) {
            return;
        }
        riding = null;

        if (mc.player.getRidingEntity() != null) {
            this.riding = mc.player.getRidingEntity();
            mc.player.dismountRidingEntity();
        }

        cam = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), mc.player.getCommandSenderEntity().getName()));
        cam.copyLocationAndAnglesFrom(mc.player);
        cam.prevRotationYaw = mc.player.rotationYaw;
        cam.rotationYawHead = mc.player.rotationYawHead;
        cam.inventory.copyInventory(mc.player.inventory);
        mc.world.addEntityToWorld(-69, cam);

        this.position = mc.player.getPositionVector();
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;

        mc.player.noClip = true;
    }

    @Override
    public void onDisable() {
        if (mc.world != null) {
            if (this.riding != null) {
                mc.player.startRiding(this.riding, true);
                riding = null;
            }
            if (this.cam != null) {
                mc.world.removeEntity(this.cam);
            }
            if (this.position != null) {
                mc.player.setPosition(this.position.x, this.position.y, this.position.z);
            }
            if(this.yaw != -1) {
                mc.player.rotationYaw = this.yaw;
            }
            if(this.pitch != -1) {
                mc.player.rotationPitch = this.pitch;
            }
            mc.player.noClip = false;
            mc.player.setVelocity(0, 0, 0);
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if(mc.player == null) {
            return;
        }
        mc.player.noClip = true;
    }

    @SubscribeEvent
    public void onClientUpdate(UpdateEvent event) {
        if(mc.player == null || mc.world == null) {
            return;
        }
        if(mc.world.getEntityByID(-69) != null) {
            Entity ent = mc.world.getEntityByID(-69);
            if(ent instanceof EntityOtherPlayerMP) {
                EntityOtherPlayerMP entityOther = (EntityOtherPlayerMP) ent;
                entityOther.setHealth(mc.player.getHealth());
                entityOther.setAbsorptionAmount(mc.player.getAbsorptionAmount());
            }
        }
        mc.player.noClip = true;

        mc.player.setVelocity(0, 0, 0);

        final double[] dir = MovementUtil.strafe(this.speed.getValue());

        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        }
        else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        mc.player.setSprinting(false);
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += speed.getValue();
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY -= speed.getValue();
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() == mc.player) {
            this.toggle();
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if(!cancelPackets.getValue()) {
            return;
        }

        if(event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) {
            event.setCanceled(true);
        }
    }
    
}
