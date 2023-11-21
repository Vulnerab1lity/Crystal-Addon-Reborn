package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.commands.arguements.EnumArgumentType;
import com.crystaldevs.crystal.utils.mc.seeds.Seed;
import com.crystaldevs.crystal.utils.mc.seeds.Seeds;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.seedfinding.mccore.version.MCVersion;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SeedCommand extends Command {
    private final static SimpleCommandExceptionType NO_SEED = new SimpleCommandExceptionType(Text.literal("No seed for current world saved."));

    public SeedCommand() {
        super("seed", "Get or set seed for the current world.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> executes = builder.executes(ctx -> {
            Seed seed = Seeds.get().getSeed();
            if (seed != null) {
                info(seed.toText());
                return SINGLE_SUCCESS;
            } else {
                throw NO_SEED.create();
            }
        });

        LiteralArgumentBuilder<CommandSource> then = builder.then(literal("list").executes(ctx -> {
            Seeds.get().seeds.forEach((name, seed) -> {
                MutableText text = Text.literal(name + " ");
                text.append(seed.toText());
                info(text);
            });
            return SINGLE_SUCCESS;
        }));

        LiteralArgumentBuilder<CommandSource> then1 = builder.then(literal("delete").executes(ctx -> {
            Seed seed = Seeds.get().getSeed();
            if (seed == null) {
                info("Seed is null, cannot execute command.");
            } else {
                MutableText text = Text.literal("Deleted ");
                text.append(seed.toText());
                info(text);
            }
            Seeds.get().seeds.remove(Utils.getWorldName());
            return SINGLE_SUCCESS;
        }));

        LiteralArgumentBuilder<CommandSource> then2 = builder.then(argument("seed", StringArgumentType.string()).executes(ctx -> {
            Seeds.get().setSeed(StringArgumentType.getString(ctx, "seed"));
            return SINGLE_SUCCESS;
        }));

        LiteralArgumentBuilder<CommandSource> then3 = builder.then(argument("seed", StringArgumentType.string()).then(argument("version", EnumArgumentType.enumArgument(MCVersion.latest())).executes(ctx -> {
            Seeds.get().setSeed(StringArgumentType.getString(ctx, "seed"), EnumArgumentType.getEnum(ctx, "version", MCVersion.latest()));
            return SINGLE_SUCCESS;
        })));
    }

}
