package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class TwoBTwoTStatsCommand extends Command {
    private static final String API_URL = "https://api.2b2t.dev/stats?username=";
    private final Pattern FIELD_PATTERN = Pattern.compile("\"(\\w+)\":\\s*([\\d.-]+)");

    public TwoBTwoTStatsCommand() {
        super("2b2tstats", "Check statistics for a user on 2b2t.", "2b2tstats");
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

                    if (!responseString.isEmpty())
                        parseStatsResponse(responseString, username);
                    else
                        error("Empty response received from the API.");
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

    private void parseStatsResponse(String responseString, String username) {
        Matcher matcher = FIELD_PATTERN.matcher(responseString);

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = matcher.group(2);

            switch (fieldName) {
                case "kills" -> info(username + " has " + Integer.parseInt(fieldValue) + " kills.");
                case "deaths" -> info(username + " has " + Integer.parseInt(fieldValue) + " deaths.");
            }
        }
    }
}