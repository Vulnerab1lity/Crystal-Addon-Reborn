package com.crystaldevs.crystal.modules.misc;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.RenderBossBarEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;

import java.util.Set;

public class AntiCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> removeEntities = sgGeneral.add(new BoolSetting.Builder()
            .name("remove-entities-if-lagging")
            .description("Whether or not to remove entities if you're lagging.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> fpsTrigger = sgGeneral.add(new IntSetting.Builder()
            .name("entity-removal-fps")
            .description("At which FPS amount to remove the entities.")
            .defaultValue(5)
            .min(1)
            .sliderMin(1)
            .sliderMax(30)
            .visible(removeEntities::get)
            .build()
    );

    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("entities-to-remove")
            .description("Which entities to include.")
            .defaultValue(EntityType.ITEM)
            .visible(removeEntities::get)
            .build()
    );

    private final Setting<Boolean> limitEntityStrings = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-entity-strings")
            .description("Whether or not to limit entity strings.")
            .defaultValue(true)
            .onChanged(onChanged -> limitStrings = onChanged)
            .build()
    );

    private final Setting<Boolean> noObfuscationSigns = sgGeneral.add(new BoolSetting.Builder()
            .name("replace-obfuscation-signs")
            .description("Replaces all obfuscation section signs.")
            .defaultValue(true)
            .visible(limitEntityStrings::get)
            .onChanged(onChanged -> replaceObfuscationSigns = onChanged)
            .build()
    );

    private final Setting<Integer> entityStringLimit = sgGeneral.add(new IntSetting.Builder()
            .name("entity-string-limit")
            .description("The limit of entity strings.")
            .defaultValue(127)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(limitEntityStrings::get)
            .onChanged(onChanged -> limitAmount = onChanged)
            .build()
    );

    private final Setting<Boolean> limitTitles = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-titles")
            .description("Whether or not to limit titles.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> maxTitleLength = sgGeneral.add(new IntSetting.Builder()
            .name("max-title-length")
            .description("The limit of titles.")
            .defaultValue(200)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(limitTitles::get)
            .build()
    );

    private final Setting<Boolean> limitParticles = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-particles")
            .description("Whether or not to limit particles.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> maxParticles = sgGeneral.add(new IntSetting.Builder()
            .name("max-particles-rendered")
            .description("The limit of rendered particles.")
            .defaultValue(1500)
            .min(1)
            .sliderMin(1)
            .sliderMax(2056)
            .visible(limitParticles::get)
            .build()
    );

    private final Setting<Integer> maxParticleCount = sgGeneral.add(new IntSetting.Builder()
            .name("max-particle-count")
            .description("The limit of particle counts.")
            .defaultValue(400)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(limitParticles::get)
            .build()
    );

    private final Setting<Integer> maxParticleSpeed = sgGeneral.add(new IntSetting.Builder()
            .name("max-particle-speed")
            .description("The limit of particle speeds.")
            .defaultValue(400)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(limitParticles::get)
            .build()
    );

    private final Setting<Boolean> limitOverlays = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-overlays")
            .description("Whether or not to limit overlays.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> maxOverlayLength = sgGeneral.add(new IntSetting.Builder()
            .name("max-overlay-length")
            .description("The limit of overlay messages.")
            .defaultValue(512)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(limitOverlays::get)
            .build()
    );

    private final Setting<Boolean> limitGameMessages = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-game-messages")
            .description("Whether or not to limit game messages.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> maxGameMessageLength = sgGeneral.add(new IntSetting.Builder()
            .name("max-game-message-length")
            .description("The limit of game messages.")
            .defaultValue(1024)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(limitGameMessages::get)
            .build()
    );

    private final Setting<Boolean> limitBossBars = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-boss-bars")
            .description("Whether or not to limit boss bars.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> maxBossbarLength = sgGeneral.add(new IntSetting.Builder()
            .name("max-bossbar-length")
            .description("The limit of bossbars.")
            .defaultValue(127)
            .min(1)
            .sliderMin(1)
            .sliderMax(1024)
            .visible(limitBossBars::get)
            .build()
    );

    private final Setting<Boolean> limitScreens = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-screens")
            .description("Whether or not to limit screens.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> limitVelocity = sgGeneral.add(new BoolSetting.Builder()
            .name("limit-velocity")
            .description("Whether or not to limit velocity.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> maxVelocity = sgGeneral.add(new IntSetting.Builder()
            .name("max-velocity-distance")
            .description("The limit of velocity.")
            .defaultValue(20)
            .min(1)
            .sliderMin(1)
            .sliderMax(512)
            .visible(limitVelocity::get)
            .build()
    );

    private final Setting<Boolean> antiOffhandCrash = sgGeneral.add(new BoolSetting.Builder()
            .name("anti-offhand-crash")
            .description("Whether or not to prevent yourself from being crashed by the offhand crash exploit.")
            .defaultValue(false)
            .onChanged(onChanged -> preventOffhandCrash = onChanged)
            .build()
    );

    private final Setting<Boolean> packetWarning = sgGeneral.add(new BoolSetting.Builder()
            .name("packet-warning")
            .description("Whether or not to send warnings about cancelled packets in chat.")
            .defaultValue(true)
            .build()
    );

    public static boolean replaceObfuscationSigns;
    public static boolean limitStrings;
    public static boolean preventOffhandCrash;
    public static int limitAmount;
    private int joinTimer;
    private boolean hasJoined;
    private long lastScreen;

    public AntiCrash() {
        super(Categories.Misc, "Anti Crash", "CRYSTAL || Attempts to stop exploits from crashing your client.");
    }

    @Override
    public void onActivate() {
        replaceObfuscationSigns = noObfuscationSigns.get();
        limitStrings = limitEntityStrings.get();
        preventOffhandCrash = antiOffhandCrash.get();
        limitAmount = entityStringLimit.get();
        if (mc.player != null) hasJoined = true;
        joinTimer = 0;
    }

    @Override
    public void onDeactivate() {
        hasJoined = false;
        joinTimer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (hasJoined) {
            joinTimer++;
            if (joinTimer > 100) {
                hasJoined = false;
                joinTimer = 0;
            }
        }
        if (!removeEntities.get()) return;
        if (hasJoined) return;
        if (mc.getCurrentFps() <= fpsTrigger.get()) {
            try {
                for (Entity entity : mc.world.getEntities()) {
                    if (entity == null || entity == mc.player || !entities.get().contains(entity.getType())) continue;
                    entity.setRemoved(Entity.RemovalReason.DISCARDED);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof OpenScreenS2CPacket packet) {
            if (!limitScreens.get()) return;
            long current = System.currentTimeMillis();
            long diff = current - lastScreen;
            lastScreen = current;
            if (diff < 10) {
                event.setCancelled(true);
                if (packetWarning.get()) warning("Dropped malicious screen packet. "
                        + "(Screen: " + packet.getName().getString() + ")");
            }
        }
        if (event.packet instanceof EntityVelocityUpdateS2CPacket packet) {
            if (!limitVelocity.get()) return;
            if (packet.getVelocityX() / 8000d > maxVelocity.get()
                    || packet.getVelocityY() / 8000d > maxVelocity.get()
                    || packet.getVelocityZ() / 8000d > maxVelocity.get()) {
                event.setCancelled(true);
                if (packetWarning.get()) warning("Dropped malicious velocity packet. "
                        + "(X: " + packet.getVelocityX()
                        + " Y: " + packet.getVelocityY()
                        + " Z: " + packet.getVelocityZ() + ")");
            }
        }
        if (event.packet instanceof ParticleS2CPacket packet) {
            if (!limitParticles.get()) return;
            if (packet.getCount() > maxParticleCount.get() || packet.getSpeed() > maxParticleSpeed.get()) {
                event.setCancelled(true);
                if (packetWarning.get()) warning("Dropped malicious particle packet. "
                        + "(Count: " + packet.getCount() + " : Speed: " + packet.getSpeed() + ")");
            }
        }
        if (event.packet instanceof TitleS2CPacket packet) {
            if (!limitTitles.get()) return;
            if (packet.getTitle().getString().length() > maxTitleLength.get()) {
                event.setCancelled(true);
                if (packetWarning.get()) warning("Dropped malicious title packet. "
                        + "(Length: " + packet.getTitle().getString().length() + ")");
            }
        }
        if (event.packet instanceof OverlayMessageS2CPacket packet) {
            if (!limitOverlays.get()) return;
            if (packet.getMessage().getString().length() > maxOverlayLength.get()) {
                event.setCancelled(true);
                if (packetWarning.get()) warning("Dropped malicious overlay packet. "
                        + "(Length: " + packet.getMessage().getString().length() + ")");
            }
        }
        if (event.packet instanceof GameMessageS2CPacket packet) {
            if (!limitGameMessages.get()) return;
            if (packet.content().getString().length() > maxGameMessageLength.get()) {
                event.setCancelled(true);
                if (packetWarning.get()) warning("Dropped malicious game message packet. "
                        + "(Length: " + packet.content().getString().length() + ")");
            }
        }
    }

    @EventHandler
    private void onRenderBossBar(RenderBossBarEvent.BossText event) {
        if (!limitBossBars.get()) return;
        if (event.name.getString().length() > maxBossbarLength.get())
            event.bossBar.setName(Text.of(event.name.getString().substring(0, maxBossbarLength.get())));
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        hasJoined = false;
        joinTimer = 0;
    }

    @EventHandler
    private void onJoin(GameJoinedEvent event) {
        hasJoined = true;
        joinTimer = 0;
    }
}