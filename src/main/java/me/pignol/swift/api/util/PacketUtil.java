package me.pignol.swift.api.util;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.pignol.swift.api.util.MC;
import net.minecraft.network.Packet;

public class PacketUtil implements MC {
    public static boolean noEvent;

    public static void invoke(Packet<?> packet) {
        if (mc.getConnection() != null) {
            mc.getConnection().getNetworkManager().channel().writeAndFlush(packet);
        }
    }



    public static void invokeNoEvent(Packet<?> packet) {
        if (mc.getConnection() != null) {
            noEvent = true;
            mc.getConnection().getNetworkManager().channel().writeAndFlush(packet);
            noEvent = false;
        }
    }


}