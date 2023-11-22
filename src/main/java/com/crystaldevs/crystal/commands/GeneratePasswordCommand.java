package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.utils.Utils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class GeneratePasswordCommand extends Command {
    public GeneratePasswordCommand() {
        super("genpassword", "Generates a random secure password of specified length and copies it to clipboard.", "genpass");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("length", IntegerArgumentType.integer(1)).executes(this::generatePassword));
    }

    private int generatePassword(CommandContext<CommandSource> context) {
        int length = IntegerArgumentType.getInteger(context, "length");

        try {
            String password = Utils.generateRandomPassword(length);
            mc.keyboard.setClipboard(password);
            info("Generated password: %s (copied to clipboard)", password);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Password length must be greater than zero.")) {
                error("Password length must be greater than zero.");
            } else if (e.getMessage().equals("Password length must be smaller than 100.")) {
                error("Password length must be smaller than 100.");
            } else {
                error("An error occurred while generating the password.");
                e.printStackTrace();
            }
        }
        return SINGLE_SUCCESS;
    }
}