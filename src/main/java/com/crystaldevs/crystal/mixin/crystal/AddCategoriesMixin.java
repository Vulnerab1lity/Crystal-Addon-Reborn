package com.crystaldevs.crystal.mixin.crystal;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "meteordevelopment.meteorclient.gui.screens.ModulesScreen$WCategoryController", remap = false)
public final class AddCategoriesMixin extends WContainer {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger("Crystal");
    @Inject(method = "init", at = @At("TAIL"))
    private void addCustomWidgets(CallbackInfo ci) {
        try {
            CrystalAddon.myWidgets.forEach(s -> s.accept(theme, this));
        } catch(Exception e) {
            LOGGER.error("An error occurred during AddCategoriesMixin addCustomWidgets:", e);
        }

    }
}
