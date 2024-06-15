package xyz.plumc.packetlimit.utils;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.plumc.packetlimit.config.LimitType;

import java.util.UUID;

public class PlayerUtils {
    public static void sendLimitActionBar(ServerPlayer player, LimitType type, int count){
        String text = "§c你已在%ds内触发了%d次警告，若再触发%d次，你将会被踢出服务器！".formatted((int)type.liveTime/1000L, count, type.kickLimit-count);
        ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(new TextComponent(text));
        player.connection.send(packet);
        sendAdminLog("§7[PacketLimits: 玩家 %s 触发了 %s 警告 %d 次]".formatted(player.getGameProfile().getName(), type.name, count));
    }
    public static void sendLimitTitle(ServerPlayer player, LimitType type, int warningWaiting){
        String text = "§c速率限制，当前的 §f%s §c已超过最大速率！- §f%s §cs".formatted(type.message, String.valueOf((type.waringWaiting/1000L)-warningWaiting-1));
        ClientboundSetTitleTextPacket packet = new ClientboundSetTitleTextPacket(new TextComponent(text));
        player.connection.send(packet);
    }

    public static void sendLimitSubTitle(ServerPlayer player, LimitType type, float rate){
        String text = "§c当前速率：%s §f/ §a%s".formatted(String.valueOf(Math.round(rate*type.scale*100.0) / 100.0), String.valueOf(Math.round(type.maxRate*type.scale*100.0) / 100.0));
        ClientboundSetSubtitleTextPacket packet = new ClientboundSetSubtitleTextPacket(new TextComponent(text));
        player.connection.send(packet);
    }

    public static void sendKick(ServerPlayer player, LimitType type){
        String text = "§6[PacketLimit] §c你因为 §f%s §c被踢出游戏！".formatted(type.message);
        player.connection.disconnect(new TextComponent(text));
        sendAdminLog("§7[PacketLimits: 玩家 %s 因为 %s 被踢出游戏]".formatted(player.getGameProfile().getName(), type.name));
    }

    public static void sendBan(MinecraftServer server, ServerPlayer player, LimitType type){
        String text = "§6[PacketLimit] §c你因为 §f%s §c被此服务器封禁！".formatted(type.message);
        server.getPlayerList().getBans().add(new UserBanListEntry(player.getGameProfile(), null, null, null, text.substring(15)));
        player.connection.disconnect(new TextComponent(text));
        sendAdminLog("§7[PacketLimits: 玩家 %s 因为 %s 被服务器封禁]".formatted(player.getGameProfile().getName(), type.name));
    }

    public static void sendAdminLog(String message){
        for (ServerPlayer serverPlayer: ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            if (serverPlayer.hasPermissions(4)){
                serverPlayer.sendMessage(new TextComponent(message), UUID.randomUUID());
            }
        }
    }

    public static boolean isOp(UUID player){
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(player);
        return serverPlayer != null && server.getPlayerList().isOp(serverPlayer.getGameProfile());
    }
}
