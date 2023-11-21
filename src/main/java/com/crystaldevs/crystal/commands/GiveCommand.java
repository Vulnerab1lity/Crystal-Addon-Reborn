package com.crystaldevs.crystal.commands;


import com.crystaldevs.crystal.commands.arguements.EnumStringArgumentType;
import com.crystaldevs.crystal.utils.mc.GiveUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class GiveCommand extends Command {

    private final Collection<String> PRESETS = GiveUtils.PRESETS.keySet();

    public GiveCommand() {
        super("give", "Gives items in creative", "item", "kit");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> then = builder.then(literal("egg").executes(ctx -> {
            assert mc.player != null;
            ItemStack inHand = mc.player.getMainHandStack();
            ItemStack item = new ItemStack(Items.STRIDER_SPAWN_EGG);
            NbtCompound ct = new NbtCompound();
            if (!(inHand.getItem() instanceof BlockItem)) {
                ct.putString("id", "minecraft:item");
                NbtCompound it = new NbtCompound();
                it.putString("id", Registries.ITEM.getId(inHand.getItem()).toString());
                it.putInt("Count", inHand.getCount());
                if (!inHand.hasNbt()) {
                } else {
                    it.put("tag", inHand.getNbt());
                }
                ct.put("Item", it);
            } else {
                ct.putInt("Time", 1);
                ct.putString("id", "minecraft:falling_block");
                ct.put("BlockState", new NbtCompound());
                ct.getCompound("BlockState").putString("Name", Registries.ITEM.getId(inHand.getItem()).toString());
                if (!inHand.hasNbt() || !Objects.requireNonNull(inHand.getNbt()).contains("BlockEntityTag")) {
                } else {
                    ct.put("TileEntityData", inHand.getNbt().getCompound("BlockEntityTag"));
                }
                NbtCompound t = new NbtCompound();
                t.put("EntityTag", ct);
                item.setNbt(t);
            }
            NbtCompound t = new NbtCompound();
            t.put("EntityTag", ct);
            item.setNbt(t);
            item.setCustomName(inHand.getName());
            GiveUtils.giveItem(item);
            return SINGLE_SUCCESS;
        }));

        LiteralArgumentBuilder<CommandSource> then1 = builder.then(literal("holo").then(argument("message", StringArgumentType.greedyString()).executes(ctx -> {
            String message = ctx.getArgument("message", String.class).replace("&", "\247");
            ItemStack stack = new ItemStack(Items.ARMOR_STAND);
            NbtCompound tag = new NbtCompound();
            NbtList NbtList = new NbtList();
            assert mc.player != null;
            NbtList.add(NbtDouble.of(mc.player.getX()));
            NbtList.add(NbtDouble.of(mc.player.getY()));
            NbtList.add(NbtDouble.of(mc.player.getZ()));
            tag.putBoolean("Invisible", true);
            tag.putBoolean("Invulnerable", true);
            tag.putBoolean("Interpret", true);
            tag.putBoolean("NoGravity", true);
            tag.putBoolean("CustomNameVisible", true);
            tag.putString("CustomName", Text.Serializer.toJson(Text.literal(message)));
            tag.put("Pos", NbtList);
            stack.setSubNbt("EntityTag", tag);
            GiveUtils.giveItem(stack);
            return SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<CommandSource> then2 = builder.then(literal("bossbar").then(argument("message", StringArgumentType.greedyString()).executes(ctx -> {
            String message = ctx.getArgument("message", String.class).replace("&", "\247");
            ItemStack stack = new ItemStack(Items.BAT_SPAWN_EGG);
            NbtCompound tag = new NbtCompound();
            tag.putString("CustomName", Text.Serializer.toJson(Text.literal(message)));
            tag.putBoolean("NoAI", true);
            tag.putBoolean("Silent", true);
            tag.putBoolean("PersistenceRequired", true);
            tag.putBoolean("Invisible", true);
            tag.put("id", NbtString.of("minecraft:wither"));
            stack.setSubNbt("EntityTag", tag);
            GiveUtils.giveItem(stack);
            return SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<CommandSource> then3 = builder.then(literal("head").then(argument("owner", StringArgumentType.greedyString()).executes(ctx -> {
            String playerName = ctx.getArgument("owner", String.class);
            ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
            NbtCompound tag = new NbtCompound();
            tag.putString("SkullOwner", playerName);
            itemStack.setNbt(tag);
            GiveUtils.giveItem(itemStack);
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("preset").then(argument("name", new EnumStringArgumentType(PRESETS)).executes(context -> {
            String name = context.getArgument("name", String.class);
            GiveUtils.giveItem(GiveUtils.getPreset(name));
            return SINGLE_SUCCESS;
        })));
    }
}
