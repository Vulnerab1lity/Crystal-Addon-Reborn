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

public class BoatFling extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add((new DoubleSetting.Builder())
            .name("speed")
            .description("How fast to fling you.")
            .defaultValue(4.0)
            .min(0.0)
            .sliderMax(10.0)
            .build()
    );

    private final Setting<Double> updateAmount = sgGeneral.add((new DoubleSetting.Builder())
            .name("update-amount")
            .description("How many times to update your position per tick.")
            .defaultValue(10.0)
            .min(0.0)
            .sliderMax(20.0)
            .build()
    );

    public BoatFling() {
        super(Categories.Movement, "Boat Fling", "CRYSTAL || Allows you to fling using boats.");
    }

    @Override
    public void onActivate() {
        if (mc.player != null && !(mc.player.getVehicle() instanceof BoatEntity)) {
            error("You must be in a boat to use this.");
            toggle();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!(mc.player.getVehicle() instanceof BoatEntity)) {
            error("You must be in a boat to use this.");
            toggle();
        } else {
            mc.player.getVehicle().setVelocity(0.0, 0.0, 0.0);
            for (int i = 0; i < speed.get(); i++) {
                mc.player.getVehicle().updatePosition(mc.player.getVehicle().getX(), mc.player.getVehicle().getY() + updateAmount.get(), mc.player.getVehicle().getZ());
                mc.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(mc.player.getVehicle()));
            }
        }
    }
}