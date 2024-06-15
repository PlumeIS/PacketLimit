package xyz.plumc.packetlimit.limits;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.entity.player.Player;
import xyz.plumc.packetlimit.Limiter;

public class InteractLimit{
    public static void checkInteract(Packet<?> packet, Player player){
        if (packet instanceof ServerboundUseItemOnPacket) {
            Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.Interact, player.getUUID());
        }
    }
}
