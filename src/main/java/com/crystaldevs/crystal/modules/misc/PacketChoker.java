package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PacketChoker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<Class<? extends Packet<?>>>> packets = sgGeneral.add(new PacketListSetting.Builder()
            .name("packets-to-delay")
            .description("The packets to delay.")
            .filter(aClass -> PacketUtils.getC2SPackets().contains(aClass))
            .build()
    );

    private final List<Packet<?>> chokedPackets = new ArrayList<>();

    public PacketChoker() {
        super(Categories.Misc, "Packet Choker", "Chokes packets.");
    }

    @Override
    public void onDeactivate() {
        for (Packet<?> packet : chokedPackets) {
            mc.player.networkHandler.sendPacket(packet);
        }
        chokedPackets.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPacketSend(PacketEvent.Send event) {
        Class<?> clazz = event.packet.getClass();
        if (packets.get().contains(clazz)) {
            chokedPackets.add(event.packet);
            event.setCancelled(true);
        }
    }
}
