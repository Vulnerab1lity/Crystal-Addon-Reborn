package com.crystaldevs.crystal.mixin.meteor.modules;

import com.crystaldevs.crystal.utils.Utils;
import com.crystaldevs.crystal.utils.mc.McUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(value = KillAura.class, remap = false)
public class KillAuraMixin extends Module {
    @Shadow
    @Final
    private SettingGroup sgGeneral;
    @Shadow
    @Final
    private SettingGroup sgTargeting;
    @Shadow
    @Final
    private Setting<Boolean> onlyOnLook;
    @Shadow
    private int hitTimer;
    @Shadow
    @Final
    private SettingGroup sgTiming;
    @Shadow
    @Final
    private Setting<Boolean> customDelay;
    @Shadow
    @Final
    private Setting<Integer> hitDelay;

    @Unique
    private final Random random = new Random();
    @Unique
    private Setting<Double> fov;
    @Unique
    private Setting<Boolean> ignoreInvisible;
    @Unique
    private Setting<Boolean> randomTeleport;
    @Unique
    private Setting<Double> hitChance;
    @Unique
    private Setting<Integer> randomDelayMax;

    public KillAuraMixin(Category category, String name, String description) {
        super(category, name, description);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        fov = sgGeneral.add(new DoubleSetting.Builder()
            .name("fov")
            .description("Will only aim entities in the fov.")
            .defaultValue(360)
            .min(0)
            .max(360)
            .build()
        );

        ignoreInvisible = sgTargeting.add(new BoolSetting.Builder()
            .name("ignore-invisible")
            .description("Whether or not to attack invisible entities.")
            .defaultValue(false)
            .build()
        );

        randomTeleport = sgGeneral.add(new BoolSetting.Builder()
            .name("random-teleport")
            .description("Randomly teleport around the target.")
            .defaultValue(false)
            .visible(() -> !onlyOnLook.get())
            .build()
        );

        hitChance = sgGeneral.add(new DoubleSetting.Builder()
            .name("hit-chance")
            .description("The probability of your hits landing.")
            .defaultValue(100)
            .range(1, 100)
            .sliderRange(1, 100)
            .build()
        );

        randomDelayMax = sgTiming.add(new IntSetting.Builder()
            .name("random-delay-max")
            .description("The maximum value for random delay.")
            .defaultValue(4)
            .min(0)
            .sliderMax(20)
            .visible(customDelay::get)
            .build()
        );
    }

    @Inject(method = "entityCheck", at = @At(value = "RETURN", ordinal = 14), cancellable = true)
    private void onReturn(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (!ignoreInvisible.get() || !entity.isInvisible()) {
            //ignoreVisible or entity.isInvisible returned false if this is reached.
        } else {
            info.setReturnValue(false);
        }
        if (McUtils.inFov(entity, fov.get())) {
        } else {
            info.setReturnValue(false);
        }
        info.setReturnValue(info.getReturnValueZ());
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity entity, CallbackInfo info) {
        if (hitChance.get() >= 100 || !(Math.random() > hitChance.get() / 100)) {
            return;
        }
        info.cancel();
    }

    @Inject(method = "onTick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onTick(TickEvent.Pre event, CallbackInfo ci, Entity primary) {
        if (!randomTeleport.get() || onlyOnLook.get()) {
            return;
        }
        assert mc.player != null;
        mc.player.setPosition(primary.getX() + randomOffset(), primary.getY(), primary.getZ() + randomOffset());
    }

    @Inject(method = "attack", at = @At(value = "TAIL"))
    private void modifyHitDelay(CallbackInfo info) {
        if (randomDelayMax.get() != 0) {
            hitTimer -= random.nextInt(randomDelayMax.get());
        } else {
            return;
        }
    }

    @Unique
    private double randomOffset() {
        return Math.random() * 4 - 2;
    }
}
