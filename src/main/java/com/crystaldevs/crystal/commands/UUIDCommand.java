package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.commands.arguements.PlayerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class UUIDCommand extends Command {
    public UUIDCommand() {
        super("uuid", "Returns a players uuid.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> executes = builder.executes(context -> {
            if (mc.player != null) {
                info("Your UUID is " + mc.player.getUuid().toString());
            } else {
                error("Failed to grab UUID, the player is null.");
            }

            return SINGLE_SUCCESS;
        });

        LiteralArgumentBuilder<CommandSource> then = builder.then(argument("player", PlayerArgumentType.player()).executes(context -> {
            PlayerEntity player = PlayerArgumentType.getPlayer(context, "player");

            if (player == null) {
                return SINGLE_SUCCESS;
            }
            info(player.getEntityName() + "'s UUID is " + player.getUuid().toString());

            return SINGLE_SUCCESS;
        }));
    }
}
