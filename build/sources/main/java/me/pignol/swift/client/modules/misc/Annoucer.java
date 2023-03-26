package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
public class Annoucer extends Module {

    public static Annoucer INSTANCE;
    int waitCounter;


   private final Value<Float> delay = new Value<>("Delay", 1F, 0.1F, 15.0F, 0.1F);

    public Annoucer() {
        super("Annoucer", Category.MISC);
    }


    @SubscribeEvent
    public void onUpdate(UpdateEvent event){
        if (waitCounter < delay.getValue() * 100) {
            waitCounter++;
            return;
        } else {
            waitCounter = 0;
        }
        double randomNum = ThreadLocalRandom.current().nextDouble(1.0, 200.0);

        mc.player.sendChatMessage("I just flew " + new DecimalFormat("0.##").format(randomNum) + " meters like a butterfly thanks to DotGod.CC!");
    }
}