package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MassPayout extends Module {
    private final Setting<Integer> moneyToPay;
    private final Setting<Boolean> playerFirstInCommand;
    private final Setting<String> commandName;
    private final Setting<Integer> delay;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MassPayout() {
        super(Categories.Misc, "Mass Payout", "CRYSTAL || A module to use the /pay command on some servers. Made for nuking and mass paying all players.");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        moneyToPay = sgGeneral.add(new IntSetting.Builder()
            .name("Money to pay")
            .description("The total amount of money to payout between all players.")
            .defaultValue(10000)
            .min(10)
            .max(100000000)
            .sliderMin(10)
            .sliderMax(100000000)
            .noSlider()
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
        playerFirstInCommand = sgGeneral.add(new BoolSetting.Builder()
            .name("Player First in command")
            .description("If the player arguement is first in the command or not, like /pay {player} {amount}.")
            .defaultValue(true)
            .build());
        commandName = sgGeneral.add(new StringSetting.Builder()
            .name("Command name")
            .description("The name of the command that will be used to pay. Do NOT include a '/'.")
            .defaultValue("pay")
            .build());
    }

    public ArrayList<PlayerListEntry> getPlayerList() {
        ArrayList<PlayerListEntry> playerList = new ArrayList<>();

        if (mc.world != null) {
            Collection<PlayerListEntry> players = Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList();
            playerList.addAll(players);
        }

        return playerList;
    }

    public void payPlayers() throws InterruptedException {
        if(!mc.isInSingleplayer()) {
            List<PlayerListEntry> playerList = getPlayerList();
            int numPlayers = playerList.size();

            if (numPlayers == 0) {
                return;
            }

            int moneyPerPlayer = moneyToPay.get() / numPlayers;
            int moneyLeftover = moneyToPay.get() % numPlayers;

            for (int i = 0; i < numPlayers; i++) {
                PlayerListEntry player = playerList.get(i);
                String playerName = player.getProfile().getName();

                assert mc.player != null;
                if(Text.of(playerName) != mc.player.getName()) {
                    int payment = moneyPerPlayer;

                    if (i < moneyLeftover) {
                        payment += 1;
                    }

                    String result = commandName.get().replaceAll("\\s", "");
                    if(playerFirstInCommand.get()) {
                        ChatUtils.sendPlayerMsg("/" + result + " " + playerName + " " + payment);
                        Thread.sleep(IntToLongMs_Convertor(delay.get()));
                    } else {
                        ChatUtils.sendPlayerMsg("/" + result + " "  + payment + " " + playerName);
                    }
                    ChatUtils.sendMsg(Text.of("Attempted to pay user: " + playerName + " amount: $" + payment + "."));
                }

            }
            toggle();
        } else {
            error("You must be on a server! Toggling.");
            toggle();
        }

    }

    public static long IntToLongMs_Convertor(int seconds) {
        return (long) seconds * 1000;
    }

    @Override
    public void onActivate() {
        executor.execute(() -> {
            try {
                payPlayers();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
