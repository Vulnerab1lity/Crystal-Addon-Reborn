package com.crystaldevs.crystal.modules.player;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

public class HeadRoll extends Module {
    public HeadRoll() {
        super(Categories.Player, "Head Roll", "CRYSTAL || Rolls the players head lol.");
    }

    @EventHandler
    public void onTick() {
        float timer = 0;
        if (mc.player != null) {
            timer = mc.player.age % 20 / 10F;
        }
        float pitch = MathHelper.sin(timer * (float)Math.PI) * 90F;

        if (mc.player != null) {
            mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), pitch,
                    mc.player.isOnGround()));
        }
    }
}
