package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class WebhookSendCommand extends Command {
    public WebhookSendCommand() {
        super("webhook-send", "sends a message to specified discord webhook.");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("webhook", StringArgumentType.greedyString())
                .then(argument("message", StringArgumentType.string())
                        .executes(context -> {
                            String webhook = context.getArgument("webhook", String.class);
                            String message = context.getArgument("message", String.class);

                            if (!webhook.isEmpty()) {
                                if (!webhook.startsWith("https://")) {
                                    webhook = "https://" + webhook;
                                }

                                if (!webhook.contains("discord.com/api/webhooks/")) {
                                    error("Invalid discord webhook URL.");
                                    return SINGLE_SUCCESS;
                                }

                                try {
                                    URL url = new URL(webhook);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setDoOutput(true);

                                    String jsonPayload = "{\"content\":\"" + message + "\"}";

                                    DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                                    outputStream.writeBytes(jsonPayload);
                                    outputStream.flush();
                                    outputStream.close();

                                    int responseCode = conn.getResponseCode();
                                    if (responseCode == 204)
                                        info("Message sent to webhook successfully.");
                                    else
                                        error("Failed to send message to webhook. Response code: " + responseCode);
                                } catch (IOException ignored) {
                                }
                            } else {
                                error("Incomplete command. Must be .webhook-delete {webhook} \"{message}\".");
                            }
                            return SINGLE_SUCCESS;
                        })
                )
        );
    }
}