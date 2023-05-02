package me.pignol.swift.client.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.util.text.TextColor;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.api.value.Value;

public class DickheadESP
        extends Module {

    public static DickheadESP INSTANCE = new DickheadESP();
    public Value<Boolean> changeOwn = (new Value<Boolean>("NameChange", false));
    public final Value<String> enemyName = (new Value<String>("Name To Change", "Name to change..."));
    public final Value<String> enemyNewName = (new Value<String>("Name", "New Name Here..."));
    public final Value<Boolean> color = (new Value<Boolean>("Colored", false));
    public final Value<TextColor.Color> nameColor = new Value<>("NameColor", TextColor.Color.WHITE, v -> color.getValue());
    private static DickheadESP instance;

    public DickheadESP() {
        super("DickheadESP", Category.RENDER);
        instance = this;
    }

    @Override
    public void onEnable() {
        ChatUtil.sendMessage((Object)ChatFormatting.GREEN + "Success!" + (Object)ChatFormatting.BLUE + " Name succesfully changed to " + (Object)ChatFormatting.GREEN + this.enemyNewName.getValue());
    }

  //  public static boolean getInstance() {
      //  if (instance == null) {
        //    instance = new DickheadESP();
        //}

      //  return instance;
  //  }

    public static String getPlayerName() {
        if (!(!DickheadESP.instance.isEnabled() && !Starlink.getInstance().isConnected())) {
            return mc.getSession().getUsername();

    }
        String name = Starlink.getInstance().getPlayerName();
        if (!(name != null && !name.isEmpty())) {
            return mc.getSession().getUsername();
        }
        return name;
    }
}