package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.net.InetSocketAddress;
import java.net.Proxy;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class NetProxyCommand extends Command {
    private static Proxy currentProxy = Proxy.NO_PROXY;

    public enum ProxyType {
        HTTP,
        SOCKS
    }

    public static void setCurrentProxy(Proxy proxy) {
        currentProxy = proxy;
    }

    public static Proxy getCurrentProxy() {
        return currentProxy;
    }

    public NetProxyCommand() {
        super("net-proxy", "Sets up a proxy for network connections.");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("proxy-type", StringArgumentType.word())
                .then(argument("proxy-ip", StringArgumentType.string())
                        .then(argument("proxy-port", IntegerArgumentType.integer())
                                .executes(context -> {
                                    String proxyTypeString = context.getArgument("proxy-type", String.class).toUpperCase();
                                    ProxyType proxyType;

                                    try {
                                        proxyType = ProxyType.valueOf(proxyTypeString);
                                    } catch (IllegalArgumentException ignored) {
                                        StringBuilder availableTypes = new StringBuilder();
                                        for (ProxyType type : ProxyType.values()) {
                                            availableTypes.append(type.name()).append(", ");
                                        }
                                        availableTypes = new StringBuilder(availableTypes.substring(0, availableTypes.length() - 2));

                                        error("Invalid proxy type provided. Available types: " + availableTypes);
                                        return SINGLE_SUCCESS;
                                    }

                                    String proxyIp = context.getArgument("proxy-ip", String.class);
                                    int proxyPort = context.getArgument("proxy-port", Integer.class);

                                    if (isValidIP(proxyIp) && isValidPort(proxyPort)) {
                                        try {
                                            Proxy.Type type = proxyType == ProxyType.HTTP ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
                                            currentProxy = new Proxy(type, new InetSocketAddress(proxyIp, proxyPort));
                                            info("Proxy set successfully: " + proxyTypeString + " - " + proxyIp + ":" + proxyPort);
                                        } catch (Exception e) {
                                            error("Failed to set up the proxy. Please check the IP and port.");
                                            error("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    } else {
                                        error("Invalid IP or port provided. Please provide a valid IP and port.");
                                    }
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    private boolean isValidIP(String ip) {
        if (ip.isEmpty()) return false;

        String ipPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        return ip.matches(ipPattern);
    }

    private boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }
}