package com.crystaldevs.crystal.mixin.meteor;

import com.crystaldevs.crystal.utils.mc.account.CustomAccount;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = Accounts.class)
public class AccountsMixin {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger("Crystal");
    @Inject(method = "lambda$fromTag$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;getString(Ljava/lang/String;)Ljava/lang/String;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void onFromTag(NbtElement tag1, CallbackInfoReturnable<Account<?>> cir, NbtCompound t) {
        try {
            if (t.getString("type").equals("Yggdrasil")) {
                Account<CustomAccount> account = new CustomAccount(null, null, null).fromTag(t);
                if (!account.fetchInfo()) {
                } else {
                    cir.setReturnValue(account);
                }
            }
        } catch(Exception e) {
            LOGGER.error("An error occurred during Meteor AccountMixin onFromTag:", e);
        }
    }
}
