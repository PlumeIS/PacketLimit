package xyz.plumc.packetlimit.limits;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.entity.player.Player;
import xyz.plumc.packetlimit.Limiter;

public class CreativeInventoryLimit {
    public static void checkCreativeInventory(Packet<?> packet, Player player){
        if (!(packet instanceof ServerboundSetCreativeModeSlotPacket slotPacket)) return;
        Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.CreativeInventory, player.getUUID());
    }
}
