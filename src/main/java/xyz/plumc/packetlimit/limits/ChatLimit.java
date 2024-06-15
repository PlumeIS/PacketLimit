package xyz.plumc.packetlimit.limits;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.world.entity.player.Player;
import xyz.plumc.packetlimit.Limiter;

public class ChatLimit {
    public static void checkChat(Packet<?> packet, Player player){
        if (packet instanceof ServerboundChatPacket){
            Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.Chat, player.getUUID());
        }
    }
}
