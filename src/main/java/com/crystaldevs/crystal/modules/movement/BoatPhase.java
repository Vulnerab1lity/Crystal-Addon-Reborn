package com.crystaldevs.crystal.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.vehicle.BoatEntity;

public class BoatPhase extends Module {
    private BoatEntity boat = null;

    public BoatPhase() {
        super(Categories.Movement, "boat-phase", "CRYSTAL || Allows you to phase through blocks using a boat.");
    }

    @Override
    public void onActivate() {
        if (mc.player == null) return;
        if (!(mc.player.getVehicle() instanceof BoatEntity)) {
            error("You must be in a boat to use ");
            toggle();
        } else {
            boat = (BoatEntity) mc.player.getVehicle();
            boat.noClip = true;
            boat.setNoGravity(true);
        }
    }

    @Override
    public void onDeactivate() {
        if (boat != null) {
            boat.noClip = false;
            boat.setNoGravity(false);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!(mc.player.getVehicle() instanceof BoatEntity))
            toggle();
    }
}