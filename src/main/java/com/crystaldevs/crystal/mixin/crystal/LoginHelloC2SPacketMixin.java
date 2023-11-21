package com.crystaldevs.crystal.mixin.crystal;

import com.crystaldevs.crystal.modules.crash.NullExceptionCrash;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoginHelloC2SPacket.class)
public abstract class LoginHelloC2SPacketMixin {
    private static final Logger LOGGER = LogManager.getLogger("Crystal");
    @Inject(method = "write", cancellable = true, at = @At("HEAD"))
    public void gid(PacketByteBuf buf, CallbackInfo ci) {
        try {
            if (Modules.get().isActive(NullExceptionCrash.class)) {
                Modules.get().get(NullExceptionCrash.class).toggle();
                buf.writeString(null);
                ci.cancel();
            }
        } catch(Exception e) {
            LOGGER.error("An error occurred during LoginHelloC2SPacketMixin gid:", e);
        }

    }
}
