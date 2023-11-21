package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.SentMessage;

import java.util.ArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DiscordCommand extends Command {
    public DiscordCommand() {
        super("crystal-discord", "Sends the crystal discord.", "discord");
    }
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> executes = builder.executes(context -> {
            if(mc.player != null) {
                if(mc.world != null) {
                    ChatUtils.info("Crystal Discord: https://discord.gg/8amWPxkdnT");
                } else {
                    ChatUtils.error("World is null, cannot execute command.");
                }
            } else {
                ChatUtils.error("Player is null, cannot execute command.");
            }
            return SINGLE_SUCCESS;
        });
    }
}
