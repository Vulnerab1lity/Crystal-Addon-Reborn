package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class SecretClose extends Module {
    public SecretClose() {
        super(Categories.Misc, "SecretClose", "CRYSTAL || Does not send a CloseHandledScreen packet.");
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof CloseHandledScreenC2SPacket) {
            event.setCancelled(true);
        }
    }
}
