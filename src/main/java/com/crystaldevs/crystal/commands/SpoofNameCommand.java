package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SpoofNameCommand extends Command {
    public SpoofNameCommand() {
        super("spoofname", "Spoofs your minecraft username locally.", "spoofuser");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("username", StringArgumentType.greedyString()).executes(context -> {
            String username = context.getArgument("username", String.class);
            if(!username.isEmpty()) {
                if(mc.world != null) {
                    if(mc.player != null) {
                        mc.player.setCustomName(Text.of(username));
                        info("Succesfully spoofed username to: " + mc.player.getCustomName());
                    } else {
                        error("The player is null. Cannot execute command.");
                    }
                } else {
                    error("The world is null. Cannot execute command.");
                }
            } else {
                error("Incomplete command. Must be .spoofname {username}.");
            }

            return SINGLE_SUCCESS;
        }));
    }
}
