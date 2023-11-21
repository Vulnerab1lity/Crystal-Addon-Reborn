package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiCoordinateLeak extends Module {

    public AntiCoordinateLeak() {
        super(Categories.Misc, "Anti Coordinate Leak", "Prevents sending messages containing coordinates.");
    }

    @EventHandler
    private void onSendMessage(SendMessageEvent event) {
        String message = event.message;

        Pattern pattern1 = Pattern.compile("X: (-?\\d+(\\.\\d+)?) Y: (-?\\d+(\\.\\d+)?) Z: (-?\\d+(\\.\\d+)?)");
        Pattern pattern2 = Pattern.compile("(-?\\d+(\\.\\d+)?) (-?\\d+(\\.\\d+)?) (-?\\d+(\\.\\d+)?)");

        Matcher matcher1 = pattern1.matcher(message);
        Matcher matcher2 = pattern2.matcher(message);

        if ((matcher1.find() || matcher2.find()) && message.contains("X:") && message.contains("Y:") && message.contains("Z:")) {
            info("Stopped potential coordinate leak.");
            event.cancel();
        }
    }
}
