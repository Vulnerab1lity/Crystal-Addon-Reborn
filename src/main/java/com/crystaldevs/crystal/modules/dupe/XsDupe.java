package com.crystaldevs.crystal.modules.dupe;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.text.Text;

import java.util.Objects;

public class XsDupe extends Module {

    private final Setting<Boolean> autoDisable;
    private final Setting<Boolean> sendMsg;
    public XsDupe() {
        super(CrystalAddon.CRYSTAL_DUPE_CATEGORY.get(), "XsDupe", "CRYSTAL || Attempts to dupe by desyncing and doing multiple actions.");
        SettingGroup sgGeneral = settings.getDefaultGroup();

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
        );
        sendMsg = sgGeneral.add(new BoolSetting.Builder()
            .name("send-message")
            .description("Sends a message when duping to the server.")
            .defaultValue(true)
            .build()
        );
    }

    @EventHandler
    public void onActivate() {
        new Thread(() -> {
            try {
                if (!mc.isInSingleplayer()) {
                    MinecraftClient minecraftClient = MinecraftClient.getInstance();
                    ClientPlayNetworkHandler networkHandler = minecraftClient.getNetworkHandler();
                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new TeleportConfirmC2SPacket(0));
                    info("Successfully desynced your player entity");

                    if (mc.player != null) {
                        mc.player.getInventory().dropSelectedItem(true);
                        if (sendMsg.get()) {
                            ChatUtils.sendMsg(Text.of("I am currently performing the XsDupe on Crystal Addon."));
                        }
                    }

                } else {
                    error("You must be in a server to use XsDupe, toggling.");
                    toggle();
                }
            } catch (Exception e) {
                CrystalAddon.LOG.error("An error occurred during XsDupe onActivate, " + e);
            }
        }).start();
    }


    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }
}
