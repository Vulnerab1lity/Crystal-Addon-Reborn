package com.crystaldevs.crystal.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class TwoBTwoTSeenCommand extends Command {
    private static final String API_URL = "https://api.2b2t.dev/seen?username=";

    public TwoBTwoTSeenCommand() {
        super("2b2tseen", "Check when a user was last seen on 2b2t.", "2b2tseen");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("username", StringArgumentType.greedyString()).executes(context -> {
            String username = context.getArgument("username", String.class);

            try {
                URL url = new URL(API_URL + username);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                    String responseString = scanner.hasNext() ? scanner.next() : "";
                    inputStream.close();

                    if (!responseString.isEmpty()) {
                        String seenTimestamp = parseSeenResponse(responseString);
                        if (!seenTimestamp.isEmpty()) {
                            info(username + " was last seen on 2b2t at: " + seenTimestamp);
                        } else {
                            info(username + " has not been seen on 2b2t.");
                        }
                    } else {
                        error("Empty response received from the API.");
                    }
                } else {
                    error("Request failed with status code: " + responseCode);
                }

                conn.disconnect();
            } catch (IOException e) {
                error("An error occurred while checking the user: " + e.getMessage());
            }

            return SINGLE_SUCCESS;
        }));
    }


    private String parseSeenResponse(String responseString) {
        try {
            JsonArray jsonArray = new JsonParser().parse(responseString).getAsJsonArray();

            if (jsonArray.size() > 0) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                String seenTimestamp = jsonObject.get("seen").getAsString();
                return seenTimestamp;
            } else {
                return "Unknown Date";
            }
        } catch (JsonParseException e) {
            return "Unknown Date (Parsing Error)";
        }
    }


}
