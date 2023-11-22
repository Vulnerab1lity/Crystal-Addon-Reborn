package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class DupeReal extends Command {
    public DupeReal() {
        super("dupereal", "Dupes item currently held");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            StringBuilder ip = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                if (i > 0) ip.append('.');
                ip.append(r(1, 255));
            }
            ChatUtils.sendPlayerMsg("Hey guys i'm a monkey here's my ip: " + ip);
            return SINGLE_SUCCESS;
        });
    }

    public static int r(int o, int e) {
        return o + (int) (Math.random() * ((e - o) + 1));
    }
}