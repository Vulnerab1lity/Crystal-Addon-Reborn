package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SpoofServerBrandCommand extends Command {
    public SpoofServerBrandCommand() {
        super("spoofbrand", "Spoofs the minecraft server brand.", "spoofserver");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("brand", StringArgumentType.greedyString()).executes(context -> {
            String brand = context.getArgument("brand", String.class);
            if(!brand.isEmpty()) {
                if(!mc.isInSingleplayer()) {
                    if(mc.world != null) {
                        if(mc.player != null) {
                            mc.player.getServer();
                            info("Succesfully spoofed server brand to: " + mc.player.getServer());
                        } else {
                            error("The player is null. Cannot execute command.");
                        }
                    } else {
                        error("The world is null. Cannot execute command.");
                    }
                } else {
                    error("You must be in a server to spoof the brand,");
                }
            } else {
                error("Incomplete command. Must be .spoofname {username}.");
            }

            return SINGLE_SUCCESS;
        }));
    }
}
