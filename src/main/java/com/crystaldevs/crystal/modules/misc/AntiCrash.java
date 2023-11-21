package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.render.RenderBossBarEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import java.util.Objects;
import java.util.Set;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;

public class AntiCrash extends Module
{
    private final Setting<Boolean> removeEntities;
    private final Setting<Integer> fpsTrigger;
    private final Setting<Set<EntityType<?>>> entities;
    private final Setting<Boolean> limitEntityStrings;
    private final Setting<Boolean> noObfuscationSigns;
    private final Setting<Integer> entityStringLimit;
    private final Setting<Boolean> limitTitles;
    private final Setting<Integer> maxTitleLength;
    private final Setting<Boolean> limitOverlays;
    private final Setting<Integer> maxOverlayLength;
    private final Setting<Boolean> limitGameMessages;
    private final Setting<Integer> maxGameMessageLength;
    private final Setting<Boolean> limitBossBars;
    private final Setting<Integer> maxBossbarLength;
    private final Setting<Boolean> limitScreens;
    private final Setting<Boolean> limitVelocity;
    private final Setting<Integer> maxVelocity;
    private final Setting<Boolean> antiOffhandCrash;
    private final Setting<Boolean> packetWarning;
    public static boolean replaceObfuscationSigns;
    public static boolean limitStrings;
    public static boolean preventOffhandCrash;
    public static int limitAmount;
    private int joinTimer;
    private boolean hasJoined;
    private long lastScreen;

    public AntiCrash() {
        super(Categories.Misc, "Anti Crash", "CRYSTAL || Attempts to stop exploits from crashing your client.");

        SettingGroup sgGeneral = this.settings.getDefaultGroup();

// Entity Settings
        this.removeEntities = sgGeneral.add(new BoolSetting.Builder()
            .name("Remove Entities if Lagging")
            .description("Whether or not to remove entities if you're lagging.")
            .defaultValue(false)
            .build());

        this.fpsTrigger = sgGeneral.add(new IntSetting.Builder()
            .name("Entity Removal FPS")
            .description("At which FPS amount to remove the entities.")
            .defaultValue(5)
            .min(1)
            .sliderMin(1)
            .sliderMax(30)
            .visible(this.removeEntities::get)
            .build());

        this.entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("Entities to Remove")
            .description("Which entities to include.")
            .defaultValue(EntityType.ITEM)
            .visible(this.removeEntities::get)
            .build());

// Limit Entity Strings Settings
        this.limitEntityStrings = sgGeneral.add(new BoolSetting.Builder()
            .name("Limit Entity Strings")
            .description("Whether or not to limit entity strings.")
            .defaultValue(true)
            .onChanged(onChanged -> AntiCrash.limitStrings = onChanged)
            .build());

        this.noObfuscationSigns = sgGeneral.add(new BoolSetting.Builder()
            .name("Replace Obfuscation Signs")
            .description("Replaces all obfuscation section signs.")
            .defaultValue(true)
            .visible(this.limitEntityStrings::get)
            .onChanged(onChanged -> AntiCrash.replaceObfuscationSigns = onChanged)
            .build());

        this.entityStringLimit = sgGeneral.add(new IntSetting.Builder()
            .name("Entity String Limit")
            .description("The limit of entity strings.")
            .defaultValue(127)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(this.limitEntityStrings::get)
            .onChanged(onChanged -> AntiCrash.limitAmount = onChanged)
            .build());

// Title Settings
        this.limitTitles = sgGeneral.add(new BoolSetting.Builder()
            .name("Limit Titles")
            .description("Whether or not to limit titles.")
            .defaultValue(true)
            .build());

        this.maxTitleLength = sgGeneral.add(new IntSetting.Builder()
            .name("Max Title Length")
            .description("The limit of titles.")
            .defaultValue(200)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(this.limitTitles::get)
            .build());

// Overlay Settings
        this.limitOverlays = sgGeneral.add(new BoolSetting.Builder()
            .name("Limit Overlays")
            .description("Whether or not to limit overlays.")
            .defaultValue(true)
            .build()
        );

        this.maxOverlayLength = sgGeneral.add(new IntSetting.Builder()
            .name("Max Overlay Length")
            .description("The limit of overlay messages.")
            .defaultValue(512)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(this.limitOverlays::get)
            .build());

// Game Message Settings
        this.limitGameMessages = sgGeneral.add(new BoolSetting.Builder()
            .name("Limit Game Messages")
            .description("Whether or not to limit game messages.")
            .defaultValue(true)
            .build()
        );

        this.maxGameMessageLength = sgGeneral.add(new IntSetting.Builder()
            .name("Max Game Message Length")
            .description("The limit of game messages.")
            .defaultValue(1024)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(this.limitGameMessages::get)
            .build());

// Boss Bar Settings
        this.limitBossBars = sgGeneral.add(new BoolSetting.Builder()
            .name("Limit Boss Bars")
            .description("Whether or not to limit boss bars.")
            .defaultValue(true)
            .build()
        );

        this.maxBossbarLength = sgGeneral.add(new IntSetting.Builder()
            .name("Max Boss Bar Length")
            .description("The limit of bossbars.")
            .defaultValue(127)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(this.limitBossBars::get)
            .build());

// Other Settings
        this.limitScreens = sgGeneral.add(new BoolSetting.Builder()
            .name("Limit Screens")
            .description("Whether or not to limit screens.")
            .defaultValue(true)
            .build()
        );

        this.limitVelocity = sgGeneral.add(new BoolSetting.Builder()
            .name("Limit Velocity")
            .description("Whether or not to limit velocity.")
            .defaultValue(true)
            .build()
        );

