package com.crystaldevs.crystal.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.LivingEntityAccessor;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class NoJumpCooldown extends Module {
    public NoJumpCooldown() {
        super(Categories.Movement, "no-jump-cooldown", "Removes the jump cooldown.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player != null) ((LivingEntityAccessor) mc.player).setJumpCooldown(0);
    }
}