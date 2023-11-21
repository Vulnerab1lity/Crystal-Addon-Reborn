package com.crystaldevs.crystal.mixin.crystal;

import com.crystaldevs.crystal.modules.render.BoosieFade;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinRenderPlayer implements SkinOverlayOwner {

    @Unique
    private void modifySkinTexture(CallbackInfoReturnable<Identifier> info) {
        if(BoosieFade.INSTANCE.isActive()) {
            Identifier newTexture = new Identifier("crystal", "boosie_fade.png");
            info.setReturnValue(newTexture);
        }
    }
}

