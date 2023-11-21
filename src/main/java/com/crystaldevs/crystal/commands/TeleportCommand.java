package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.commands.arguements.PositionArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class TeleportCommand extends Command {
    public TeleportCommand() {
        super("teleport", "Allows to teleport small distances.", "tp");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("pos", PositionArgumentType.pos()).executes(context -> {
            return teleport(context, "pos", 0, 0, false);
        }));

        builder.then(argument("pos", PositionArgumentType.pos()).then(argument("yaw", FloatArgumentType.floatArg()).then(argument("pitch", FloatArgumentType.floatArg()).executes(context -> {
            return teleport(context, "pos", FloatArgumentType.getFloat(context, "yaw"), FloatArgumentType.getFloat(context, "pitch"), true);
        }))));

        builder.then(argument("pos", PositionArgumentType.pos()).then(argument("ticks", IntegerArgumentType.integer(0)).executes(context -> {
            return teleportRepeatedly(context, "pos", 0, 0, IntegerArgumentType.getInteger(context, "ticks"), false);
        })));

        builder.then(argument("pos", PositionArgumentType.pos()).then(argument("yaw", FloatArgumentType.floatArg()).then(argument("pitch", FloatArgumentType.floatArg()).then(argument("ticks", IntegerArgumentType.integer(0)).executes(context -> {
            return teleportRepeatedly(context, "pos", FloatArgumentType.getFloat(context, "yaw"), FloatArgumentType.getFloat(context, "pitch"), IntegerArgumentType.getInteger(context, "ticks"), true);
        })))));
    }

    private int teleport(CommandContext<CommandSource> context, String posArg, float yaw, float pitch, boolean updateAngles) {
        Vec3d pos = PositionArgumentType.getPos(context, posArg);
        assert mc.player != null;

        if (mc.player.hasVehicle()) {
            Entity vehicle = mc.player.getVehicle();
            assert vehicle != null;
            vehicle.setPosition(pos.getX(), pos.getY(), pos.getZ());
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new VehicleMoveC2SPacket(vehicle));
        } else {
            mc.player.updatePosition(pos.getX(), pos.getY(), pos.getZ());
            if (updateAngles) {
                mc.player.updatePositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
            }
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
        }

        return SINGLE_SUCCESS;
    }

    private int teleportRepeatedly(CommandContext<CommandSource> context, String posArg, float yaw, float pitch, int ticks, boolean updateAngles) {
        for (int i = 0; i < Math.max(1, ticks); i++) {
            teleport(context, posArg, yaw, pitch, updateAngles);
        }
        return SINGLE_SUCCESS;
    }
}
