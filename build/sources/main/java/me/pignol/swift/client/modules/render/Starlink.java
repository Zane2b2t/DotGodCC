package me.pignol.swift.client.modules.render;

import java.util.concurrent.atomic.AtomicBoolean;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Starlink
        extends Module {
    public Value<Boolean> getName = (new Value<Boolean>("GetName", true));
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private static Starlink instance;
    private StringBuffer name = null;

    public Starlink() {
        super("Starlink", Category.RENDER);
        instance = this;
    }

    public static Starlink getInstance() {
        if (instance == null) {
            instance = new Starlink();
        }
        return instance;
    }

    public String getPlayerName() {
        if (this.name == null) {
            return null;
        }
        return this.name.toString();
    }

    public boolean isConnected() {
        return this.connected.get();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.getConnection() != null && this.isConnected() && this.getName.getValue().booleanValue()) {
            this.getName.setValue(false);
        }
    }
}
