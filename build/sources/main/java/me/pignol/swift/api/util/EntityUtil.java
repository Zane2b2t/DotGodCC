package me.pignol.swift.api.util;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.api.mixins.IEntityLivingBase;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

import java.util.Objects;
import java.util.List;

public class EntityUtil implements Globals {

    public static boolean isInLiquid(final Entity entity) {
        return entity.isInLava() || entity.isInWater();
    }

    public static EntityPlayer getClosestPlayer(double range) {
        return getClosestPlayer(range, false, mc.world.playerEntities);
    }

    public static EntityPlayer getClosestPlayer(double range, boolean unsafe) {
        return getClosestPlayer(range, unsafe, mc.world.playerEntities);
    }
    

    public static EntityPlayer getClosestPlayer(double range, final boolean unsafe, final List<EntityPlayer> players) {
        range *= range;
        EntityPlayer target = null;

        double maxDistance = 100000.0D;
        final int size = players.size();
        for (int i = 0; i < size; i++) {
            final EntityPlayer player = players.get(i);
            if (player != mc.player) {
                final double distance = player.getDistanceSq(mc.player);
                if (distance < maxDistance && range > distance && !isDead(player) && !FriendManager.getInstance().isFriend(player.getName()) && player.getEntityId() != -69) {
                    if (unsafe && BlockUtil.isSafeFast(player)) {
                        continue;
                    }
                    maxDistance = distance;
                    target = player;
                }
            }
        }

        if (unsafe && target == null) {
            return getClosestPlayer(range, false);
        }

        return target;
    }

    public static EntityPlayer getClosestEnemy(double x, double y, double z, List<EntityPlayer> list) {
        EntityPlayer closest = null;
        double distance = 10000.0f;

        for (EntityPlayer player : list) {
            if (player != null) {
                double dist = player.getDistance(x, y, z);
                if (!EntityUtil.isDead(player) && dist < distance && !FriendManager.getInstance().isFriend(player.getName())) {
                    distance = dist;
                    closest = player;
                }
            }
        }

        return closest;
    }
    public static double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            maxModifier *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }


    public static double getBaseMotionSpeed() {
        double event = 0.272;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int var3 = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            event *= 1.0 + 0.2 * var3;
        }
        return event;
    }

    public static EntityPlayer getClosestPlayer(final List<EntityPlayer> players) {
        EntityPlayer target = null;

        double maxDistance = 999.0D;
        final int size = players.size();
        for (int i = 0; i < size; i++) {
            final EntityPlayer player = players.get(i);
            if (player != mc.player) {
                final double distance = player.getDistanceSq(mc.player);
                if (!isDead(player) && !FriendManager.getInstance().isFriend(player.getName())) {
                    if (distance < maxDistance) {
                        maxDistance = distance;
                        target = player;
                    }
                }
            }
        }

        return target;
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }

        if (swingArm) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    
       public static void OffhandAttack(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            EntityUtil.mc.player.connection.sendPacket((Packet) new CPacketUseEntity(entity));
        } else {
            EntityUtil.mc.playerController.attackEntity((EntityPlayer) EntityUtil.mc.player, entity);
        }
        if (swingArm) {
            EntityUtil.mc.player.swingArm(EnumHand.OFF_HAND);
        }
    }
    
        public static void swingArmNoPacket(EnumHand hand, EntityLivingBase entity) {
        ItemStack stack = entity.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem().onEntitySwing(entity, stack)) {
            return;
        }
        if (!entity.isSwingInProgress || entity.swingProgressInt >= ((IEntityLivingBase) entity).getArmSwingAnimationEnd() / 2 || entity.swingProgressInt < 0) {
            entity.swingProgressInt = -1;
            entity.isSwingInProgress = true;
            entity.swingingHand = hand;
        }
    }

    public static boolean isDead(final Entity entity) {
        return entity.isDead || entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0.0F;
    }

    public static boolean isntDead(final Entity entity) {
        return !isDead(entity);
    }

    public static int getPing(final EntityPlayer player) {
        if (player != null && mc.player.connection != null) {
            final NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(player.getUniqueID());
            if (info != null) { //dont listen to intellij this can actually be null
                return info.getResponseTime();
            }
        }
        return -1;
    }

    public static boolean isPlayerValid(final EntityPlayer player, final double range) {
        return player != null && player != mc.player && mc.player.getDistanceSq(player) < range * range && !isDead(player) && !FriendManager.getInstance().isFriend(player.getName());
    }

    public static float getHealth(final EntityLivingBase entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

}
