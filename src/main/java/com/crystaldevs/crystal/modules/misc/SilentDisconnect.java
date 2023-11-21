package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class SilentDisconnect extends Module {

    private final Setting<Boolean> autoDisable;
    public SilentDisconnect()
    {
        super(Categories.Misc, "Silent Disconnect", "CRYSTAL || Won't show a disconnect screen when you disconnect.");
        SettingGroup sgGeneral = settings.getDefaultGroup();

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());
    }

    // full implementation soon.

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }
}
