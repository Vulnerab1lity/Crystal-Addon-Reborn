package com.crystaldevs.crystal.modules.movement;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Boost extends Module {

    private final Setting<Double> strength;

    private final Setting<Boolean> autoBoost;

    private final Setting<Integer> interval;
    private final Setting<Boolean> autoDisable;

    private int timer = 0;

    public Boost() {
        super(Categories.Movement, "boost", "CRYSTAL || Works like a dash move.");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        strength = sgGeneral.add(new DoubleSetting.Builder()
            .name("Strength")
            .description("Strength to yeet you with.")
            .defaultValue(4.0)
            .sliderMax(10)
            .build()
        );
        autoBoost = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Boost")
            .description("Automatically boosts you.")
            .defaultValue(false)
            .build()
        );
        interval = sgGeneral.add(new IntSetting.Builder()
            .name("Interval")
            .description("Boost interval in ticks.")
            .visible(autoBoost::get)
            .defaultValue(20)
            .sliderMax(120)
            .build()
        );
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    @Override
    public void onActivate() {
        timer = interval.get();
        if (!autoBoost.get()) {
            if (mc.player == null) {
                error("Player is null, toggling.");
            } else if (mc.world == null) {
                error("World is null, toggling.");
            }
            else {
                boost();
            }
            this.toggle();
        } else {
            return;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!autoBoost.get()) {
            return;
        } else {
            if (timer < 1) {
                boost();
                timer = interval.get();
            } else {
                timer--;
            }
        }
    }

    private void boost() {
        assert mc.player != null;
        Vec3d v = Objects.requireNonNull(mc.player).getRotationVecClient().multiply(strength.get());
        mc.player.addVelocity(v.getX(), v.getY(), v.getZ());
    }
}
