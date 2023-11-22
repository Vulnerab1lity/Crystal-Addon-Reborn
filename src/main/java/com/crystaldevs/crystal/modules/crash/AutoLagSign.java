package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.mixin.AbstractSignEditScreenAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

public class AutoLagSign extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("The mode for Auto Lag Sign.")
            .defaultValue(Mode.Random)
            .build()
    );

    private final Setting<Boolean> colorChar = sgGeneral.add(new BoolSetting.Builder()
            .name("color-char")
            .description("Attempts to obfuscate the text.")
            .defaultValue(false)
            .build()
    );

    private final Setting<ColorMode> colorMode = sgGeneral.add(new EnumSetting.Builder<ColorMode>()
            .name("color-mode")
            .description("The mode for Color Char.")
            .defaultValue(ColorMode.Vanilla)
            .visible(colorChar::get)
            .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("char-amount")
            .description("How many characters to put on the sign.")
            .defaultValue(80)
            .min(1)
            .sliderMin(1)
            .sliderMax(80)
            .build()
    );

    private final Setting<String> customChar = sgGeneral.add(new StringSetting.Builder()
            .name("custom-char")
            .description("The char to use for Auto Lag Sign.")
            .defaultValue("")
            .visible(() -> mode.get() == Mode.Custom)
            .build()
    );

    public AutoLagSign() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "auto-lag-sign", "Automatically writes lag signs.");
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!(event.screen instanceof SignEditScreen)) return;
        SignBlockEntity sign = ((AbstractSignEditScreenAccessor) event.screen).getSign();
        String signText;
        if (mode.get() != Mode.Custom) signText = mode.get() == Mode.Random ? (colorChar.get() ? (colorMode.get() == ColorMode.Vanilla ? "§§kk" : "&k") + generateMessage() : generateMessage()) : (colorChar.get() ? (colorMode.get() == ColorMode.Vanilla ? "§§kk" : "&k") + "\uFFFF".repeat(amount.get() - (colorMode.get() == ColorMode.Vanilla ? 4 : 2)) : "\uFFFF".repeat(amount.get()));
        else signText = (colorChar.get() ? (colorMode.get() == ColorMode.Vanilla ? "§§kk" : "&k") : "") + customChar.get().substring(0, 1).repeat(amount.get() - (colorChar.get() ? (colorMode.get() == ColorMode.Vanilla ? 4 : 2) : 0));
        mc.player.networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), true, signText, signText, signText, signText));
        event.setCancelled(true);
    }

    private String generateMessage() {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < (colorChar.get() ? amount.get() - (colorMode.get() == ColorMode.Vanilla ? 4 : 2) : amount.get()); i++) message.append((char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1))));
        return message.toString();
    }

    public enum Mode {
        Random,
        FFFF,
        Custom
    }

    public enum ColorMode {
        Vanilla,
        Plugins
    }
}