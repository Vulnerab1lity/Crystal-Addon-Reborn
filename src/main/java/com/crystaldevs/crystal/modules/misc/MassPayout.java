package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public class MassPayout extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> moneyToPay = sgGeneral.add(new IntSetting.Builder()
            .name("money-to-pay")
            .description("The total amount of money to payout between all players.")
            .defaultValue(10000)
            .min(1)
            .sliderMax(100000000)
            .noSlider()
            .build()
    );

    private final Setting<Boolean> playerFirstInCommand = sgGeneral.add(new BoolSetting.Builder()
            .name("player-first-in-command")
            .description("If the player argument is first in the command or not, like /pay {player} {amount}.")
            .defaultValue(true)
            .build()
    );

    private final Setting<String> commandName = sgGeneral.add(new StringSetting.Builder()
            .name("command-name")
            .description("The name of the command that will be used to pay.")
            .defaultValue("pay")
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay time in seconds before paying each time.")
            .defaultValue(5)
            .min(1)
            .sliderMin(0)
            .sliderMax(20)
            .noSlider()
            .build()
    );

    private Thread thread = null;

    public MassPayout() {
        super(Categories.Misc, "mass-payout", "CRYSTAL || A module to use the /pay command on some servers. Made for nuking and mass paying all players.");
    }

    @Override
    public void onActivate() {
        thread = new Thread(() -> {
            List<PlayerListEntry> players = mc.player.networkHandler.getPlayerList().stream().toList();
            for (PlayerListEntry player : players) {
                String name = player.getProfile().getName();

                if (!(name.equals(mc.player.getGameProfile().getName()))) {
                    if (playerFirstInCommand.get())
                        mc.player.networkHandler.sendChatCommand(commandName.get() + " " + name + " " + moneyToPay.get());
                    else
                        mc.player.networkHandler.sendChatCommand(commandName.get() + " " + moneyToPay.get() + " " + name);
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