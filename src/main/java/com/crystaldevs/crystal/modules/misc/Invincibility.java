package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

public class Invincibility extends Module {
    public Invincibility() {
        super(Categories.Misc, "invincibility", "CRYSTAL || Attempts to make you invincible on non paper-based servers.");
    }

    @Override
    public void onActivate() {
        info("You must die to become invincible.");
    }

    @Override
    public void onDeactivate() {
        warning("You are no longer invincible.");
        mc.player.requestRespawn();
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Receive event) {
        if (event.packet instanceof DeathMessageS2CPacket) {
            warning("You are now invincible - rejoin to be able to play normally.");
            event.setCancelled(true);
            mc.player.setHealth(20f);
            mc.player.getHungerManager().setFoodLevel(20);
        }
        if (event.packet instanceof HealthUpdateS2CPacket) event.setCancelled(true);
        if (event.packet instanceof EntityTrackerUpdateS2CPacket packet && packet.id() == mc.player.getId())
            event.setCancelled(true);
    }
}