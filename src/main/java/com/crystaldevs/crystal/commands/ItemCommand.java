package com.crystaldevs.crystal.commands;

import com.crystaldevs.crystal.utils.mc.McUtils;
import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ItemCommand extends Command {
    public ItemCommand() {
        super("item", "Allows you to put any item in any slot in your inventory.", "move");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        List<RequiredArgumentBuilder<CommandSource, Integer>> asList = Arrays.asList(argument("slot", IntegerArgumentType.integer()).executes(context -> {
            clickSlot(context.getArgument("slot", Integer.class));

            return SINGLE_SUCCESS;
        }), argument("from", IntegerArgumentType.integer()).then(argument("to", IntegerArgumentType.integer()).executes(context -> {
            clickSlot(0, context.getArgument("from", Integer.class), context.getArgument("to", Integer.class));

            return SINGLE_SUCCESS;
        })));
        for (RequiredArgumentBuilder<CommandSource, Integer> commandSourceIntegerRequiredArgumentBuilder : asList) {
            builder.then(commandSourceIntegerRequiredArgumentBuilder);
        }

        LiteralArgumentBuilder<CommandSource> then = builder.then(literal("bypass").then(argument("from", IntegerArgumentType.integer()).then(argument("to", IntegerArgumentType.integer()).executes(context -> {
            clickSlot(mc.player != null && mc.player.currentScreenHandler != null ? mc.player.currentScreenHandler.syncId : 0, context.getArgument("from", Integer.class), context.getArgument("to", Integer.class));

            return SINGLE_SUCCESS;
        }))));

        LiteralArgumentBuilder<CommandSource> then1 = builder.then(literal("head").executes(context -> {
            clickSlot(39);

            return SINGLE_SUCCESS;
        }));

        LiteralArgumentBuilder<CommandSource> then2 = builder.then(literal("chest").executes(context -> {
            clickSlot(38);

            return SINGLE_SUCCESS;
        }));

        LiteralArgumentBuilder<CommandSource> then3 = builder.then(literal("leggings").executes(context -> {
            clickSlot(37);

            return SINGLE_SUCCESS;
        }));

        LiteralArgumentBuilder<CommandSource> then4 = builder.then(literal("boots").executes(context -> {
            clickSlot(36);

            return SINGLE_SUCCESS;
        }));
    }


    private void clickSlot(int slot) {
        assert mc.player != null;
        clickSlot(0, 36 + Objects.requireNonNull(McUtils.getInventory()).selectedSlot, slot);
    }

    private void clickSlot(int syncId, int id, int button) {
        if (id <= 0 || button <= 0) {
            return;
        }
        assert mc.player != null;
        ScreenHandler handler = mc.player.currentScreenHandler;

        DefaultedList<Slot> slots = handler.slots;
        int i = slots.size();
        List<ItemStack> list = Lists.newArrayListWithCapacity(i);

        for (Slot slot : slots) {
            list.add(slot.getStack().copy());
        }

        try {
            handler.onSlotClick(id, button, SlotActionType.SWAP, mc.player);
        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }

        Int2ObjectMap<ItemStack> stacks = new Int2ObjectOpenHashMap<>();

        IntStream.range(0, i).forEach(slot -> {
            ItemStack stack1 = list.get(slot);
            ItemStack stack2 = slots.get(slot).getStack();
            if (ItemStack.areEqual(stack1, stack2)) {
                return;
            }
            stacks.put(slot, stack2.copy());
        });

        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new ClickSlotC2SPacket(syncId, handler.getRevision(), id, button, SlotActionType.SWAP, handler.getCursorStack().copy(), stacks));
    }
}
