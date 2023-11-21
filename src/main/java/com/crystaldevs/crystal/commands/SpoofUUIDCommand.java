package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.util.UUID;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SpoofUUIDCommand extends Command {
    public SpoofUUIDCommand() {
        super("spoof-uuid", "Spoof your UUID.", "spoofuuuid");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> executes = builder.executes(context -> {
            if (mc.player != null) {
                UUID randomUUID = UUID.randomUUID();
                mc.player.setUuid(randomUUID);
                info("Spoofed UUID to: " + randomUUID);
            } else {
                error("Failed to execute command, player is null.");
            }
            return SINGLE_SUCCESS;
        });
    }
}
