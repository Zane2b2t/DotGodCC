package me.pignol.swift.client.event.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;


public class PacketEvent extends Event {

    private static Packet<?> packet = null;

    public PacketEvent(int stage, Packet<?> packet) {
        super();
        this.packet = packet;
    }

    public PacketEvent(Packet<?> packet) {

        this.packet = packet;
    }

    public PacketEvent() {

    }

    public Packet<?> getPacket() {
        return packet;
    }

    @Cancelable
    public static class Receive extends PacketEvent {

        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }

        public Receive(Packet<?> packetIn) {
            super(packet);
        }
    }

    @Cancelable
    public static class Send extends PacketEvent {

        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }

        public Send(Packet<?> packetIn) {
            super();
        }

        public int getStage() {
            return 0;
        }

    }


}
