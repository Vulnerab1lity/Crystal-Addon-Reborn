package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

import static meteordevelopment.meteorclient.systems.modules.Categories.Misc;

public class ServerOpNuke extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> opAlt = sgGeneral.add(new BoolSetting.Builder()
            .name("op-alt")
            .description("Give your alt account operator.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> stopServer = sgGeneral.add(new BoolSetting.Builder()
            .name("stop-server")
            .description("Runs the /stop command.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> deopAllPlayers = sgGeneral.add(new BoolSetting.Builder()
            .name("deop-all-players")
            .description("Removes all online players operator status'.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> clearAllPlayersInv = sgGeneral.add(new BoolSetting.Builder()
            .name("clear-all-players-inventory")
            .description("Will clear all online players inventories.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> banAllPlayers = sgGeneral.add(new BoolSetting.Builder()
            .name("ban-all-players")
            .description("Automatically bans all online players except yourself.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> ipBanAllPlayers = sgGeneral.add(new BoolSetting.Builder()
            .name("IP-ban-all-players")
            .description("Automatically IP bans all online players except yourself.")
            .defaultValue(false)
            .visible(banAllPlayers::get)
            .build()
    );

    private final Setting<String> altNameToOp = sgGeneral.add(new StringSetting.Builder()
            .name("alt-account-username.")
            .description("The name of your alt to op.")
            .defaultValue("trolling")
            .visible(opAlt::get)
            .build()
    );

    public ServerOpNuke() {
        super(Misc, "server-operator-nuke", "CRYSTAL || For trolling once you get operator on a server.");
    }

    @Override
    public void onActivate() {
        List<PlayerListEntry> players = mc.player.networkHandler.getPlayerList().stream().toList();
        for (PlayerListEntry player : players) {
            String name = player.getProfile().getName();

            if (!(name.equals(mc.player.getGameProfile().getName()))) {
                if (deopAllPlayers.get())
                    mc.player.networkHandler.sendChatCommand("deop " + name);

                if (clearAllPlayersInv.get())
                    mc.player.networkHandler.sendChatCommand("clear " + name);

                if (banAllPlayers.get()) {
                    if (ipBanAllPlayers.get())
                        mc.player.networkHandler.sendChatCommand("minecraft:ban-ip " + name);
                    else
                        mc.player.networkHandler.sendChatCommand("minecraft:ban " + name);
                }
            }
        }

        if (opAlt.get()) mc.player.networkHandler.sendChatCommand("op " + altNameToOp.get());

        if (stopServer.get()) mc.player.networkHandler.sendChatCommand("stop");

        toggle();
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}