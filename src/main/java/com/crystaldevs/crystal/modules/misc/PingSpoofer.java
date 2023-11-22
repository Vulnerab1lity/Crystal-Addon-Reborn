package com.crystaldevs.crystal.modules.misc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;

public class PingSpoofer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> ping = sgGeneral.add(new IntSetting.Builder()
            .name("ping")
            .description("The ping in milliseconds to add to your ping.")
            .defaultValue(250)
            .min(1)
            .sliderMin(100)
            .sliderMax(500)
            .noSlider()
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Object2LongMap<KeepAliveC2SPacket> packets = new Object2LongOpenHashMap<>();

    public PingSpoofer() {
        super(Categories.Misc, "ping-spoofer", "CRYSTAL || Spoof your ping.");
    }

    @Override
    public void onDeactivate() {
        for (KeepAliveC2SPacket packet : packets.keySet())
            mc.player.networkHandler.sendPacket(packet);

        packets.clear();
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        for (KeepAliveC2SPacket packet : packets.keySet()) {
            if (packets.getLong(packet) + (long) ping.get() <= System.currentTimeMillis())
                mc.player.networkHandler.sendPacket(packet);
        }
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof KeepAliveC2SPacket packet) {
            if (!packets.containsKey(packet)) {
                packets.removeLong(packet);
                return;
            }

            packets.put(packet, System.currentTimeMillis());
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}