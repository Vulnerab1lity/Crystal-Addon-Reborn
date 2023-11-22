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

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

// TODO: Make this work
public class IPBlacklistCommand extends Command {
    private static final String API_KEY = "1f2e42ff297481ac8976fd44fd7a88fee81870580cfd07c549dfa3a6a7ff3b122306803a9c081566";
    private static final String API_URL = "https://api.abuseipdb.com/api/v2/check";

    public IPBlacklistCommand() {
        super("ipblacklist", "Check if an IP address is blacklisted in known databases.", "ipblacklist");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("ip", StringArgumentType.greedyString()).executes(context -> {
            String ipAddress = context.getArgument("ip", String.class);

            if (!isValidIpAddress(ipAddress)) {
                error("Invalid IP address: " + ipAddress);
                return SINGLE_SUCCESS;
            }

            try {
                URL url = new URL(API_URL + "?ipAddress=" + ipAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Key", API_KEY);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                    String responseString = scanner.hasNext() ? scanner.next() : "";
                    inputStream.close();

                    if (!responseString.isEmpty()) {
                        boolean isBlacklisted = parseBlacklistResponse(responseString);

                        if (isBlacklisted)
                            info("IP address " + ipAddress + " is blacklisted.");
                        else
                            info("IP address " + ipAddress + " is not blacklisted.");
                    } else {
                        error("Empty response received from the API.");
                    }
                } else {
                    error("Request failed with status code: " + responseCode);
                }
                conn.disconnect();
            } catch (IOException e) {
                error("An error occurred while checking the IP: " + e.getMessage());
            }
            return SINGLE_SUCCESS;
        }));
    }

    private boolean isValidIpAddress(String ipAddress) {
        return ipAddress.matches("^([0-9]{1,3}\\.){3}[0-9]{1,3}$") || ipAddress.matches("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    }

    private boolean parseBlacklistResponse(String responseString) {
        return false;
    }
}