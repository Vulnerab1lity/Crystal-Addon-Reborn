package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.command.CommandSource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

                    int pingAttempts = 5;

                    for (int i = 0; i < pingAttempts; i++) {
                        int finalI = i;
                        MeteorExecutor.execute(() -> {
                            try {
                                InetAddress address = InetAddress.getByName(ip);
                                if (address.isReachable(2000))
                                    info("Ping attempt " + (finalI + 1) + ": " + ip + " - Ping successful");
                                else
                                    error("Ping attempt " + (finalI + 1) + ": " + ip + " - Request timed out");
                            } catch (UnknownHostException e) {
                                error("Invalid IP/Domain: " + ip);
                            } catch (IOException e) {
                                error("An error occurred while pinging " + ip + ": " + e.getMessage());
                            }
                        });
                    }
                    return SINGLE_SUCCESS;
                })
        );
    }

    private boolean isValidDomain(String domain) {
        return domain != null && !domain.isEmpty();
    }
}