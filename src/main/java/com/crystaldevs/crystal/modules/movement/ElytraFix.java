package com.crystaldevs.crystal.modules.movement;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns.BoundingBox;

import static net.minecraft.util.math.MathHelper.cos;

public class ElytraFix extends Module {

    private final Setting<Boolean> instantFly;

    private final Setting<Boolean> speedCtrl;

    private final Setting<Boolean> heightCtrl;

    private final Setting<Boolean> stopInWater;
    private final Setting<Boolean> stopInLava;

    private final Setting<Boolean> autoDisable;

    private final Setting<Boolean> pitchControl;

    private final Setting<Boolean> smartLanding;
    private final Setting<Double> landingHeightThreshold;
    private final Setting<Double> landingPitchThreshold;
    private final Setting<Double> landingDescentSpeed;
    private final Setting<Boolean> yawControl;
    private final Setting<Double> yawControlSpeed;

    private final Setting<Boolean> turnAssist;

    private final Setting<Double> yawTurnAssistMax;
    private final Setting<Double> yawTurnAssistMin;


    private int jumpTimer;

    @Override
    public void onActivate() {
        jumpTimer = 0;
    }

    public ElytraFix() {
        super(Categories.Movement, "Elytra Fix", "CRYSTAL || Easier elytra");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        instantFly = sgGeneral.add(new BoolSetting.Builder()
            .name("Instant Fly")
            .description("Jump to fly, no weird double-jump needed!")
            .defaultValue(true)
            .build()
        );
        turnAssist = sgGeneral.add(new BoolSetting.Builder()
            .name("Turn Assist")
            .description("subtly adjusts the elytra's direction to align with the player's crosshair, aiding in precision maneuvers.")
            .defaultValue(false)
            .build()
        );

        yawTurnAssistMax = sgGeneral.add(new DoubleSetting.Builder()
            .name("Yaw Turn Assist Max")
            .description("Maximum yaw adjustment when using turn assist.")
            .defaultValue(1.5)
            .min(0.1)
            .sliderMax(5)
            .visible(turnAssist::get)
            .build()
        );

        yawTurnAssistMin = sgGeneral.add(new DoubleSetting.Builder()
            .name("Yaw Turn Assist Min")
            .description("Minimum yaw adjustment when using turn assist.")
            .defaultValue(0.2)
            .min(0.1)
            .sliderMax(1)
            .visible(turnAssist::get)
            .build()
        );

        speedCtrl = sgGeneral.add(new BoolSetting.Builder()
            .name("Speed Control")
            .description("Control your speed with the Forward and Back keys.")
            .defaultValue(true)
            .build()
        );
        heightCtrl = sgGeneral.add(new BoolSetting.Builder()
            .name("Height Control")
            .description("Control your height with the Jump and Sneak keys.")
            .defaultValue(false)
            .build()
        );
        stopInWater = sgGeneral.add(new BoolSetting.Builder()
            .name("Stop in water")
            .description("Stop flying in water")
            .defaultValue(true)
            .build()
        );
        stopInLava = sgGeneral.add(new BoolSetting.Builder()
            .name("Stop in lava")
            .description("Stop flying in lava")
            .defaultValue(true)
            .build()
        );
        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
        );

        pitchControl = sgGeneral.add(new BoolSetting.Builder()
            .name("Pitch Control")
            .description("Control your pitch angle while flying.")
            .defaultValue(false)
            .build()
        );

        smartLanding = sgGeneral.add(new BoolSetting.Builder()
            .name("Smart Landing")
            .description("Automatically adjust pitch and descent speed for safe landings.")
            .defaultValue(true)
            .build()
        );

        landingHeightThreshold = sgGeneral.add(new DoubleSetting.Builder()
            .name("Landing Height Threshold")
            .description("The height threshold at which smart landing activates.")
            .defaultValue(5.0)
            .min(1.0)
            .sliderMin(1.0)
            .sliderMax(20.0)
            .visible(smartLanding::get)
            .build()
        );

        landingPitchThreshold = sgGeneral.add(new DoubleSetting.Builder()
            .name("Landing Pitch Threshold")
            .description("The pitch angle threshold for activating smart landing.")
            .defaultValue(30.0)
            .min(0.0)
            .sliderMin(0.0)
            .sliderMax(90.0)
            .visible(smartLanding::get)
            .build()
        );

        landingDescentSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("Landing Descent Speed")
            .description("The descent speed adjustment for smart landing.")
            .defaultValue(0.04)
            .min(0.01)
            .sliderMin(0.01)
            .sliderMax(0.1)
            .visible(smartLanding::get)
            .build()
        );
        yawControl = sgGeneral.add(new BoolSetting.Builder()
            .name("Yaw Control")
            .description("Control your yaw angle while flying.")
            .defaultValue(false)
            .build()
        );

        yawControlSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("Yaw Control Speed")
            .description("Adjust the speed of yaw angle control.")
            .defaultValue(2.0)
            .min(0.1)
            .visible(yawControl::get)
            .sliderMin(0.1)
            .sliderMax(10.0)
            .build()
        );
    }

    @EventHandler
    public void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (jumpTimer <= 0) {
            jumpTimer--;
        }

        if (yawControl.get()) {
            controlYaw();
        }

        assert mc.player != null;
        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.getItem() == Items.ELYTRA) {
            if (!mc.player.isFallFlying()) {
                if (!ElytraItem.isUsable(chest) || !mc.options.jumpKey.isPressed()) {
                    return;
                }
                doInstantFly();
            } else {
                if (!mc.player.isTouchingWater()) {
                    controlSpeed();
                    controlHeight();
                } else if (!stopInWater.get()) {
                    controlSpeed();
                    controlHeight();
                } else if (pitchControl.get()) {
                    controlPitch();
                } else if (!mc.player.isInLava()) {
                    controlSpeed();
                    controlHeight();
                } else if (!stopInLava.get()) {
                    controlSpeed();
                    controlHeight();
                } else {
                    sendStartStopPacket();
                    return;
                }

                if (smartLanding.get()) {
                    performSmartLanding();
                }

                if (turnAssist.get()) {
                    controlTurnAssist();
                }
            }
        }
    }


    private void sendStartStopPacket() {
        ClientCommandC2SPacket packet = null;
        if (mc.player != null) {
            packet = new ClientCommandC2SPacket(mc.player,
                ClientCommandC2SPacket.Mode.START_FALL_FLYING);
        }
        if (mc.player != null) {
            mc.player.networkHandler.sendPacket(packet);
        }
    }

    private void controlHeight() {
        if (heightCtrl.get()) {
            Vec3d v = null;
            if (mc.player != null) {
                v = mc.player.getVelocity();
            }

            if (!mc.options.jumpKey.isPressed()) {
                if (!mc.options.sneakKey.isPressed()) {
                    return;
                }
                mc.player.setVelocity(v.x, v.y - 0.04, v.z);
            } else {
                mc.player.setVelocity(v.x, v.y + 0.08, v.z);
            }
        }

    }

    private void controlSpeed() {
        if (speedCtrl.get()) {
            float yaw = 0;
            if (mc.player != null) {
                yaw = (float) Math.toRadians(mc.player.getYaw());
            }
            Vec3d forward = new Vec3d(-MathHelper.sin(yaw) * 0.05, 0,
                cos(yaw) * 0.05);

            Vec3d v = mc.player.getVelocity();

            if (!mc.options.forwardKey.isPressed()) {
                if (!mc.options.backKey.isPressed()) {
                    return;
                }
                mc.player.setVelocity(v.subtract(forward));
            } else {
                mc.player.setVelocity(v.add(forward));
            }
        }

    }

    private void doInstantFly() {
        if (instantFly.get()) {
            if (jumpTimer > 0) {
            } else {
                jumpTimer = 20;
                if (mc.player != null) {
                    mc.player.setJumping(false);
                }
                mc.player.setSprinting(true);
                mc.player.jump();
            }

            sendStartStopPacket();
        }

    }

    private void controlPitch() {
        if (mc.player != null) {
            float pitch = mc.player.getPitch();
            if (mc.options.sneakKey.isPressed()) {
                pitch += 2.0f; // Adjust this value as needed
            } else if (mc.options.jumpKey.isPressed()) {
                pitch -= 2.0f; // Adjust this value as needed
            }
            pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
            mc.player.setPitch(pitch);
        }
    }

    private void performSmartLanding() {
        BlockPos playerBlockPos = mc.player.getBlockPos();
        double groundHeight = getGroundHeight(playerBlockPos);
        double heightDifference = mc.player.getY() - groundHeight;

        if (heightDifference <= landingHeightThreshold.get()) {
            adjustPitchAndDescent();
        }
    }

    private double getGroundHeight(BlockPos blockPos) {
        BlockPos groundBlockPos = mc.world.getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).down();
        BlockState blockState = mc.world.getBlockState(groundBlockPos);

        double maxY = -1.0;
        VoxelShape voxelShape = blockState.getCollisionShape(mc.world, groundBlockPos);
        for (Box box : voxelShape.getBoundingBoxes()) {
            if (box.maxY > maxY) {
                maxY = box.maxY;
            }
        }

        return groundBlockPos.getY() + maxY;
    }

    private void controlYaw() {
        if (mc.player != null) {
            float yaw = mc.player.getYaw();
            if (mc.options.leftKey.isPressed()) {
                yaw -= yawControlSpeed.get().floatValue();
            } else if (mc.options.rightKey.isPressed()) {
                yaw += yawControlSpeed.get().floatValue();
            }
            mc.player.setYaw(yaw);
        }
    }

    private void adjustPitchAndDescent() {
        float currentPitch = mc.player.getPitch();

        if (currentPitch > landingPitchThreshold.get()) {
            float newPitch = Math.max(landingPitchThreshold.get().floatValue(), currentPitch - 1.0f);
            mc.player.setPitch(newPitch);
        }

        Vec3d velocity = mc.player.getVelocity();
        mc.player.setVelocity(velocity.x, velocity.y - landingDescentSpeed.get(), velocity.z);
    }

    private void controlTurnAssist() {
        assert mc.player != null;

        float playerYaw = mc.player.getYaw();
        float crosshairYaw = mc.player.getYaw(1);

        float yawDifference = MathHelper.wrapDegrees(crosshairYaw - playerYaw);

        Double maxAdjustment = yawTurnAssistMax.get();
        Double minAdjustment = yawTurnAssistMin.get();

        float yawAdjustment = yawDifference > 0.1f || yawDifference < -0.1f ?
            (float) MathHelper.clamp(yawDifference * maxAdjustment, -maxAdjustment, maxAdjustment) : yawDifference;

        if (yawAdjustment < minAdjustment && yawAdjustment > -minAdjustment) {
            yawAdjustment = (float) MathHelper.clamp(yawAdjustment, -minAdjustment, minAdjustment);
        }

        float smoothedYawAdjustment = MathHelper.lerp(0.1f, 0.0f, yawAdjustment);

        float newYaw = playerYaw + smoothedYawAdjustment;
        mc.player.setYaw(newYaw);
    }



}
