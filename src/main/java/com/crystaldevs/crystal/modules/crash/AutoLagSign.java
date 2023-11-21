package com.crystaldevs.crystal.modules.crash;

import java.awt.*;
import java.util.Objects;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.mixin.AbstractSignEditScreenAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

public class AutoLagSign extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Mode> mode;
    private final Setting<Boolean> colorChar;
    private final Setting colorMode;
    private final Setting<ColorMode> colorModeSetting;
    private final Setting<Integer> amount;
    private final Setting<String> customChar;

    public AutoLagSign() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Auto Lag Sign", "Automatically writes lag signs.");

        sgGeneral = settings.getDefaultGroup();

        mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("The mode for Auto Lag Sign.")
            .defaultValue(AutoLagSign.Mode.Random)
            .build()
        );

        colorChar = sgGeneral.add(new BoolSetting.Builder()
            .name("Color Char")
            .description("Attempts to obfuscate the text.")
            .defaultValue(false)
            .build()
        );

        colorModeSetting = new EnumSetting.Builder<ColorMode>()
            .name("Color Mode")
            .description("The mode for Color Char.")
            .defaultValue(ColorMode.Vanilla)
            .visible(this.colorChar::get)
            .build();

        this.colorMode = this.sgGeneral.add(colorModeSetting);

        this.amount = this.sgGeneral.add(new IntSetting.Builder()
            .name("Char Amount")
            .description("How many characters to put on the sign.")
            .defaultValue(80)
            .min(1)
            .sliderMin(1)
            .sliderMax(80)
            .build()
        );

        this.customChar = this.sgGeneral.add(new StringSetting.Builder()
            .name("custom-char")
            .description("The char to use for Auto Lag Sign.")
            .defaultValue("")
            .visible(() -> this.mode.get() == Mode.Custom)
            .build()
        );
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!mc.isInSingleplayer()) {
            if (event.screen instanceof SignEditScreen) {
                SignBlockEntity sign = ((AbstractSignEditScreenAccessor) event.screen).getSign();

                String signText;
                if (this.mode.get() != AutoLagSign.Mode.Custom) {
                    if (this.mode.get() == AutoLagSign.Mode.Random) {
                        if ((Boolean) this.colorChar.get()) {
                            signText = (this.colorMode.get() == AutoLagSign.ColorMode.Vanilla ? "§§kk" : "&k") + this.generateMessage();
                        } else {
                            signText = this.generateMessage();
                        }
                    } else {
                        int repeatAmount = (Integer) this.amount.get();
                        if ((Boolean) this.colorChar.get()) {
                            repeatAmount -= (this.colorMode.get() == AutoLagSign.ColorMode.Vanilla ? 4 : 2);
                            signText = (this.colorMode.get() == AutoLagSign.ColorMode.Vanilla ? "§§kk" : "&k") + "\uffff".repeat(repeatAmount);
                        } else {
                            signText = "\uffff".repeat(repeatAmount);
                        }
                    }
                } else {
                    String customChar = (String) this.customChar.get();
                    int repeatAmount = (Integer) this.amount.get() - ((Boolean) this.colorChar.get() ? (this.colorMode.get() == AutoLagSign.ColorMode.Vanilla ? 4 : 2) : 0);
                    signText = ((Boolean) this.colorChar.get() ? (this.colorMode.get() == AutoLagSign.ColorMode.Vanilla ? "§§kk" : "&k") : "") + customChar.substring(0, 1).repeat(repeatAmount);
                }

                if (this.mc.player != null) {
                    UpdateSignC2SPacket updateSignPacket = new UpdateSignC2SPacket(sign.getPos(), true, signText, signText, signText, signText);
                    this.mc.player.networkHandler.sendPacket(updateSignPacket);
                }

                event.setCancelled(true);
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }


    private String generateMessage() {
        StringBuilder message = new StringBuilder();

        for(int i = 0; i < ((Boolean)this.colorChar.get() ? (Integer)this.amount.get() - (this.colorMode.get() == AutoLagSign.ColorMode.Vanilla ? 4 : 2) : (Integer)this.amount.get()); ++i) {
            message.append((char)(19968 + (int)(Math.random() * 20902.0)));
        }

        return message.toString();
    }

    public static enum Mode {
        Random,
        FFFF,
        Custom;

        private Mode() {
        }
    }

    public static enum ColorMode {
        Vanilla,
        Plugins;

        private ColorMode() {
        }
    }
}
