package xyz.plumc.packetlimit.limits;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import xyz.plumc.packetlimit.Limiter;

public class ContainerClickLimit {
    public static void checkContainerClick(Packet<?> packet, Player player){
        if (!(packet instanceof ServerboundContainerClickPacket clickPacket)) return;
        if (clickPacket.getStateId()==123344 && clickPacket.getSlotNum()==2957234 && clickPacket.getButtonNum()==2859623) {
            if (clickPacket.getCarriedItem().getItem()== Items.AIR){
                Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.ContainerClick, player.getUUID());
            }
        }
    }
}
