package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class WebhookDeleteCommand extends Command {
    public WebhookDeleteCommand() {
        super("webhook-delete", "Deletes a specified discord webhook.");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("webhook", StringArgumentType.greedyString()).executes(context -> {
            String webhook = context.getArgument("webhook", String.class);

            String result = webhook.replaceAll("\\s", "");

            if(!webhook.isEmpty()) {

                if (!result.startsWith("https://")) {
                    result = "https://" + result;
                }

                if(!result.contains("discord.com/api/webhooks/")) {
                    error("Invalid discord webhook URL.");
                    return SINGLE_SUCCESS;
                }

                String command;
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    command = "cmd.exe /c curl -X DELETE " + result;
                } else {
                    command = "bash -c \"curl -L -X DELETE " + result + "\"";
                }

                Process process;
                try {
                    process = Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while (true) {
                    try {
                        if ((line = reader.readLine()) == null) {
                            break;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    info(line);
                }

                int exitCode = 0;
                try {
                    exitCode = process.waitFor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(exitCode == 0) {
                    info("Command executed, successfully deleted webhook.");
                } else {
                    info("Command executed, exit code: " + exitCode);
                }
            } else {
                error("Incomplete command. Must be .webhook-delete {webhook}.");
            }

            return SINGLE_SUCCESS;
        }));
    }
}
