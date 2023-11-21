package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.net.Proxy;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class NetProxyDisconnectCommand extends Command {
    public NetProxyDisconnectCommand() {
        super("net-proxy-disconnect", "Disconnects from the configured proxy.");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (NetProxyCommand.getCurrentProxy() == Proxy.NO_PROXY) {
                warning("Not connected to a proxy.");
            } else {
                NetProxyCommand.setCurrentProxy(Proxy.NO_PROXY);
                info("Disconnected from the proxy.");
            }

            return SINGLE_SUCCESS;
        });
    }
}
