package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import me.pignol.swift.client.modules.combat.AutoCrystalElite;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
public class Annoucer extends Module {

    public static Annoucer INSTANCE;
    int waitCounter;


   private final Value<Float> delay = new Value<>("Delay", 1F, 0.1F, 15.0F, 0.1F);
   private final Value<Boolean> test = new Value<>("Test", false);
    public Value<Name> name = new Value<>("ClientName", Name.Ascendancy);
    public Value<Language> language = new Value<>("Language", Language.English);

    private boolean condition;
    private double lastPositionX;
    private double lastPositionY;
    private double lastPositionZ;

    public Annoucer() {
        super("Annoucer", Category.MISC);
    }


    @SubscribeEvent
    public void onUpdate(UpdateEvent event){
        if  (mc.player.movementInput.moveForward == 0.0f || mc.player.movementInput.moveStrafe == 0.0f) {
            condition = false;
            return;
        }
        else {
            condition = true;
        }
        if (waitCounter < delay.getValue() * 100) {
            waitCounter++;
            return;
        } else {
            waitCounter = 0;
        }
        double traveledX = lastPositionX - mc.player.lastTickPosX;
        double traveledY = lastPositionY - mc.player.lastTickPosY;
        double traveledZ = lastPositionZ - mc.player.lastTickPosZ;

        double traveledDistance = Math.sqrt(traveledX * traveledX + traveledY * traveledY + traveledZ * traveledZ);
        if (test.getValue()) {
            mc.player.sendChatMessage("I just flew " + new DecimalFormat("0.00").format(traveledDistance) + " meters like a Butterfly thanks to DotGod.CC!");
        }
        double randomNum = ThreadLocalRandom.current().nextDouble(1.0, 200.0);
    if (condition && name.getValue() == Name.Ascendancy && language.getValue() == Language.English) { //the language thingy can be made using less space but i cba to use brain. it's just a message sending module
        mc.player.sendChatMessage("I just flew " + new DecimalFormat("0.##").format(randomNum) + " meters like a bullet coming out of the intervention thanks to ASCENDANCY V3!");
    }

    else if (condition && name.getValue() == Name.DotGodCC && language.getValue() == Language.Spanish) {
        mc.player.sendChatMessage("Acabo de valor " + new DecimalFormat("0.##").format(randomNum) + " meters like a bullet coming out of the intervention thanks to ASCENDANCY V3!");
    }
    else if (condition && name.getValue() == Name.DotGodCC && language.getValue() == Language.English) {
        mc.player.sendChatMessage("I just flew " + new DecimalFormat("0.##").format(randomNum) + " metros como una mariposa gracias a DotGod.CC");
    }
    }
    public enum Name {
        Ascendancy,
        DotGodCC
    }
    public enum Language {
        English,
        Spanish,
        Finnish,
        German
    }
}
