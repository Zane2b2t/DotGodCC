package me.pignol.swift.client.event.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class BreakBlockEvent extends Event {

    BlockPos pos;

    public BreakBlockEvent(BlockPos blockPos){
        super();
        pos = blockPos;
    }

    public BlockPos getPos(){
        return pos;
    }

    public void setPos(BlockPos pos){
        this.pos = pos;
    }
}
