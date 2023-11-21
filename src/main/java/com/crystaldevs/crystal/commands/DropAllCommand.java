package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.utils.mc.McUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DropAllCommand extends Command {
    public DropAllCommand() {
        super("dropall", "Drops all items in your inventory.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            assert mc.player != null;
            Objects.requireNonNull(McUtils.getInventory()).dropAll();
            return SINGLE_SUCCESS;
        });
    }
}
