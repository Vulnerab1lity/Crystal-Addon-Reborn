package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.utils.crystal.IPInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class IPLookupCommand extends Command {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public IPLookupCommand() {
        super("iplookup", "Gives info about a specified IP address.", "lookup");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("ip", StringArgumentType.greedyString()).executes(context -> {
            String ip = context.getArgument("ip", String.class);
            executor.execute(() -> checkIp(ip));
            return SINGLE_SUCCESS;
        }));
    }

    private void checkIp(String ip) {
        String apiBaseUrl = "http://ip-api.com/json/";
        String urlString = apiBaseUrl + ip;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                String jsonString = response.toString();
                IPInfo ipInfo = parseJsonResponse(jsonString);

                if (ipInfo.getStatus().equals("Invalid IP") || ipInfo.getTimezone() == null && ipInfo.getCountry() == null && ipInfo.getIsp() == null && ipInfo.getZip() == null) {
                    error("Invalid IP Address.");
                } else {
                    info("IP Information for " + ip + ":");
                    info(ipInfo.toString());
                }
            } else {
                error("Failed to retrieve information. HTTP error code: " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (IOException e) {
            System.err.println("Error occurred while contacting the API: " + e.getMessage());
        }
    }
    private IPInfo parseJsonResponse(String jsonString) {
        IPInfo ipInfo = new IPInfo();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        String[] keyValuePairs = jsonString.split(",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim().replaceAll("\"", "");
            String value = keyValue[1].trim();

            if (value.equals("null")) {
                if ("lat".equals(key) || "lon".equals(key)) {
                    jsonMap.put(key, 0.0);
                } else {
                    jsonMap.put(key, "");
                }
                continue;
            }

            if (value.startsWith("\"") && value.endsWith("\"")) {
                jsonMap.put(key, value.substring(1, value.length() - 1));
            } else if (value.contains(".")) {
                jsonMap.put(key, Double.parseDouble(value));
            } else {
                jsonMap.put(key, value);
            }
        }

        ipInfo.setStatus((String) jsonMap.get("status"));
        ipInfo.setCountry((String) jsonMap.get("country"));
        ipInfo.setCountryCode((String) jsonMap.get("countryCode"));
        ipInfo.setRegion((String) jsonMap.get("region"));
        ipInfo.setRegionName((String) jsonMap.get("regionName"));
        ipInfo.setCity((String) jsonMap.get("city"));
        ipInfo.setZip((String) jsonMap.get("zip"));

        Object latObj = jsonMap.get("lat");
        if (latObj instanceof String) {
            ipInfo.setLat(0.0);
        } else if (latObj instanceof Double) {
            ipInfo.setLat((Double) latObj);
        }

        Object lonObj = jsonMap.get("lon");
        if (lonObj instanceof String) {
            ipInfo.setLon(0.0);
        } else if (lonObj instanceof Double) {
            ipInfo.setLon((Double) lonObj);
        }

        ipInfo.setTimezone((String) jsonMap.get("timezone"));

        String ispValue = (String) jsonMap.get("isp");
        if (ispValue != null) {
            ipInfo.setIsp(ispValue.replaceAll(",", ""));
        }

        String orgValue = (String) jsonMap.get("org");
        if (orgValue != null) {
            ipInfo.setOrg(orgValue.replaceAll(",", ""));
        }

        ipInfo.setAs((String) jsonMap.get("as"));
        ipInfo.setQuery((String) jsonMap.get("query"));

        boolean allNull = jsonMap.values().stream().allMatch(value -> value == null || (value instanceof String && ((String) value).isEmpty()));

        if (allNull) {
            ipInfo.setStatus("Invalid IP");
        }

        return ipInfo;
    }

    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
