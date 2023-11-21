package com.crystaldevs.crystal.modules.movement;


import com.crystaldevs.crystal.events.OffGroundSpeedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.Objects;

public class Jetpack extends Module {

    private final Setting<Double> jetpackSpeed;
    private final Setting<Boolean> autoDisable;

    public Jetpack() {
        super(Categories.Movement, "Jetpack", "CRYSTAL || Flies as if using a jetpack.");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        jetpackSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("Jetpack Speed")
            .description("How fast you go while using jetpack.")
            .defaultValue(0.42)
            .min(0)
            .sliderMax(1)
            .build()
        );
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
        );
    }
    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!mc.options.jumpKey.isPressed()) {
            return;
        }
        assert mc.player != null;
        ((IVec3d) Objects.requireNonNull(mc.player).getVelocity()).setY(jetpackSpeed.get());
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    @EventHandler
    private void onOffGroundSpeed(OffGroundSpeedEvent event) {
        assert mc.player != null;
        event.speed = mc.player.getMovementSpeed() * jetpackSpeed.get().floatValue();
    }
}
