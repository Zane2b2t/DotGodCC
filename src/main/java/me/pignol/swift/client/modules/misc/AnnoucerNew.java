package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.BreakBlockEvent;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.MathUtil;

import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;
import java.util.Random;

public class AnnoucerNew extends Module {

    private final Value<Boolean> move = new Value<>("move", true);
    private final Value<Boolean> breakBlock = new Value<>("Break", false);
    private final Value<Boolean> eat = new Value<>("Eat", false);

    private final Value<Double> delay = new Value<>("Delay", 50d, 1d, 3000d);

    private double lastPositionX;
    private double lastPositionY;
    private double lastPositionZ;

    private int eaten;

    private int broken;
    int waitCounter;

    private final StopWatch delayTimer = new StopWatch();

    public AnnoucerNew() { super("AnnouncerTest", Category.MISC);}

    @Override
    public void onEnable() {
        eaten = 0;
        broken = 0;

        delayTimer.reset();
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent Event) {
        if (waitCounter < delay.getValue() * 100) {
            waitCounter++;
            return;
        } else {
            waitCounter = 0;
        }
        if (fullNullCheck() || !spawnCheck()) return;

        double traveledX = lastPositionX - mc.player.lastTickPosX;
        double traveledY = lastPositionY - mc.player.lastTickPosY;
        double traveledZ = lastPositionZ - mc.player.lastTickPosZ;

        double traveledDistance = Math.sqrt(traveledX * traveledX + traveledY * traveledY + traveledZ * traveledZ);

        if (move.getValue()
                && traveledDistance >= 1
                && traveledDistance <= 1000
                && delayTimer.passedS(delay.getValue())) {

            mc.player.sendChatMessage(getWalkMessage()
                    .replace("{blocks}", new DecimalFormat("0.00").format(traveledDistance)));

            lastPositionX = mc.player.lastTickPosX;
            lastPositionY = mc.player.lastTickPosY;
            lastPositionZ = mc.player.lastTickPosZ;


        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent.Finish event) {
        if (fullNullCheck() || !spawnCheck()) return;

        int random = MathUtil.randomBetween(1, 6);

        if (eat.getValue()
                && event.getEntity() == mc.player
                && event.getItem().getItem() instanceof ItemFood
                || event.getItem().getItem() instanceof ItemAppleGold) {

            ++eaten;

            if (eaten >= random && delayTimer.passedS(delay.getValue())) {

                mc.player.sendChatMessage(getEatMessage()
                        .replace("{amount}", "" + eaten)
                        .replace("{name}", "" + event.getItem().getDisplayName()));

                eaten = 0;

                delayTimer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onBreakBlock(BreakBlockEvent event) {
        if (fullNullCheck() || !spawnCheck()) return;

        int random = MathUtil.randomBetween(1, 6);

        ++broken;

        if (breakBlock.getValue()
                && broken >= random
                && delayTimer.passedS(delay.getValue())) {

            mc.player.sendChatMessage(getBreakMessage()
                    .replace("{amount}", "" + broken)
                    .replace("{name}", "" + BlockUtil.getBlock(event.getPos()).getLocalizedName()));

            broken = 0;

            delayTimer.reset();
        }
    }

    private String getWalkMessage() {

        String[] walkMessage = {
                "i just flew {blocks} meters like a Bullet coming out of the INTERVATION thanks to AscendancyV4!"

        };

        return walkMessage[new Random().nextInt(walkMessage.length)];
    }

    private String getBreakMessage() {

        String[] breakMessage = {
                "I just destroyed {amount} {name} using INTERVATION thanks to AscendancyV4!"

        };

        return breakMessage[new Random().nextInt(breakMessage.length)];
    }

    private String getEatMessage() {

        String[] eatMessage = {
                "I just ate {amount} {name} like a fat iReapZz hater!!!"

        };

        return eatMessage[new Random().nextInt(eatMessage.length)];
    }
}
