package com.crystaldevs.crystal.mixin.meteor;

import com.crystaldevs.crystal.utils.crystal.config.CrystalConfig;
import meteordevelopment.meteorclient.utils.network.Http;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.http.HttpRequest;

@Mixin(value = Http.Request.class, remap = false)
public class HttpRequestMixin {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger("Crystal");
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/net/http/HttpRequest$Builder;header(Ljava/lang/String;Ljava/lang/String;)Ljava/net/http/HttpRequest$Builder;"))
    private HttpRequest.Builder onAddUAHeader(HttpRequest.Builder builder, String userAgent, String value) {
        try {
            if (!CrystalConfig.get().httpUserAgent.isBlank()) {
                return builder.header("User-Agent", value);
            } else {
                // if this is reached, the user-agent is blank.
                return builder;
            }
        } catch(Exception e) {
            LOGGER.error("An error occurred during HttpRequestMixin onAddUAHeader:", e);
            return null;
        }
    }
}
