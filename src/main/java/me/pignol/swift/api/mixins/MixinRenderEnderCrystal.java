package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.other.ManageModule;
import me.pignol.swift.client.modules.render.Chams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.pignol.swift.api.interfaces.Globals.mc;
import static org.lwjgl.opengl.GL11.*;

@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Redirect(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void doRender(ModelBase instance, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (Chams.INSTANCE.isEnabled()) {
            boolean chams = Chams.INSTANCE.crystals.getValue();
            GL11.glScalef(Chams.INSTANCE.crystalScale.getValue(), Chams.INSTANCE.crystalScale.getValue(), Chams.INSTANCE.crystalScale.getValue());
            if (chams) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(Chams.INSTANCE.red.getValue() / 255.0F, Chams.INSTANCE.green.getValue() / 255.0F, Chams.INSTANCE.blue.getValue() / 255.0F, Chams.INSTANCE.alpha.getValue() / 255.0F);
            }
          //  if (Chams.glint.getValue()) {
               // GL11.glPushMatrix();
               // GL11.glPushAttrib(1048575);
              //  GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
              //  GL11.glDisable(GL11.GL_LIGHTING);
              //  GL11.glDepthRange( 0, 0.1 );
              //  GL11.glEnable(GL11.GL_BLEND);
              //  GL11.glColor4f(Chams.INSTANCE.red.getValue() / 255.0F, Chams.INSTANCE.green.getValue() / 255.0F, Chams.INSTANCE.blue.getValue() / 255.0F, Chams.INSTANCE.alpha.getValue() / 255.0F);

              //  GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);

             //   mc.getTextureManager().bindTexture(RES_ITEM_GLINT);


              //  GL11.glDisable(GL11.GL_BLEND);
               // GL11.glDepthRange( 0, 1 );
               // GL11.glEnable(GL11.GL_LIGHTING);

             //   GL11.glPopAttrib();
             //   GL11.glPopMatrix();
          //  }
            instance.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (chams) {
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }

            //Glint
            if (Chams.cGlint.getValue() && entityIn instanceof EntityEnderCrystal) {


                glPushMatrix();
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                glDisable(GL_LIGHTING);

                glDepthRange(0, 0.1);

                glEnable(GL_BLEND);

                GL11.glColor4f(Chams.INSTANCE.cgRed.getValue() / 255.0F, Chams.INSTANCE.cgGreen.getValue() / 255.0F, Chams.INSTANCE.cgBlue.getValue() / 255.0F, Chams.INSTANCE.cgAlpha.getValue() / 255.0F);

                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);

                float f = (float) entityIn.ticksExisted + mc.getRenderPartialTicks();

                mc.getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));

                for (int i = 0; i < 2; ++i) {
                    GlStateManager.matrixMode(GL_TEXTURE);

                    GlStateManager.loadIdentity();

                    glScalef(8.0f, 0.5f, 8.0f);
                    GlStateManager.rotate(30.0f - i * 60.0f, 0.0f, 0.0f, 1.0f);
                    GlStateManager.translate(0.0F, f * (0.001F + (float) i * 0.003F) * 20.0F, 0.0F);

                    GlStateManager.matrixMode(GL_MODELVIEW);

                    instance.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }

                GlStateManager.matrixMode(GL_TEXTURE);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(GL_MODELVIEW);

                glDisable(GL_BLEND);

                glDepthRange(0, 1);

                glEnable(GL_LIGHTING);

                glPopAttrib();
                glPopMatrix();
                if (!ManageModule.INSTANCE.renderGlintOnce.getValue()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0F, 8.0F, 8.0F);
                    float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
                    GlStateManager.translate(-f1, 0.0F, 0.0F);
                    GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.popMatrix();
                }
            }


                if (Chams.cShine.getValue() && entityIn instanceof EntityEnderCrystal) {
                    Chams.mc.getTextureManager().bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
                    GL11.glEnable(3553);
                    GL11.glBlendFunc(768, 771);
                    instance.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3553);
                }


            GL11.glScalef(1.0F, 1.0F, 1.0F);
        } else {
            instance.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

}
