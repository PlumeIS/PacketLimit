package xyz.plumc.packetlimit.limits;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.world.entity.player.Player;
import xyz.plumc.packetlimit.Limiter;

public class BookLimit {
    public static void checkBook(Packet<?> packet, Player player){
        if (!(packet instanceof ServerboundEditBookPacket editBookPacket)) return;
        if (editBookPacket.getPages().size() == 50) {
            Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.Book, player.getUUID());
        }
    }
}
