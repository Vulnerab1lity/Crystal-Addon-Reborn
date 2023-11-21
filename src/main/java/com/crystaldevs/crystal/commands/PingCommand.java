package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PingCommand extends Command {
    public PingCommand() {
        super("ping", "Ping a server or domain to check the response time.", "ping");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("ip", StringArgumentType.greedyString())
            .executes(context -> {
                String ip = context.getArgument("ip", String.class);

                if (!isValidDomain(ip)) {
                    error("Invalid IP/Domain: " + ip);
                    return SINGLE_SUCCESS;
                }

                try {
                    int pingAttempts = 5;

                    ExecutorService executor = Executors.newFixedThreadPool(pingAttempts);

                    for (int i = 0; i < pingAttempts; i++) {
                        int finalI = i;
                        executor.submit(() -> {
                            try {
                                InetAddress address = InetAddress.getByName(ip);
                                if (address.isReachable(2000)) {
                                    info("Ping attempt " + (finalI + 1) + ": " + ip + " - Ping successful");
                                } else {
                                    error("Ping attempt " + (finalI + 1) + ": " + ip + " - Request timed out");
                                }
                            } catch (UnknownHostException e) {
                                error("Invalid IP/Domain: " + ip);
                            } catch (IOException e) {
                                error("An error occurred while pinging " + ip + ": " + e.getMessage());
                            }
                        });
                    }

                    executor.shutdown();
                    executor.awaitTermination(5, TimeUnit.SECONDS);

                } catch (InterruptedException e) {
                    error("Interrupted while waiting for ping attempts to complete.");
                }

                return SINGLE_SUCCESS;
            })
        );
    }

    private boolean isValidDomain(String domain) {
        return domain != null && !domain.isEmpty();
    }
}
