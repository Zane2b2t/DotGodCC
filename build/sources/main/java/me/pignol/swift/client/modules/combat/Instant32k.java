package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class Instant32k extends Module {

    public Instant32k() {
        super("Instant32k", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        ChatUtil.sendMessage("Sorry bout dat");
        setEnabled(false);
    }

}
