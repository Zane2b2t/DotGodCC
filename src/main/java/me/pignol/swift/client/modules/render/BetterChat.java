package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class BetterChat extends Module {
    public BetterChat() {
        super("BetterChat", Category.COMBAT);
    }
    public void onEnable() {
        ChatUtil.sendMessage("FakeModule");
    }
}
