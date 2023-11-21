package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.commands.arguements.ClientPosArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.Objects;

import static com.crystaldevs.crystal.commands.arguements.ClientPosArgumentType.mc;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class CenterCommand extends Command {
    public CenterCommand() {
        super("center", "Centers you on the block you are currently standing on.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            double x = 0;
            if (mc.player != null) {
                x = mc.player.getBlockX() + 0.5D;
            }
            double z = mc.player.getBlockZ() + 0.5D;
            mc.player.setVelocity(0.0D, 0.0D, 0.0D);
            mc.player.setPosition(x, mc.player.getY(), z);
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, mc.player.getY(), z, mc.player.isOnGround()));

            return SINGLE_SUCCESS;
        });
    }
}
