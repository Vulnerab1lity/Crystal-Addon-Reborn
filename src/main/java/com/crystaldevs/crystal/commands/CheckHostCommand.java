package com.crystaldevs.crystal.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CheckHostCommand extends Command {
    private final JsonObject EMPTY_JSON = JsonParser.parseString("{}").getAsJsonObject();

    public CheckHostCommand() {
        super("check-host", "Gives info about a specified IP address.", "iplookup", "lookup");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("server").then(argument("ip", StringArgumentType.string()).executes(ctx -> {
            String address = ctx.getArgument("ip", String.class);
            return getResponse(address.isBlank() ? "1.1.1.1" : (address.contains(":") ? address.substring(0, address.indexOf(':')) : address));
        })));
        builder.then(literal("currentServer").executes(ctx -> {
            String address;
            try {
                address = InetAddress.getByName(ServerAddress.parse(mc.getCurrentServerEntry().address).getAddress()).getHostAddress();
            } catch (UnknownHostException ignored) {
                address = mc.getCurrentServerEntry().address;
            }
            return getResponse(address.isBlank() ? "1.1.1.1" : (address.contains(":") ? address.substring(0, address.indexOf(':')) : address));
        }));
    }

    private int getResponse(String address) {
        new Thread(() -> {
            String addressForQuery = address.isBlank() ? "1.1.1.1" : (address.contains(":") ? address.substring(0, address.indexOf(':')) : address);
            String urlString = "http://ip-api.com/json/" + addressForQuery + "?fields=status,message,continent,continentCode,country,countryCode,region,regionName,city,district,zip,lat,lon,timezone,offset,currency,isp,org,as,asname,reverse,mobile,proxy,hosting,query";
            String jsonResponse = Http.get(urlString).sendString();
            JsonObject currentJson = jsonResponse != null ? JsonParser.parseString(jsonResponse).getAsJsonObject() : EMPTY_JSON;
            info("Server Information:");
            Set<Map.Entry<String, JsonElement>> currentJsonEntrySet = currentJson.entrySet();
            Map<String, JsonElement> currentJsonMap = new Object2ObjectArrayMap<>();
            for (Map.Entry<String, JsonElement> entry : currentJsonEntrySet) {
                if (entry.getKey().isBlank() || entry.getValue().getAsString().isBlank()) continue;
                currentJsonMap.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, JsonElement> entry : currentJsonMap.entrySet()) {
                if (entry.getKey().isBlank() || entry.getValue().getAsString().isBlank()) continue;
                ChatUtils.info("%s%s%s: %s", Formatting.WHITE, entry.getKey().toUpperCase(), Formatting.GRAY, entry.getValue());
            }
        }).start();
        return SINGLE_SUCCESS;
    }
}