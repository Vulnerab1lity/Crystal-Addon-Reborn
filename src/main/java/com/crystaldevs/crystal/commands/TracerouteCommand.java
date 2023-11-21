package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class TracerouteCommand extends Command {
    public TracerouteCommand() {
        super("traceroute", "Perform a traceroute to a target IP address.", "traceroute");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("ip", StringArgumentType.greedyString())
            .executes(context -> {
                String ip = context.getArgument("ip", String.class);

                try {
                    List<String> tracerouteResults = performTraceroute(ip);

                    for (String result : tracerouteResults) {
                        info(result);
                    }
                } catch (IOException e) {
                    error("An error occurred while performing traceroute.");
                }

                return SINGLE_SUCCESS;
            })
        );
    }

    private List<String> performTraceroute(String ip) throws IOException {
        List<String> results = new ArrayList<>();

        Process tracerouteProcess = Runtime.getRuntime().exec("traceroute " + ip);
        BufferedReader reader = new BufferedReader(new InputStreamReader(tracerouteProcess.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            results.add(line);
        }

        return results;
    }
}
