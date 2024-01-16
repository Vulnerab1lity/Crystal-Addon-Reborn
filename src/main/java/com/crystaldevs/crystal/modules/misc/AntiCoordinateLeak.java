package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.regex.Pattern;

public class AntiCoordinateLeak extends Module {
    private static final Pattern POSITIVE_PATTERN = Pattern.compile("X: (-?\\d+(\\.\\d+)?) Y: (-?\\d+(\\.\\d+)?) Z: (-?\\d+(\\.\\d+)?)");
    private static final Pattern NEGATIVE_PATTERN = Pattern.compile("(-?\\d+(\\.\\d+)?) (-?\\d+(\\.\\d+)?) (-?\\d+(\\.\\d+)?)");

    public AntiCoordinateLeak() {
        super(Categories.Misc, "anti-coordinate-leak", "CRYSTAL || Prevents sending messages containing coordinates.");
    }

    @EventHandler
    private void onSendMessage(SendMessageEvent event) {
        if (POSITIVE_PATTERN.matcher(event.message).find() || NEGATIVE_PATTERN.matcher(event.message).find()) {
            info("Stopped potential coordinate leak: " + event.message);
            event.setCancelled(true);
        }
    }
}