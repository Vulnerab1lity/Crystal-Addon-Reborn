package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.CrystalAddon;
import com.crystaldevs.crystal.utils.mc.McUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear-inv", "Clears your inventory.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            assert mc.player != null;
            if (mc.player.getAbilities().creativeMode) {
                Objects.requireNonNull(McUtils.getInventory()).clear();
            } else {
                CrystalAddon.LOG.error("You must be in creative to run the .clear command.");
            }
            return SINGLE_SUCCESS;
        });
    }
}
