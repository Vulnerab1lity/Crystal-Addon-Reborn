package com.crystaldevs.crystal.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

import java.util.stream.IntStream;

public class BoatFling extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> speed;
    private final Setting<Double> updateAmount;

    public BoatFling() {
        super(Categories.Movement, "Boat Fling", "CRYSTAL || Allows you to fling using boats.");

        this.sgGeneral = this.settings.getDefaultGroup();

        this.speed = this.sgGeneral.add((new DoubleSetting.Builder())
            .name("Speed")
            .description("How fast to fling you.")
            .defaultValue(4.0)
            .min(0.0)
            .sliderMax(10.0)
            .build());

        this.updateAmount = this.sgGeneral.add((new DoubleSetting.Builder())
            .name("Update Amount")
            .description("How much to update your position per tick.")
            .defaultValue(10.0)
            .min(0.0)
            .sliderMax(20.0)
            .build());
    }

    public void onActivate() {
        if (mc.player != null && !(mc.player.getVehicle() instanceof BoatEntity)) {
            error("You must be in a boat to use this.");
            toggle();
        }

    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        assert mc.player != null;
        if (!(mc.player.getVehicle() instanceof BoatEntity)) {
            error("You must be in a boat to use this.");
            toggle();
        } else {
            IntStream.iterate(0, i -> (double) i < this.speed.get(), i -> {
                return i + 1;
            }).forEach((int i) -> {
                this.mc.player.getVehicle().updatePosition(mc.player.getVehicle().getX(), mc.player.getVehicle().getY() + updateAmount.get(), mc.player.getVehicle().getZ());
                this.mc.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(mc.player.getVehicle()));
                this.mc.player.getVehicle().setVelocity(0.0, 0.0, 0.0);
            });

        }
    }
}
