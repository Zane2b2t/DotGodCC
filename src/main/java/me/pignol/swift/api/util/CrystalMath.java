package me.pignol.swift.api.util;

import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.interfaces.Globals;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.stream.Collectors;

public class CrystalMath
        implements Globals {
    public static void placeCrystalOnBlocks(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand, boolean silent) {
        RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        int old = BlockUtil.mc.player.inventory.currentItem;
        int crystal = ItemUtil.getItemHotbar(Items.END_CRYSTAL);
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != BlockUtil.mc.player.inventory.currentItem) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(crystal));
        }
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        if (hand == EnumHand.MAIN_HAND && silent && crystal != -1 && crystal != BlockUtil.mc.player.inventory.currentItem) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(old));
        }
        if (swing) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange) {
        NonNullList positions = NonNullList.create();
        positions.addAll(BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(BlockUtil::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> possiblePlacePositions2(float placeRange, boolean specialEntityCheck, boolean oneDot15, boolean cc) {
        NonNullList positions = NonNullList.create();
        positions.addAll(BlockUtil.getSphere(EntityUtil.getPlayerPos(BlockUtil.mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> BlockUtil.canPlaceCrystals(pos, specialEntityCheck, oneDot15, cc)).collect(Collectors.toList()));
        return positions;
    }
}
