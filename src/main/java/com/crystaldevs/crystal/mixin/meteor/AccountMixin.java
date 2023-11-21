package com.crystaldevs.crystal.mixin.meteor;

import com.crystaldevs.crystal.utils.mc.account.CustomAccount;
import meteordevelopment.meteorclient.systems.accounts.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = Account.class)
public class AccountMixin {
    private static final Logger LOGGER = LogManager.getLogger("Crystal");
    @ModifyArg(method = "toTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putString(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0), index = 1)
    private String putString(String key) {
        try {
            if (!((Object) this instanceof CustomAccount)) {
                return key;
            }
            return "Yggdrasil";
        } catch(Exception e) {
            LOGGER.error("An error occurred during Meteor AccountMixin putString:", e);
            return null;
        }
    }
}
