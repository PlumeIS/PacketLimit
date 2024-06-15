package xyz.plumc.packetlimit.limits;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import xyz.plumc.packetlimit.Limiter;

public class NocomLimit{

    public static void checkNocom(Packet<?> packet, Player player){
        if (!(packet instanceof ServerboundUseItemOnPacket useItemOnPacket)) return;
        BlockPos pos = useItemOnPacket.getHitResult().getBlockPos();
        Vec3 position = player.position();
        double distance = position.distanceTo(new Vec3(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5));
        if (distance >= 100){
            Limiter.INSTANCE.addLimit(Limiter.AcceptLimitType.Nocom, player.getUUID());
        }
    }
}
