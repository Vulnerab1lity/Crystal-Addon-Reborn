package com.crystaldevs.crystal.modules.player;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;

public class VehicleOneHit extends Module {

    private final Setting<Integer> amount;
    private final Setting<Boolean> autoDisable;

    private boolean ignorePackets;

    public VehicleOneHit() {
        super(Categories.Player, "Vehicle One Hit", "CRYSTAL || Destroy vehicles with one hit.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        amount = sgGeneral.add(new IntSetting.Builder()
            .name("Amount")
            .description("The number of packets to send.")
            .defaultValue(16)
            .range(1, 100)
            .sliderRange(1, 20)
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
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return;
        }
        if (!ignorePackets)
            if (event.packet instanceof PlayerInteractEntityC2SPacket)
                if (mc.crosshairTarget instanceof EntityHitResult ehr)
                    if (ehr.getEntity() instanceof AbstractMinecartEntity) {
                        ignorePackets = true;
                        int i = 0;
                        if (i < amount.get() - 1) {
                            mc.player.networkHandler.sendPacket(event.packet);
                            i++;
                            while (true) {
                                if (i < amount.get() - 1) {
                                    mc.player.networkHandler.sendPacket(event.packet);
                                    i++;
                                } else {
                                    break;
                                }
                            }
                        }
                        ignorePackets = false;
                    } else if (ehr.getEntity() instanceof BoatEntity) {
                        ignorePackets = true;
                        int i = 0;
                        if (i < amount.get() - 1) {
                            mc.player.networkHandler.sendPacket(event.packet);
                            i++;
                            while (true) {
                                if (i < amount.get() - 1) {
                                    mc.player.networkHandler.sendPacket(event.packet);
                                    i++;
                                } else {
                                    break;
                                }
                            }
                        }
                        ignorePackets = false;
                    }

    }
}
