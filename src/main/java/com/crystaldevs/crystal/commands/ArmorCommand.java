package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.screen.slot.SlotActionType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ArmorCommand extends Command {
    public ArmorCommand() {
        super("armor", "Allows you to move an item to your armor slot.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("helmet").executes(ctx -> {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, mc.player);
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("chestplate").executes(ctx -> {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.getInventory().selectedSlot, 38, SlotActionType.SWAP, mc.player);
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("leggings").executes(ctx -> {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.getInventory().selectedSlot, 37, SlotActionType.SWAP, mc.player);
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("boots").executes(ctx -> {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.getInventory().selectedSlot, 36, SlotActionType.SWAP, mc.player);
            return SINGLE_SUCCESS;
        }));
    }
}