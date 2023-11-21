package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

import java.util.Objects;

public class ExtraKnockback extends Module {

    private final Setting<Boolean> onlyKillAura;
    private final Setting<Boolean> autoDisable;

    public ExtraKnockback() {
        super(Categories.Misc, "Extra Knockback", "CRYSTAL || Does more knockback by editing the Interaction packet.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        onlyKillAura = sgGeneral.add(new BoolSetting.Builder()
            .name("Only killaura")
            .description("Only perform more KB when using the killaura module.")
            .defaultValue(false)
            .build()
        );

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
        );
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if(mc.player != null) {
            if(mc.world != null) {
                if (!(event.packet instanceof IPlayerInteractEntityC2SPacket packet)) {
                    return;
                } else if (packet.getType() != PlayerInteractEntityC2SPacket.InteractType.ATTACK) {
                    return;
                }
                Entity entity = packet.getEntity();

                if (!(entity instanceof LivingEntity)) {
                    return;
                } else if (onlyKillAura.get() && entity != Modules.get().get(KillAura.class).getTarget()) {
                    return;
                }
                assert mc.player != null;
                if(mc.player.networkHandler != null) {
                    Objects.requireNonNull(mc.player).networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.START_SPRINTING));
                } else {
                    error("The networkHandler is null, toggling.");
                    toggle();
                }

            } else {
                error("The world is null, toggling.");
                toggle();
            }
        } else {
            error("The player is null, toggling.");
            toggle();
        }
    }
}
