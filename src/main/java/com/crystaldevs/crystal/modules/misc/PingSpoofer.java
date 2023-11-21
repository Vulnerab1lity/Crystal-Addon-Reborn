package com.crystaldevs.crystal.modules.misc;

import com.crystaldevs.crystal.CrystalAddon;
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

import java.util.HashSet;

public class PingSpoofer extends Module {


    private final Setting<Integer> ping;
    private final Setting<Boolean> autoDisable;



    private final Object2LongMap<KeepAliveC2SPacket> packets = new Object2LongOpenHashMap<>();


    public PingSpoofer() {
        super(Categories.Misc, "Ping Spoofer", "CRYSTAL || Spoof your ping.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        ping = sgGeneral.add(new IntSetting.Builder()
            .name("ping")
            .description("The ping in milliseconds to add to your ping.")
            .defaultValue(250)
            .min(1)
            .sliderMin(100)
            .sliderMax(500)
            .noSlider()
            .build()
        );

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Automatically disables the module on server kick.")
            .defaultValue(true)
            .build()
        );
    }


    @Override
    public void onActivate() {
        this.packets.clear();
    }

    public void onDeactivate() {
        try {
            if(mc.getNetworkHandler() != null)
            {
                if (!this.packets.isEmpty()) {
                    new HashSet<>(this.packets.keySet()).stream().filter(packet -> this.packets.getLong(packet) + (long) this.ping.get() <= System.currentTimeMillis()).forEachOrdered(packet -> mc.getNetworkHandler().sendPacket(packet));
                }
            }

        } catch(NullPointerException NPE) {
            CrystalAddon.LOG.error("NullPointerException for PingSpoof onDeactivate: ", NPE);
        }

    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        try {
            if(mc.getNetworkHandler() != null)
            {
                for (KeepAliveC2SPacket packet : new HashSet<>(this.packets.keySet())) {
                    if (this.packets.getLong(packet) + (long) this.ping.get() <= System.currentTimeMillis()) {
                        mc.getNetworkHandler().sendPacket(packet);
                        break;
                    }
                }
            }

        } catch(NullPointerException NPE) {
            CrystalAddon.LOG.error("NullPointerException for PingSpoof onReceivePacket: ", NPE);
        }

    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        try {
            if(mc.getNetworkHandler() != null)
            {
                if (event.packet instanceof KeepAliveC2SPacket packet) {
                    if (!this.packets.isEmpty() && new HashSet<>(this.packets.keySet()).contains(packet)) {
                        this.packets.removeLong(packet);
                        return;
                    }

                    this.packets.put(packet, System.currentTimeMillis());

                    event.cancel();
                }
            }

        } catch(NullPointerException NPE) {
            CrystalAddon.LOG.error("NullPointerException for PingSpoof onSendPacket: ", NPE);
        }

    }


    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }
}
