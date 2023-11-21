package com.crystaldevs.crystal.modules.player;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;

public class GhostMode extends Module {

    private final Setting<Boolean> fullFood;
    private final Setting<Boolean> autoDisable;

    public GhostMode() {
        super(Categories.Player, "ghost-mode", "CRYSTAL || Allows you to continue playing after you die, by desyncing you and changing the health.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        fullFood = sgGeneral.add(new BoolSetting.Builder()
            .name("full-food")
            .description("Sets the food level client-side to max.")
            .defaultValue(true)
            .build()
        );

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
        );
    }

    private boolean active = false;

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        active = false;
        warning("You are no longer in a ghost mode!");
        if (mc.player == null || mc.player.networkHandler == null) {
            return;
        }
        mc.player.requestRespawn();
        info("Respawn request has been sent to the server.");
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        active = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (active) {
            assert mc.player != null;
            if (mc.player.getHealth() < 1f) {
                mc.player.setHealth(20f);
            }
            if (fullFood.get() && (mc.player.getHungerManager().getFoodLevel() < 20)) {
                mc.player.getHungerManager().setFoodLevel(20);
            }
        }
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (event.screen instanceof DeathScreen) {
            event.cancel();
            if (!active) {
                active = true;
                info("You are now in a ghost mode. ");
            } else {
                return;
            }
        } else {
            return;
        }
    }

}
