package com.crystaldevs.crystal.mixin.crystal;

import com.crystaldevs.crystal.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(at = @At("HEAD"), method = "sendMessage(Lnet/minecraft/text/Text;)V", cancellable = true)
    public void sendChatMessage(Text message, CallbackInfo ci) {
        if (message.toString().startsWith("$boatcrash")) {
            try {
                if (message.toString().split(" ")[1].equals("toggle")) {
                    if (mc.player != null && mc.player.getVehicle() != null && mc.player.getVehicle() instanceof BoatEntity) {
                        Utils.crashing = !Utils.crashing;
                        sendMsg(Utils.crashing ? "Starting crash..." : "Stopping crash...");
                        for (int i = 0; i < 100000; i++) {
                            if (Utils.crashing) {
                                if (Utils.nettyChannel() != null) {
                                    Vec3d prevPos = mc.player.getVehicle().getPos();
                                    mc.player.getVehicle().setPos(mc.player.getVehicle().getX() - 3, mc.player.getVehicle().getY() - 3, mc.player.getVehicle().getZ() - 3);
                                    Objects.requireNonNull(Utils.nettyChannel()).writeAndFlush(new VehicleMoveC2SPacket(mc.player.getVehicle()));
                                    mc.player.getVehicle().setPos(prevPos.getX(), prevPos.getY(), prevPos.getZ());
                                    Objects.requireNonNull(Utils.nettyChannel()).writeAndFlush(new VehicleMoveC2SPacket(mc.player.getVehicle()));
                                }
                            } else {
                                break;
                            }
                        }
                        Utils.crashing = false;
                    }
                } else {
                    sendErr("Incorrect usage, use §n$boatcrash toggle");
                }
            } catch (Exception e) {
                sendErr("Incorrect usage, use §n$boatcrash toggle");
                ci.cancel();
            }
            ci.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (mc.player != null && mc.player.getVehicle() == null && !(mc.player.getVehicle() instanceof BoatEntity) && Utils.crashing) {
            sendErr("You dismounted the boat, stopping crash...");
            Utils.crashing = false;
        }
    }

    @Unique
    private static void sendMsg(String msg) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.of(msg), false);
        }
    }

    @Unique
    private static void sendErr(String msg) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.of("§c" + msg), false);
        }
    }
}
