package xyz.plumc.packetlimit.utils;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class PacketUtils {
    public static ServerPlayer getPlayer(Object connection, MinecraftServer server){
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (player.connection.getConnection() == (Connection)connection) {
                return player;
            }
        }
        return null;
    }
}
