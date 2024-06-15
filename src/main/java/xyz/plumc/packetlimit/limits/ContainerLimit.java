package xyz.plumc.packetlimit.limits;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.plumc.packetlimit.Limiter;

public class ContainerLimit {
    public static void checkContainer(Packet<?> packet, Player player){
        if (!(packet instanceof ServerboundUseItemOnPacket useItemOnPacket)) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        BlockState blockState = player.level.getBlockState(useItemOnPacket.getHitResult().getBlockPos());
        if (blockState.getBlock() instanceof AbstractChestBlock<?>) {
            Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.Container, player.getUUID());
        }
    }
}
