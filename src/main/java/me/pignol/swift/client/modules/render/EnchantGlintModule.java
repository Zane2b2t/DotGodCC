package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class  EnchantGlintModule extends Module {

    public static EnchantGlintModule INSTANCE = new EnchantGlintModule();

    public final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    public static final Value<Float> glintScale = new Value<>("GlintSize", 0.1f, 5.0f, 25.0f);
    public static final Value<Float> glintSpeed = new Value<>("GlintSpeed", 0.1f, 1.0f, 15.0f);
    public final Value<Boolean> rainbow = new Value<>("Rainbow", false);

    public EnchantGlintModule() {
        super("GlintModify", Category.RENDER, false, false);
    }

}
