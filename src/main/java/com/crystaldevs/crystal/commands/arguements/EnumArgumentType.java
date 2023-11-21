package com.crystaldevs.crystal.commands.arguements;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class EnumArgumentType<T extends Enum<?>> implements ArgumentType<T> {
    private static final Logger LOGGER = LogManager.getLogger("Crystal");
    private static final DynamicCommandExceptionType NO_SUCH_TYPE = new DynamicCommandExceptionType(o ->
        Text.literal(o + " is not a valid argument."));

    private T[] values;

    public EnumArgumentType(T defaultValue) {
        super();
        try {
            values = (T[]) defaultValue.getClass().getMethod("values").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("An error occurred while initializing EnumArgumentType:", e);
        }
    }

    public static <T extends Enum<?>> EnumArgumentType<T> enumArgument(T defaultValue) {
        return new EnumArgumentType<>(defaultValue);
    }

    public static <T extends Enum<?>> T getEnum(CommandContext<?> context, String name, T defaultValue) {
        return (T) context.getArgument(name, defaultValue.getClass());
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        try {
            String argument = reader.readString();
            for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                T t = values[i];
                if (!t.toString().equals(argument)) {
                } else {
                    return t;
                }
            }
            throw NO_SUCH_TYPE.create(argument);
        } catch (Exception e) {
            LOGGER.error("An error occurred while parsing EnumArgumentType:", e);
            throw e;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            return CommandSource.suggestMatching(Arrays.stream(values).map(T::toString), builder);
        } catch (Exception e) {
            LOGGER.error("An error occurred while listing suggestions for EnumArgumentType:", e);
            return Suggestions.empty();
        }
    }
}
