package com.crystaldevs.crystal.mixin.meteor;

import meteordevelopment.meteorclient.utils.network.Http;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Http.class)
public class HttpMixin {
    @ModifyArg(method = "get", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/utils/network/Http$Request;<init>(Lmeteordevelopment/meteorclient/utils/network/Http$Method;Ljava/lang/String;)V"), remap = false)
    private static String onGet(String url) {
        if (url.startsWith("https://meteorclient.com/api")) return "http://0.0.0.0";
        return url;
    }

    @ModifyArg(method = "post", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/utils/network/Http$Request;<init>(Lmeteordevelopment/meteorclient/utils/network/Http$Method;Ljava/lang/String;)V"), remap = false)
    private static String onPost(String url) {
        if (url.startsWith("https://meteorclient.com/api")) return "http://0.0.0.0";
        return url;
    }
}