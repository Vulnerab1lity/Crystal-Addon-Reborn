package com.crystaldevs.crystal.gui;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class CrystalHud extends HudElement {
    public static final HudElementInfo<CrystalHud> INFO = new HudElementInfo<>(CrystalAddon.HUD_GROUP, "Crystal", "Crystal HUD", CrystalHud::new);

    public CrystalHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        setSize(renderer.textWidth("Crystal Watermark", true), renderer.textHeight(true));
    }

    @Override
    public void render(HudRenderer renderer) {
        renderer.text("Crystal Addon", 1, 2, Color.MAGENTA, true);
    }
}