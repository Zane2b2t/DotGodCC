package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class AntiHitbox extends Module {
    public AntiHitbox() {
        super ("AntiHitbox", Category.COMBAT);
    }
    public void onEnable() {
        ChatUtil.sendMessage("FakeModule");
    }
}
