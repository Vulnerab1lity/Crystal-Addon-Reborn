package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

public class Invincibility extends Module {
    public Invincibility() {
        super(Categories.Misc, "invincibility", "Attempts to make you invincible on non paper-based servers.");
    }

    @Override
    public void onActivate() {
        info("You must die to become invincible.");
    }

    @Override
    public void onDeactivate() {
        warning("You are no longer invincible.");

        assert this.mc.player != null;

        mc.player.requestRespawn();
    }

    @EventHandler
    private void onPacketReceive(final PacketEvent.Receive event) {
        Packet<?> receivedPacket = event.packet;

        if (receivedPacket instanceof DeathMessageS2CPacket) {
            warning("You are now invincible. Rejoin to be able to play normally.");
            event.setCancelled(true);

            assert mc.player != null;
            mc.player.setHealth(20.0f);
            mc.player.getHungerManager().setFoodLevel(20);
        } else if (receivedPacket instanceof HealthUpdateS2CPacket) {
            event.setCancelled(true);
        } else if (receivedPacket instanceof EntityTrackerUpdateS2CPacket entityTrackerUpdatePacket) {
            assert mc.player != null;

            if (entityTrackerUpdatePacket.id() == mc.player.getId()) {
                event.setCancelled(true);
            }
        }
    }
}
