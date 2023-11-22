package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class DiscordCommand extends Command {
    public DiscordCommand() {
        super("crystal-discord", "Sends the crystal discord.", "discord");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.info("Crystal Discord: https://discord.gg/MSN68KFjdy");
            return SINGLE_SUCCESS;
        });
    }
}