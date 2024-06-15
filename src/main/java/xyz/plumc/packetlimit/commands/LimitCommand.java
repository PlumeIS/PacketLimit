package xyz.plumc.packetlimit.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import xyz.plumc.packetlimit.commands.arguments.LimitTypeArgument;
import xyz.plumc.packetlimit.config.LimitType;
import xyz.plumc.packetlimit.config.Limits;
import xyz.plumc.packetlimit.config.PacketLimitConfig;

public class LimitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("limit").executes(LimitCommand::helper).requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                    .then(Commands.literal("toggle").executes(context -> toggle(context, !PacketLimitConfig.toggle))
                            .then(Commands.literal("true").executes(context -> toggle(context, true)))
                            .then(Commands.literal("false").executes(context -> toggle(context, false)))
                    )
                    .then(Commands.literal("bypassOP").executes(context -> bypassOP(context, !PacketLimitConfig.bypassOP))
                            .then(Commands.literal("true").executes(context -> bypassOP(context, true)))
                            .then(Commands.literal("false").executes(context -> bypassOP(context, false)))
                    )
                    .then(Commands.literal("config").executes(LimitCommand::helper)
                            .then(Commands.argument("limitType", LimitTypeArgument.limitType()).executes(LimitCommand::helper)
                                    .then(Commands.literal("waringTime").executes(LimitCommand::helper).then(Commands.argument("value", FloatArgumentType.floatArg(0)).executes(context -> set(context, "waringTime", IntegerArgumentType.getInteger(context, "value")))))
                                    .then(Commands.literal("maxRate").executes(LimitCommand::helper).then(Commands.argument("value", FloatArgumentType.floatArg(0)).executes(context -> set(context, "maxRate", FloatArgumentType.getFloat(context, "value")))))
                                    .then(Commands.literal("recordTime").executes(LimitCommand::helper).then(Commands.argument("value", FloatArgumentType.floatArg(0)).executes(context -> set(context, "recordTime", FloatArgumentType.getFloat(context, "value")))))
                                    .then(Commands.literal("kick").executes(LimitCommand::helper).then(Commands.argument("value", IntegerArgumentType.integer(0)).executes(context -> set(context, "kick", IntegerArgumentType.getInteger(context, "value")))))
                                    .then(Commands.literal("ban").executes(LimitCommand::helper).then(Commands.argument("value", IntegerArgumentType.integer(0)).executes(context -> set(context, "ban", IntegerArgumentType.getInteger(context, "value")))))
                                    .then(Commands.literal("min").executes(LimitCommand::helper).then(Commands.argument("value", IntegerArgumentType.integer(0)).executes(context -> set(context, "min", IntegerArgumentType.getInteger(context, "value")))))
                            )
                    )
        );
    }

    private static int toggle(CommandContext<CommandSourceStack> context, boolean toggle) {
        System.out.println(Limits.INSTANCE.getLimitNames());
        PacketLimitConfig.toggle = toggle;
        context.getSource().sendSuccess(new TextComponent("当前LimitPacket状态: "+(toggle?"开":"关")), true);
        return 1;
    }

    private static int bypassOP(CommandContext<CommandSourceStack> context, boolean toggle) {
        PacketLimitConfig.bypassOP = toggle;
        context.getSource().sendSuccess(new TextComponent("当前是否绕过管理员: "+(toggle?"是":"否")), true);
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> context, String key, Object value){
        LimitType limit = LimitTypeArgument.getLimitType(context, "limitType");
        context.getSource().sendSuccess(new TextComponent("当前 %s 的 %s 已设置为 %s".formatted(limit.name, key, String.valueOf(value))), true);
        switch (key){
            case "waringTime":
                limit.waringWaiting = ((float) value)*1000L;
                break;
            case "maxRate":
                limit.maxRate = ((float) value)/limit.scale;
                break;
            case "recordTime":
                limit.liveTime = ((float) value)*1000L;
                break;
            case "kick":
                limit.kickLimit = (int) value;
                break;
            case "ban":
                limit.banLimit = (int) value;
                break;
            case "min":
                limit.minLimit = (int) value;
                break;
        }
        return 1;
    }

    private static int helper(CommandContext<CommandSourceStack> context) {
        context.getSource().sendFailure(new TextComponent("""
                                                    用法:
                                                        /limit toggle [true/false]
                                                            总开关
                                                        /limit bypassOP [ture/false]
                                                            切换是否绕过管理员
                                                        
                                                        /limit config <limitType> waringTime [int]
                                                            当玩家持续触发警告一段时间后将其踢出游戏
                                                        /limit config <limitType> maxRate [float]
                                                            当超过一定速率后触发警告
                                                        /limit config <limitType> recordTime [float]
                                                            警告保存时间
                                                        /limit config <limitType> kick [int]
                                                            当玩家触发多次警告时将其踢出游戏
                                                        /limit config <limitType> ban [int]
                                                            当玩家被多次踢出游戏后将其封禁
                                                        /limit config <limitType> min [int]
                                                            玩家被处罚的最小发包数
                                                    """));
        return 0;
    }
}

