package com.crystaldevs.crystal.modules.render;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class BoosieFade extends Module {
    public static BoosieFade INSTANCE;

    public BoosieFade() {
        super(Categories.Render, "Boosie Fade","CRYSTAL || Give all players the Boosie Fade skin.");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        INSTANCE = this;
    }
}
