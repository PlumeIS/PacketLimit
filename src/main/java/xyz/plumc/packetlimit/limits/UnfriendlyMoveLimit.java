package xyz.plumc.packetlimit.limits;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Player;
import xyz.plumc.packetlimit.Limiter;

public class UnfriendlyMoveLimit {
    public static void checkUnfriendlyMove(Packet<?> packet, Player player)
    {
        if (!(packet instanceof ServerboundMovePlayerPacket movePlayerPacket)) return;
        if (movePlayerPacket.hasPosition()){
            if (Math.abs(movePlayerPacket.getX(0.0)-player.getX())>=10000 ||
                Math.abs(movePlayerPacket.getY(0.0)-player.getY())>=10000 ||
                Math.abs(movePlayerPacket.getZ(0.0)-player.getZ())>=10000) {
                Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.UnfriendlyMove, player.getUUID());
            }
        }
    }
}
