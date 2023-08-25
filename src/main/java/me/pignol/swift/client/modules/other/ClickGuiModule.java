package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class ClickGuiModule extends Module {

    public static final ClickGuiModule INSTANCE = new ClickGuiModule();

    public final Value<Boolean> moduleButtons = new Value<>("ModuleButtons", true);
    public final Value<Boolean> moduleDisabledButtons = new Value<>("DisabledModuleButtons", true);
    public final Value<Integer> red = new Value<>("Red", 63, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 18, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 153, 0, 255);
    public final Value<Integer> alpha = new Value<>("Alpha", 173, 0, 255);
    public final Value<Integer> disabledRed = new Value<>("DisabledRed", 108, 0, 255);
    public final Value<Integer> disabledGreen = new Value<>("DisabledGreen", 60, 0, 255);
    public final Value<Integer> disabledBlue = new Value<>("DisabledBlue", 188, 0, 255);
    public final Value<Integer> disabledAlpha = new Value<>("DisabledAlpha", 68, 0, 255);
    public final Value<Integer> lineAlpha = new Value<>("LineAlpha", 80, 0, 255);
    public final Value<Integer> hoverAlpha = new Value<>("HoverAlpha", 80, 0, 255);
    public final Value<Integer> categoryRed = new Value<>("CategoryRed", 63, 0, 255);
    public final Value<Integer> categoryGreen = new Value<>("CategoryGreen", 18, 0, 255);
    public final Value<Integer> categoryBlue = new Value<>("CategoryBlue", 152, 0, 255);
    public final Value<Integer> categoryAlpha = new Value<>("CategoryAlpha", 255, 0, 255);
    public final Value<Integer> backgroundRed = new Value<>("BackgroundRed", 43, 0, 255);
    public final Value<Integer> backgroundGreen = new Value<>("BackgroundGreen", 23, 0, 255);
    public final Value<Integer> backgroundBlue = new Value<>("BackgroundBlue", 80, 0, 255);
    public final Value<Integer> backgroundAlpha = new Value<>("BackgroundAlpha", 133, 0, 255);

    public final Value<Integer> textEnabledRed = new Value<>("TextEnabledRed", 255, 0, 255);
    public final Value<Integer> textEnabledGreen = new Value<>("TextEnabledGreen", 255, 0, 255);
    public final Value<Integer> textEnabledBlue = new Value<>("TextEnabledBlue", 255, 0, 255);

    public final Value<Integer> textDisabledRed = new Value<>("TextDisabledRed", 255, 0, 255);
    public final Value<Integer> textDisabledGreen = new Value<>("TextDisabledGreen", 255, 0, 255);
    public final Value<Integer> textDisabledBlue = new Value<>("TextDisabledBlue", 255, 0, 255);

    public final Value<Boolean> closeSettings = new Value<>("CloseSettings", true);
    public final Value<Boolean> customFont = new Value<>("CustomFont", true);

    public ClickGuiModule() {
        super("ClickGUI", Category.OTHER, false, false);
        setDrawn(false);
        new ClickGUI();
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(ClickGUI.getInstance());
    }

}
