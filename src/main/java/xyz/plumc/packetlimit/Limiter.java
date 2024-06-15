package xyz.plumc.packetlimit;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.plumc.packetlimit.config.LimitType;
import xyz.plumc.packetlimit.config.Limits;
import xyz.plumc.packetlimit.config.PacketLimitConfig;
import xyz.plumc.packetlimit.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Limiter {
    public final static Limiter INSTANCE = new Limiter();
    private final List<Limit> playerLimits = new ArrayList<>();
    private int clearCount = 0;
    private boolean running = true;
    public void run() throws InterruptedException {
        while (running){
            Thread.sleep(10);
            INSTANCE.clear();
            for (Limit limit : this.playerLimits){
                limit.checkOverRate();
            }
        }
    }

    public void stop(){
        running = false;
    }

    public void addLimit(AcceptLimitType type, UUID player){
        if (!PacketLimitConfig.toggle) return;
        if (PacketLimitConfig.bypassOP && PlayerUtils.isOp(player)) return;
        Limit limit = getLimit(Limits.INSTANCE.getLimit(type.name), player);
        limit.addCount();
    }

    private void clear(){
        if (!playerLimits.isEmpty() && clearCount++ >= 100) clearCount = 0;
        else return;
        List<Limit> limits = new ArrayList<>();
        playerLimits.forEach(limit -> limits.add(limit));
        for (Limit limit : limits){
            if ((System.currentTimeMillis()-limit.recordTime)>=limit.type.liveTime&&(System.currentTimeMillis()-limit.recordTime)>=limit.type.sensitive*1000L){
                playerLimits.remove(limit);
            }
        }
    }

    private Limit getLimit(LimitType type, UUID player){
        for (Limit limit : playerLimits){
            if (limit.type == type && limit.player == player){
                return limit;
            }
        }
        Limit limit = new Limit(type, player);
        playerLimits.add(limit);
        return limit;
    }

    public enum AcceptLimitType{
        Nocom("nocom"),
        Interact("interact"),
        Chat("chat"),
        Book("book"),
        CreativeInventory("creativeInventory"),
        ContainerClick("containerClick"),
        Container("container"),
        UnfriendlyMove("unfriendlyMove")
        ;
        public final String name;

        AcceptLimitType(String name){
            this.name = name;
        }
    }


}

class Limit{
    public final LimitType type;
    public final UUID player;
    public ServerPlayer serverPlayer;
    public long lastTime;
    public long recordTime;
    public float rate;
    private int count = 0;
    private int packets = 0;
    private int warningWaiting = 0;
    public boolean overRate = false;
    public Long overTime;
    public int warningCount = 0;
    public int kickCount = 0;

    public Limit(LimitType type, UUID player){
        this.type = type;
        this.player = player;
        this.lastTime = System.currentTimeMillis()-10L;
        this.recordTime = System.currentTimeMillis()-10L;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        this.serverPlayer = server.getPlayerList().getPlayer(player);
    }

    public void addCount(){
        this.count++;
        this.packets++;
        if ((float) (System.currentTimeMillis() - lastTime)>type.sensitive){
            lastTime = System.currentTimeMillis();
            count = 0;
            rate = (float) count / (float) (System.currentTimeMillis() - recordTime) * 1000;
        } else {
            rate = (float) count / (float) (System.currentTimeMillis() - lastTime) * 1000;
        }
        recordTime = System.currentTimeMillis();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        this.serverPlayer = server.getPlayerList().getPlayer(player);
    }

    public void updateRate(){
        rate = (float) count / (float) (System.currentTimeMillis() - lastTime) * 1000;
    }

    public void checkOverRate(){
        updateRate();

        if (rate > type.maxRate){
            if (!overRate){
                overTime = System.currentTimeMillis();
            }
            overRate = true;

            if (((System.currentTimeMillis()-overTime)/1000L) > warningWaiting) {
                if (warningWaiting == 0){
                    warningCount++;
                    PlayerUtils.sendLimitActionBar(serverPlayer, type, warningCount);
                    if (warningCount >= type.kickLimit && packets >= type.minLimit){
                        kickCount++;
                        if (kickCount >= type.banLimit){
                            PlayerUtils.sendBan(ServerLifecycleHooks.getCurrentServer(), serverPlayer, type);
                            count = 0;
                        }
                        PlayerUtils.sendKick(serverPlayer, type);
                    }
                }
                PlayerUtils.sendLimitTitle(serverPlayer, type, warningWaiting);
                warningWaiting++;
            }
            PlayerUtils.sendLimitSubTitle(serverPlayer, type, rate);

            if (System.currentTimeMillis()-overTime>type.waringWaiting && packets >= type.minLimit){
                kickCount++;
                if (kickCount >= type.banLimit){
                    PlayerUtils.sendBan(ServerLifecycleHooks.getCurrentServer(), serverPlayer, type);
                    count = 0;
                }
                PlayerUtils.sendKick(serverPlayer, type);
                overRate = false;
            }
        } else if (System.currentTimeMillis()-recordTime>type.sensitive) {
            overRate = false;
            warningWaiting = 0;
            packets=0;
        }
    }
}