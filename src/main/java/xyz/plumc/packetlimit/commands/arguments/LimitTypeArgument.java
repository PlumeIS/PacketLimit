package xyz.plumc.packetlimit.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import xyz.plumc.packetlimit.config.LimitType;
import xyz.plumc.packetlimit.config.Limits;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class LimitTypeArgument implements ArgumentType<LimitType> {
    private static final Collection<String> EXAMPLES = Arrays.asList("nocom", "interact");
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((p_85470_) -> {
        return new TranslatableComponent("commands.forge.arguments.enum.invalid", "LimitType", p_85470_);
    });
    public static LimitType getLimitType(CommandContext<CommandSourceStack> context, String name){
        return context.getArgument(name, LimitType.class);
    }

    public static LimitTypeArgument limitType(){
        return new LimitTypeArgument();
    }

    @Override
    public LimitType parse(StringReader stringReader) throws CommandSyntaxException {
        String s = stringReader.readUnquotedString();
        LimitType limitType = Limits.INSTANCE.getLimit(s);
        if (limitType != null) return limitType;
        else throw ERROR_INVALID_VALUE.create(stringReader.getString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Limits.INSTANCE.getLimitNames(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}