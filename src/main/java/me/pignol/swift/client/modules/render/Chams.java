package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;

import org.lwjgl.opengl.GL11;

import static me.pignol.swift.api.interfaces.Globals.mc;


public class Chams extends Module {

    public static final Chams INSTANCE = new Chams();

    public static final Value<Boolean> players = new Value<>("Players", true);
    public static final Value<Boolean> crystals = new Value<>("Crystals", true);
    public static final Value<Boolean> glint = new Value<>("Glints", false);
    public static final Value<Boolean> cGlint = new Value<>("CGlint", false, v -> glint.getValue() && crystals.getValue());
    public static final Value<Boolean> pGlint = new Value<>("PGLINT", true, v -> glint.getValue() && players.getValue());
    public static final Value<Boolean> shine = new Value<>("Shines", false);
    public static final Value<Boolean> cShine = new Value<>("CShine", false, v -> shine.getValue());
    public static final Value<Boolean> pShine = new Value<>("PShine", false, v -> shine.getValue());
    public static Minecraft mc;

    public final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    public final Value<Integer> cgRed = new Value<>("GlintRed", 255, 0, 255, v -> glint.getValue());
    public final Value<Integer> cgGreen = new Value<>("GlintGreen", 255, 0 ,255, v -> glint.getValue());
    public final Value<Integer> cgBlue = new Value<>("GlintBlue", 255, 0, 255, v -> glint.getValue());
    public final Value<Integer> cgAlpha = new Value<>("CGAlpha", 80, 0, 255, v -> glint.getValue());
    public final Value<Integer> alpha = new Value<>("Alpha", 100, 0, 255);

    public final Value<Float> crystalScale = new Value<>("CrystalScale", 1.0F, 0.1F, 1.0F, 0.05F);

    public Chams() {
        super("Chams", Category.RENDER);
    }




}