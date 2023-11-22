package com.crystaldevs.crystal.mixin.crystal;

import com.crystaldevs.crystal.modules.render.BoosieFade;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow
    @Final
    private Supplier<SkinTextures> texturesSupplier;

    @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
    private void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        if (Modules.get().get(BoosieFade.class).isActive())
            cir.setReturnValue(new SkinTextures(
                    new Identifier("crystal", "boosie_fade.png"),
                    texturesSupplier.get().textureUrl(),
                    texturesSupplier.get().capeTexture(),
                    texturesSupplier.get().elytraTexture(),
                    texturesSupplier.get().model(),
                    texturesSupplier.get().secure()
            ));
    }
}