package com.crystaldevs.crystal.mixin.meteor;

import com.crystaldevs.crystal.utils.crystal.config.CrystalConfig;
import meteordevelopment.meteorclient.utils.network.Http;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;


@Mixin(Http.class)
public class HttpMixin {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger("Crystal");

    @ModifyArg(method="get", at= @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/utils/network/Http$Request;<init>(Lmeteordevelopment/meteorclient/utils/network/Http$Method;Ljava/lang/String;)V"), remap = false)
    private static String onGet(String url) {
        try {
            if (CrystalConfig.get().httpAllowed != CrystalConfig.HttpAllowed.Nothing) {
                if (CrystalConfig.get().httpAllowed != CrystalConfig.HttpAllowed.NotMeteorApi || !url.startsWith("https://meteorclient.com/api")) {
                    if (CrystalConfig.get().httpAllowed != CrystalConfig.HttpAllowed.NotMeteorPing || !url.startsWith("https://meteorclient.com/api/online")) {
                    } else {
                        return "http://0.0.0.0";
                    }
                } else {
                    return "http://0.0.0.0";
                }
            } else {
                return "http://0.0.0.0";
            }
            return url;
        } catch(Exception e) {
            LOGGER.error("An error occurred during Meteor HttpMixin onGet:", e);
            return null;
        }
    }

    @ModifyArg(method="post", at= @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/utils/network/Http$Request;<init>(Lmeteordevelopment/meteorclient/utils/network/Http$Method;Ljava/lang/String;)V"), remap = false)
    private static String onPost(String url) {
        try {
            if (CrystalConfig.get().httpAllowed != CrystalConfig.HttpAllowed.Nothing) {
                if (CrystalConfig.get().httpAllowed != CrystalConfig.HttpAllowed.NotMeteorApi || !url.startsWith("https://meteorclient.com/api")) {
                    if (CrystalConfig.get().httpAllowed != CrystalConfig.HttpAllowed.NotMeteorPing || !url.startsWith("https://meteorclient.com/api/online")) {
                    } else {
                        return "http://0.0.0.0";
                    }
                } else {
                    return "http://0.0.0.0";
                }
            } else {
                return "http://0.0.0.0";
            }
            return url;
        } catch(Exception e) {
            LOGGER.error("An error occurred during Meteor HttpMixin onPost:", e);
            return null;
        }

    }
}
