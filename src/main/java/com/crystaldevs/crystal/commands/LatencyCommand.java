package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.utils.player.PlayerUtils.getPing;

public class LatencyCommand extends Command {
    public LatencyCommand() {
        super("latency", "returns your ping");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> executes = builder.executes(context -> {
            info("Your latency is: " + getPing());
            return SINGLE_SUCCESS;
        });
    }
}
