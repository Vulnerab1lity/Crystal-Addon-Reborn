package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DupeReal extends Command {
    public DupeReal() {
        super("dupereal", "Dupes item currently held");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> executes = builder.executes(context -> {
            assert mc.player != null;
            if (Objects.requireNonNull(mc.player.getServer()).isSingleplayer()) {
                StringBuilder built = new StringBuilder();
                int i = 1;
                do {
                    built.append(r(1, 255)).append(".");
                    i++;
                } while (i < 5);
                ChatUtils.sendPlayerMsg("Hey guys i'm a monkey here's my ip: " + built);
            } else {
                ChatUtils.info("Successfully duped " + mc.player.getActiveItem());
            }
            return SINGLE_SUCCESS;
        });
    }

    public static int r(int o,
                        int e) {
        return o + (int) (Math.random() * ((e - o) + 1));
    }
}
