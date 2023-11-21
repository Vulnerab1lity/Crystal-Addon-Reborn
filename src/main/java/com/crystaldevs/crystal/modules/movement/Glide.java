package com.crystaldevs.crystal.modules.movement;


import com.crystaldevs.crystal.events.OffGroundSpeedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;


public class Glide extends Module {
    public final Setting<Double> fallSpeed;
    public final Setting<Double> moveSpeed;
    public final Setting<Double> minHeight;
    private final Setting<Boolean> autoDisable;
    public Glide() {
        super(Categories.Movement, "glide", "CRYSTAL || You know what this is!");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        fallSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("Fall speed")
            .description("Fall speed.")
            .defaultValue(0.125)
            .min(0.005)
            .sliderRange(0.005, 0.25)
            .build()
        );
        moveSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("Move speed")
            .description("Horizontal movement factor.")
            .defaultValue(1.2)
            .min(1)
            .sliderRange(1, 5)
            .build()
        );
        minHeight = sgGeneral.add(new DoubleSetting.Builder()
            .name("Minimum height")
            .description("Won't glide when you are too close to the ground.")
            .defaultValue(0)
            .min(0)
            .sliderRange(0, 2)
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
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;
        assert player != null;
        Vec3d v = player.getVelocity();

        if (!player.isOnGround()) {
            if (!player.isTouchingWater()) {
                if (!player.isInLava()) {
                    if (!player.isClimbing()) {
                        if (!(v.y >= 0)) {
                            if (0 < minHeight.get()) {
                                Box box = player.getBoundingBox();
                                box = box.union(box.offset(0, -minHeight.get(), 0));
                                assert mc.world != null;
                                if (!mc.world.isSpaceEmpty(box)) return;
                            }

                            player.setVelocity(v.x, Math.max(v.y, -fallSpeed.get()), v.z);
                        }
                    }
                }
            }
        }

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
        event.speed = event.speed * moveSpeed.get().floatValue();
    }
}
