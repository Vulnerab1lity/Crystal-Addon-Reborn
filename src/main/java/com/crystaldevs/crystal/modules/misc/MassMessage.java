package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public class MassMessage extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> commandName = sgGeneral.add(new StringSetting.Builder()
            .name("message-command")
            .description("The command for DMing players.")
            .defaultValue("msg")
            .build()
    );

    private final Setting<String> message = sgGeneral.add(new StringSetting.Builder()
            .name("message")
            .description("The specified message to DM players.")
            .defaultValue("Crystal Addon on Top!")
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay time in seconds before messaging each time.")
            .defaultValue(5)
            .min(0)
            .max(300)
            .sliderMin(0)
            .sliderMax(300)
            .noSlider()
            .build()
    );

    private Thread thread = null;

    public MassMessage() {
        super(Categories.Misc, "mass-message", "CRYSTAL || Mass private message everyone on a server.");
    }

    @Override
    public void onActivate() {
        thread = new Thread(() -> {
            List<PlayerListEntry> players = mc.player.networkHandler.getPlayerList().stream().toList();
            for (PlayerListEntry player : players) {
                String name = player.getProfile().getName();

                if (!(name.equals(mc.player.getGameProfile().getName()))) {
                    mc.player.networkHandler.sendChatCommand(commandName.get() + " " + name + " " + message.get());
                    try {
                        Thread.sleep(delay.get().longValue() * 1000L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onDeactivate() {
        if (thread != null && thread.isAlive()) thread.stop();
    }
}