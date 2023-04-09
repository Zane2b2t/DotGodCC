package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.misc.NameProtectModule;
import me.pignol.swift.client.modules.render.DickheadESP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {

    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);

    @Redirect(method = {"renderString(Ljava/lang/String;FFIZ)I"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer renderer, String text, boolean shadow) {
            if (DickheadESP.INSTANCE.isEnabled() && DickheadESP.INSTANCE.changeOwn.getValue().booleanValue()) {
                this.renderStringAtPos(text.replace(DickheadESP.INSTANCE.enemyName.getValue(), DickheadESP.INSTANCE.enemyNewName.getValue().toString()), shadow);
            } else {
                this.renderStringAtPos(text, shadow);
            }
        }


    @ModifyVariable(method = "renderString", at = @At("HEAD"), require = 1, ordinal = 0)
    private String renderString(final String string) {
        if (string == null)
            return null;

        if (NameProtectModule.INSTANCE.isEnabled()) {
            return StringUtils.replace(string, Minecraft.getMinecraft().getSession().getUsername(), NameProtectModule.INSTANCE.getFakeName());
        }
        return string;
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), require = 1, ordinal = 0)
    private String getStringWidth(final String string) {
        if (string == null)
            return null;
        if (NameProtectModule.INSTANCE.isEnabled()) {
            return StringUtils.replace(string, Minecraft.getMinecraft().getSession().getUsername(), NameProtectModule.INSTANCE.getFakeName());
        }
        return string;
    }



}
