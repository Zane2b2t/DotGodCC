package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {

    @Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
    public void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo ci) {
        if (NoRenderModule.INSTANCE.isEnabled()) {
            if (shouldCancel(slotIn)) {
                ci.cancel();
            }
        }
    }

    private boolean shouldCancel(EntityEquipmentSlot slot) {
        switch (slot) {
            case FEET:
                return NoRenderModule.INSTANCE.armor.getValue();
            case LEGS:
                return NoRenderModule.INSTANCE.armor.getValue();
            case CHEST:
                return NoRenderModule.INSTANCE.armor.getValue();
            case HEAD:
                return NoRenderModule.INSTANCE.armor.getValue();
            default:
                return false;
        }
    }

}
