package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DesyncCommand extends Command {
    private Entity entity = null;

    public DesyncCommand() {
        super("desync", "Desyncs yourself or the vehicle you're riding from the server.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (this.entity != null) {
                if (mc.player != null && mc.player.hasVehicle()) {
                    error("You are not riding another entity.");
                }
            } else {
                assert mc.player != null;
                if (!mc.player.hasVehicle()) {
                    error("You are not riding an entity.");
                } else {
                    this.entity = mc.player.getVehicle();

                    mc.player.dismountVehicle();
                    assert mc.world != null;
                    mc.world.removeEntity(this.entity.getId(), Entity.RemovalReason.UNLOADED_TO_CHUNK);

                    info("Successfully desynced your vehicle.");
                }
            }

            return SINGLE_SUCCESS;
        });

        builder.then(literal("entity")).executes(this::run);

        builder.then(literal("player")).executes(context -> {
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new TeleportConfirmC2SPacket(0));
            info("Successfully desynced your player entity.");

            return SINGLE_SUCCESS;
        });
    }

    private int run(CommandContext<CommandSource> context) {
        if (this.entity != null) {
            assert mc.player != null;
            if (mc.player.hasVehicle()) {
                error("You are not riding another entity.");
            } else {
                assert mc.world != null;
                mc.player.startRiding(this.entity, true);

                this.entity = null;

                info("Successfully resynced your vehicle.");
            }
        } else {
            assert mc.player != null;
            if (!mc.player.hasVehicle()) {
                error("You are not riding an entity.");
            } else {
                this.entity = mc.player.getVehicle();

                mc.player.dismountVehicle();
                assert mc.world != null;
                mc.world.removeEntity(this.entity.getId(), Entity.RemovalReason.UNLOADED_TO_CHUNK);

                info("Successfully desynced your vehicle.");
            }
        }

        return SINGLE_SUCCESS;
    }
}
