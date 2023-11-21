package com.crystaldevs.crystal.utils.mc;

import com.crystaldevs.crystal.utils.mc.seeds.Seeds;
import kaptainwutax.seedcrackerX.api.SeedCrackerAPI;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class SeedCrackerEP implements SeedCrackerAPI {
    @Override
    public void pushWorldSeed(long seed) {
        Seeds.get().setSeed(String.format("%d", seed));
        ChatUtils.infoPrefix("Seed", "Added seed from SeedCrackerX");
    }
}
