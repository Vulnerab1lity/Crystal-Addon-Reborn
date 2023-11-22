package com.crystaldevs.crystal.modules.dupe;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public class XsDupe extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> sendMsg = sgGeneral.add(new BoolSetting.Builder()
            .name("send-message")
            .description("Sends a message when duping to the server.")
            .defaultValue(true)
            .build()
    );

    public XsDupe() {
        super(CrystalAddon.CRYSTAL_DUPE_CATEGORY, "xs-dupe", "CRYSTAL || Attempts to dupe by desyncing and doing multiple actions.");
    }

    @Override
    public void onActivate() {
        mc.player.networkHandler.sendPacket(new TeleportConfirmC2SPacket(0));
        info("Successfully desynced your player entity");
        mc.player.getInventory().dropSelectedItem(true);
        if (sendMsg.get())
            ChatUtils.sendPlayerMsg("I am currently performing the XsDupe on Crystal Addon.");
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}