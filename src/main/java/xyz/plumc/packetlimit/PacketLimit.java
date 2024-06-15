package xyz.plumc.packetlimit;

import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.plumc.packetlimit.commands.LimitCommand;
import xyz.plumc.packetlimit.commands.arguments.LimitTypeArgument;
import xyz.plumc.packetlimit.config.ConfigHelper;

@Mod(PacketLimit.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class PacketLimit {
    public static final String MOD_ID = "packetlimit";

    public PacketLimit() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(Limiter.INSTANCE);
        ConfigHelper.load();
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        new Thread(()-> {
            try {
                Limiter.INSTANCE.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        Limiter.INSTANCE.stop();
        ConfigHelper.save();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ArgumentTypes.register("limit_type", LimitTypeArgument.class, new EmptyArgumentSerializer<>(LimitTypeArgument::limitType));
        LimitCommand.register(event.getDispatcher());
    }
}