        this.maxVelocity = sgGeneral.add(new IntSetting.Builder()
            .name("Max Velocity Distance")
            .description("The limit of velocity.")
            .defaultValue(20)
            .min(1)
            .sliderMin(1)
            .sliderMax(512)
            .visible(this.limitVelocity::get)
            .build());

        this.antiOffhandCrash = sgGeneral.add(new BoolSetting.Builder()
            .name("Anti Offhand Crash")
            .description("Whether or not to prevent yourself from being crashed by the offhand crash exploit.")
            .defaultValue(false)
            .onChanged(onChanged -> AntiCrash.preventOffhandCrash = onChanged)
            .build()
        );

        this.packetWarning = sgGeneral.add(new BoolSetting.Builder()
            .name("Packet Warning")
            .description("Whether or not to send warnings about cancelled packets in chat.")
            .defaultValue(true)
            .build()
        );

    }

    @Override
    public void onActivate() {
        AntiCrash.replaceObfuscationSigns = this.noObfuscationSigns.get();
        AntiCrash.limitStrings = this.limitEntityStrings.get();
        AntiCrash.preventOffhandCrash = this.antiOffhandCrash.get();
        AntiCrash.limitAmount = this.entityStringLimit.get();

        this.hasJoined = true;
        this.joinTimer = 0;
    }

    @Override
    public void onDeactivate() {
        this.hasJoined = false;
        this.joinTimer = 0;
    }

    @EventHandler
    private void onTick(final TickEvent.Post event) {
        if (this.hasJoined) {
            ++this.joinTimer;
            if (this.joinTimer > 100) {
                this.hasJoined = false;
                this.joinTimer = 0;
            }
        }

        if (!this.removeEntities.get() || this.hasJoined) {
            return;
        }

        try {
            int currentFps = MinecraftClientAccessor.getFps();
            if (currentFps <= this.fpsTrigger.get()) {
                assert this.mc.world != null;

                for (final Entity entity : this.mc.world.getEntities()) {
                    if (entity != null && entity != this.mc.player && this.entities.get().contains(entity.getType())) {
                        entity.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
                    }
                }
            }
        } catch (Exception ignored) {
            // Handle exceptions if needed
        }
    }


    @EventHandler
    private void onPacketReceive(final PacketEvent.Receive event) {
        final Packet<?> receivedPacket = event.packet;

        if (receivedPacket instanceof OpenScreenS2CPacket openScreenPacket) {
            if (!this.limitScreens.get()) {
                return;
            }

            final long current = System.currentTimeMillis();
            final long diff = current - this.lastScreen;
            this.lastScreen = current;

            if (diff < 10L) {
                event.setCancelled(true);
                if (this.packetWarning.get()) {
                    this.warning("Packet name: " + openScreenPacket.getName().getString());
                }
            }
        } else if (receivedPacket instanceof EntityVelocityUpdateS2CPacket velocityUpdatePacket) {
            if (!this.limitVelocity.get()) {
                return;
            }

            double velocityX = velocityUpdatePacket.getVelocityX() / 8000.0;
            double velocityY = velocityUpdatePacket.getVelocityY() / 8000.0;
            double velocityZ = velocityUpdatePacket.getVelocityZ() / 8000.0;

            if (velocityX > this.maxVelocity.get() || velocityY > this.maxVelocity.get() || velocityZ > this.maxVelocity.get()) {
                event.setCancelled(true);
                if (this.packetWarning.get()) {
                    this.warning("Velocity X: " + velocityX + ", Velocity Y: " + velocityY + ", Velocity Z: " + velocityZ);
                }
            }
        } else if (receivedPacket instanceof TitleS2CPacket titlePacket) {
            if (!this.limitTitles.get()) {
                return;
            }

            String titleString = titlePacket.getTitle().getString();

            if (titleString.length() > this.maxTitleLength.get()) {
                event.setCancelled(true);
                if (this.packetWarning.get()) {
                    this.warning("Packet title length exceeds the maximum: " + titleString.length());
                }
            }
        } else if (receivedPacket instanceof OverlayMessageS2CPacket overlayMessagePacket) {
            if (!this.limitOverlays.get()) {
                return;
            }

            String overlayMessage = overlayMessagePacket.getMessage().getString();

            if (overlayMessage.length() > this.maxOverlayLength.get()) {
                event.setCancelled(true);
                if (this.packetWarning.get()) {
                    this.warning("Packet length exceeds the maximum overlay length: " + overlayMessage.length());
                }
            }
        } else if (receivedPacket instanceof GameMessageS2CPacket gameMessagePacket) {
            if (!this.limitGameMessages.get()) {
                return;
            }

            String contentString = gameMessagePacket.content().getString();

            if (contentString.length() > this.maxGameMessageLength.get()) {
                event.setCancelled(true);
                if (this.packetWarning.get()) {
                    String warningMessage = "Warning message with length: " + contentString.length();
                    this.warning(warningMessage);
                }
            }
        }
    }


    @EventHandler
    private void onRenderBossBar(final RenderBossBarEvent.BossText event) {
        if (!this.limitBossBars.get()) {
            return;
        }
        if (event.name.getString().length() > this.maxBossbarLength.get()) {
            event.bossBar.setName(Text.of(event.name.getString().substring(0, this.maxBossbarLength.get())));
        }
    }

    @EventHandler
    private void onGameLeft(final GameLeftEvent event) {
        this.hasJoined = false;
        this.joinTimer = 0;
    }

    @EventHandler
    private void onJoin(final GameJoinedEvent event) {
        this.hasJoined = true;
        this.joinTimer = 0;
    }
}
