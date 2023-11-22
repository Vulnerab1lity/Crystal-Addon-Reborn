package com.crystaldevs.crystal.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

public class DNSLookupCommand extends Command {
    public DNSLookupCommand() {
        super("dnslookup", "Gets the DNS records for a specific server.", "records");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("domain", StringArgumentType.greedyString()).executes(context -> {
            String domain = context.getArgument("domain", String.class);

            if (!isValidDomain(domain)) {
                System.out.println("Invalid domain: " + domain);
                return SINGLE_SUCCESS;
            }

            String apiUrl = "https://api.api-ninjas.com/v1/dnslookup?domain=" + domain;
            String apiKey = "MUbKzj/qMAzwTWzo/TUd0A==4gzO0ipNpKqfEsuD";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("X-Api-Key", apiKey);

                int statusCode = conn.getResponseCode();

                if (statusCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                    String responseString = scanner.hasNext() ? scanner.next() : "";
                    inputStream.close();

                    if (!responseString.isEmpty()) {
                        com.google.gson.JsonArray recordsArray = com.google.gson.JsonParser.parseString(responseString).getAsJsonArray();

                        if (!recordsArray.isEmpty()) {
                            info("DNS Records Summary for " + domain);
                            for (JsonElement recordElement : recordsArray) {
                                JsonObject recordObject = recordElement.getAsJsonObject();

                                String recordType = getNonNullString(recordObject, "record_type");
                                String recordValue = getNonNullString(recordObject, "value");

                                info(recordType + ": " + recordValue);
                            }
                        } else {
                            error("No DNS records found for: " + domain);
                        }
                    } else {
                        error("Empty response received for: " + domain);
                    }
                } else {
                    error("Request failed with status code: " + statusCode);

                    InputStream errorStream = conn.getErrorStream();
                    if (errorStream != null) {
                        Scanner scanner = new Scanner(errorStream).useDelimiter("\\A");
                        String errorResponse = scanner.hasNext() ? scanner.next() : "";
                        error("Error Response: " + errorResponse);
                    }
                }

                conn.disconnect();
            } catch (IOException e) {
                error("An error occurred while making the API request: " + e.getMessage());
            }
            return SINGLE_SUCCESS;
        }));
    }

    private boolean isValidDomain(String domain) {
        if (domain != null) return !domain.isEmpty();
        else return false;
    }

    private String getNonNullString(JsonObject jsonObject, String fieldName) {
        JsonElement jsonElement = jsonObject.get(fieldName);
        if ((jsonElement == null) || jsonElement.isJsonNull())
            return "N/A";
        return jsonElement.getAsString();
    }
}