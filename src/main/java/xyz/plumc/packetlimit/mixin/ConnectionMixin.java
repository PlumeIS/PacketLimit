package xyz.plumc.packetlimit.mixin;


import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.plumc.packetlimit.limits.*;
import xyz.plumc.packetlimit.utils.PacketUtils;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at=@At("HEAD"))
    private void channelRead0(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        Player player = PacketUtils.getPlayer(this, ServerLifecycleHooks.getCurrentServer());
        if (player == null) return;

        NocomLimit.checkNocom(packet, player);
        InteractLimit.checkInteract(packet, player);
        ChatLimit.checkChat(packet, player);
        BookLimit.checkBook(packet, player);
        ContainerClickLimit.checkContainerClick(packet, player);
        ContainerLimit.checkContainer(packet, player);
        CreativeInventoryLimit.checkCreativeInventory(packet, player);
        UnfriendlyMoveLimit.checkUnfriendlyMove(packet, player);
    }
}
