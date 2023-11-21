package com.crystaldevs.crystal.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.vehicle.BoatEntity;

public class BoatPhase extends Module {
    private BoatEntity boat = null;

    public BoatPhase() {
        super(Categories.Movement, "Boat Phase", "CRYSTAL || Allows you to phase through blocks using a boat.");
    }

    public void onActivate() {
        assert this.mc.player != null;
        if (!(this.mc.player.getVehicle() instanceof BoatEntity)) {
            this.error("You must be in a boat to use this.");
            this.toggle();
        } else {
            this.boat = (BoatEntity)this.mc.player.getVehicle();
            this.boat.noClip = true;
            this.boat.setNoGravity(true);
            this.mc.player.noClip = true;
        }
    }

    public void onDeactivate() {
        if (this.mc.player != null) {
            this.mc.player.noClip = false;
        }
        if (this.boat != null) {
            this.boat.noClip = false;
            this.boat.setNoGravity(false);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (this.mc.player != null && !(this.mc.player.getVehicle() instanceof BoatEntity)) {
            this.toggle();
        }

    }
}
