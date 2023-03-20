package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.MobEffects;

public class SprintModule extends Module {

    public static SprintModule INSTANCE;

    public static Value<Mode> mode = new Value<>("Mode", Mode.LEGIT);

    private double previousDistance, motionSpeed;
    private int currentState = 1;

    public SprintModule() {
        super("Sprint", Category.MOVEMENT);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == mc.player) {
            switch (mode.getValue()) {
                case RAGE:
                    if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) && !(mc.player.isSneaking() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6f)) {
                        mc.player.setSprinting(true);
                    }
                    break;
                case LEGIT:
                    if (mc.gameSettings.keyBindForward.isKeyDown() && !(mc.player.isSneaking() || mc.player.isHandActive() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6f) && mc.currentScreen == null) {
                        mc.player.setSprinting(true);
                    }
                    break;

                case STRAFE:
                    if (!(mc.player.isSneaking() || mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) || !mc.player.STRAFE) {
                        MovementInput movementInput = mc.player.movementInput;
                        float moveForward = movementInput.moveForward;
                        float moveStrafe = movementInput.moveStrafe;
                        float rotationYaw = mc.player.rotationYaw;
                        if ((moveForward == 0.0 && moveStrafe == 0.0)) {
                            event.setMotionX(0.0);
                            event.setMotionZ(0.0);
                        } else {
                            if (moveForward != 0.0) {
                                if (moveStrafe > 0.0) {
                                    rotationYaw += (moveForward > 0.0 ? -45 : 45);
                                } else if (moveStrafe < 0.0) {
                                    rotationYaw += moveForward > 0.0 ? 45 : -45;
                                }
                                moveStrafe = 0.0f;
                            }
                            moveStrafe = moveStrafe == 0.0f ? moveStrafe : (moveStrafe > 0.0 ? 1.0f : -1.0f);
                            final double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
                            final double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
                            event.setMotionX(moveForward * EntityUtil.getMaxSpeed() * cos + moveStrafe * EntityUtil.getMaxSpeed() * sin);
                            event.setMotionZ(moveForward * EntityUtil.getMaxSpeed() * sin - moveStrafe * EntityUtil.getMaxSpeed() * cos);
                        }
                    }
            }
        }
    }


            public enum Mode {
                LEGIT, RAGE, STRAFE
            }

}
