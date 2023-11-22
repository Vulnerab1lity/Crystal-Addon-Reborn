package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.MiddleClickExtra;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class FakeHacker extends Module {
    private PlayerEntity target = null;

    public FakeHacker() {
        super(Categories.Misc, "fake-hacker", "CRYSTAL || Makes it seem like another player is hacking.");
    }

    @Override
    public void onActivate() {
        if (Modules.get().isActive(MiddleClickExtra.class)) warning("You have Middle Click Extra enabled, overriding.");
    }

    @Override
    public void onDeactivate() {
        target = null;
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (mc.currentScreen != null) return;
        if (event.button == GLFW_MOUSE_BUTTON_MIDDLE && event.action == KeyAction.Press) {
            if (mc.crosshairTarget instanceof EntityHitResult ehr
                    && ehr.getEntity() instanceof PlayerEntity player)
                target = player;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (target == null) return;
        for (Entity entity : mc.world.getEntities()) {
            if (entity == target) continue;
            if (entity.isAttackable() && entity.distanceTo(target) < 4) {
                target.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,
                        entity.getPos().add(0, entity.getHeight() / 2, 0));
                target.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    @Override
    public String getInfoString() {
        return target == null ? null : target.getEntityName();
    }
}
