package me.pignol.swift.client.event.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventPacketReceive extends Event
{
    private final Packet<?> packet;

    public EventPacketReceive(final Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}