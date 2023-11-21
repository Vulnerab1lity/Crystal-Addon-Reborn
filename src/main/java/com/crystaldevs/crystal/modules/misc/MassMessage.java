package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MassMessage extends Module {
    private final Setting<String> commandName;
    private final Setting<String> message;
    private final Setting<Integer> delay;

    public MassMessage() {
        super(Categories.Misc, "Mass Message", "CRYSTAL || Mass private message everyone on a server.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        commandName = sgGeneral.add(new StringSetting.Builder()
            .name("Message command")
            .description("The command for dming players.")
            .defaultValue("/msg")
            .build()
        );

        delay = sgGeneral.add(new IntSetting.Builder()
            .name("Delay")
            .description("The delay time in seconds before paying each time.")
            .defaultValue(5)
            .min(0)
            .max(300)
            .sliderMin(0)
            .sliderMax(300)
            .noSlider()
            .build()
        );

        message = sgGeneral.add(new StringSetting.Builder()
            .name("Message")
            .description("The specified message to DM players.")
            .defaultValue("Join the Crystal Addon discord: https://discord.gg/TZMT4jPHbG")
            .build()
        );
    }

    public ArrayList<PlayerListEntry> getPlayerList() {
        ArrayList<PlayerListEntry> playerList = new ArrayList<>();

        if (mc.world != null) {
            Collection<PlayerListEntry> players = Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList();
            playerList.addAll(players);
        }

        return playerList;
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onActivate() {
        executor.execute(() -> {
            try {
                MassMessagePlayers();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void MassMessagePlayers() throws InterruptedException {
        String result = commandName.get().replaceAll("\\s", "");
        if(!mc.isInSingleplayer()) {
            List<PlayerListEntry> playerList = getPlayerList();
            for(PlayerListEntry player : playerList) {
                assert mc.player != null;

                String name = player.getProfile().getName();

                if(!Objects.equals(player.getProfile().getName(), mc.player.getName().toString())) {
                    if(result.startsWith("/")) {
                        ChatUtils.sendPlayerMsg(result + " " + name + " " + message.get());
                        Thread.sleep(convertSecondsToMilliseconds(delay.get()));
                    } else {
                        String fixedCMD = "/" + result;
                        ChatUtils.sendPlayerMsg(fixedCMD + " " + name + " " + message.get());
                        Thread.sleep(convertSecondsToMilliseconds(delay.get()));
                    }
                }
            }
        } else {
            error("You must be in a server to use MassMessage, toggling.");
            toggle();
        }
    }

    public static long convertSecondsToMilliseconds(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Seconds must be a positive value.");
        }
        return (long) seconds * 1000;
    }
}
