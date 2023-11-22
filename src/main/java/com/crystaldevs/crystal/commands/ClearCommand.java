package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.command.CommandSource;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear-inv", "Clears your inventory.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            warning("Are you sure that you want to clear your inventory? if yes, use " + Config.get().prefix.get() + "clear-inventory confirm.");
            return SINGLE_SUCCESS;
        });
        builder.then(literal("confirm").executes(ctx -> {
            ScreenHandler handler = mc.player.currentScreenHandler;
            for (int i = 9; i < 45; i++) {
                Slot slot = handler.getSlot(i);
                if (slot.getStack().isEmpty()) continue;
                mc.interactionManager.clickSlot(handler.syncId, slot.id, 120, SlotActionType.SWAP, mc.player);
            }
            return SINGLE_SUCCESS;
        }));
    }
}