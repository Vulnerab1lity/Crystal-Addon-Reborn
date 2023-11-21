package com.crystaldevs.crystal.utils;

import com.crystaldevs.crystal.utils.crystal.config.CrystalConfig;
import com.crystaldevs.crystal.utils.mc.seeds.Seeds;
import io.netty.channel.Channel;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixin.ClientConnectionAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.MathHelper;


import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class Utils {
    public static boolean crashing = false;
    public static Channel nettyChannel() {
        if (mc.player != null) {
            if (mc.world != null) {
                if (mc.getNetworkHandler() != null) {
                    return ((ClientConnectionAccessor) mc.getNetworkHandler().getConnection()).getChannel();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static ItemStack generateItemWithNbt(final String nbt, final Item item) {
        try {
            final ItemStack stack = new ItemStack((ItemConvertible)item);
            stack.setNbt(StringNbtReader.parse(nbt));
            return stack;
        }
        catch (Exception ignored) {
            return new ItemStack((ItemConvertible)item);
        }
    }
    public static String yesno(boolean b) {
        return b ? "yes" : "no";
    }
    public static String printif(String s, boolean b) {
        return b ? s : "";
    }


    public static boolean isInt(String s) {
        return isInt(s, true);
    }
    public static boolean isUint(String s) {
        return isInt(s, false);
    }

    private static boolean isInt(String s, boolean negative) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        try {
            if (!negative || !s.startsWith("-")) {
                Integer.parseInt(s);
            } else {
                Integer.parseInt(s.substring(1));
            }
            return true;
        }catch (NumberFormatException e) {return false;}
    }

    public static int getInt(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                if (!s.startsWith("-")) {
                    return Integer.parseInt(s);
                } else {
                    return -Integer.parseInt(s.substring(1));
                }
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @PostInit
    public static void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("saving seeds...");
            CrystalConfig.get().save(MeteorClient.FOLDER);
            Seeds.get().save(MeteorClient.FOLDER);
        }));
    }

    public static String getModuleName(String name) {
        int dupe = 0;
        for (Iterator<Module> iterator = Modules.get().getAll().iterator(); iterator.hasNext(); ) {
            Module module = iterator.next();
            if (!module.name.equals(name)) {
            } else {
                dupe++;
                break;
            }
        }
        return dupe == 0 ? name : getModuleName(name + "*".repeat(dupe));
    }

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_!@#$%^&*()-+=<>?";

    public static String generateRandomPassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than zero.");
        }

        if(length > 100) {
            throw new IllegalArgumentException("Password length must be smaller than 100.");
        }

        SecureRandom random = new SecureRandom();
        return IntStream.range(0, length)
            .mapToObj(i -> String.valueOf(CHARACTERS.charAt(random.nextInt(CHARACTERS.length()))))
            .collect(Collectors.joining());
    }

}
