package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SubnetCalculatorCommand extends Command {
    public SubnetCalculatorCommand() {
        super("subnet-calc", "Calculate subnet details based on a given IP address and subnet mask.", "subnetcalc");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("ip", StringArgumentType.greedyString())
                .then(argument("subnetMask", StringArgumentType.greedyString()).executes(context -> {
                    String ip = context.getArgument("ip", String.class);
                    String subnetMask = context.getArgument("subnetMask", String.class);

                    try {
                        SubnetCalculatorResult result = calculateSubnet(ip, subnetMask);
                        info("Subnet Details for " + ip + "/" + subnetMask);
                        info("Network Address: " + result.networkAddress());
                        info("Broadcast Address: " + result.broadcastAddress());
                        info("Usable Host IP Range: " + result.usableHostRange());
                        info("Number of Hosts: " + result.numOfHosts());
                    } catch (UnknownHostException e) {
                        error("Invalid IP address or subnet mask.");
                    } catch (IllegalArgumentException e) {
                        error("Invalid subnet mask: " + e.getMessage());
                    }
                    return SINGLE_SUCCESS;
                }))
        );
    }

    private SubnetCalculatorResult calculateSubnet(String ip, String subnetMask) throws UnknownHostException {
        if (!isValidIP(ip)) {
            throw new IllegalArgumentException("Invalid IP address: " + ip);
        }

        if (!isValidSubnetMask(subnetMask)) {
            throw new IllegalArgumentException("Invalid subnet mask: " + subnetMask);
        }

        InetAddress inetAddress = InetAddress.getByName(ip);
        InetAddress subnetAddress = InetAddress.getByName(subnetMask);

        byte[] ipBytes = inetAddress.getAddress();
        byte[] subnetBytes = subnetAddress.getAddress();

        if (ipBytes.length != subnetBytes.length) {
            throw new IllegalArgumentException("Invalid subnet mask: " + subnetMask);
        }

        byte[] networkBytes = new byte[ipBytes.length];
        byte[] broadcastBytes = new byte[ipBytes.length];
        for (int i = 0; i < ipBytes.length; i++) {
            networkBytes[i] = (byte) (ipBytes[i] & subnetBytes[i]);
            broadcastBytes[i] = (byte) (ipBytes[i] | ~subnetBytes[i]);
        }

        InetAddress networkAddress = InetAddress.getByAddress(networkBytes);
        InetAddress broadcastAddress = InetAddress.getByAddress(broadcastBytes);

        int numberOfHosts = ~subnetAddress.hashCode();
        return new SubnetCalculatorResult(networkAddress.getHostAddress(),
                broadcastAddress.getHostAddress(),
                getUsableHostRange(networkBytes, broadcastBytes),
                numberOfHosts - 2);
    }

    private String getUsableHostRange(byte[] networkBytes, byte[] broadcastBytes) throws UnknownHostException {
        byte[] usableHostRangeStart = networkBytes.clone();
        byte[] usableHostRangeEnd = broadcastBytes.clone();

        usableHostRangeStart[usableHostRangeStart.length - 1]++;
        usableHostRangeEnd[usableHostRangeEnd.length - 1]--;

        return InetAddress.getByAddress(usableHostRangeStart).getHostAddress()
                + " - " + InetAddress.getByAddress(usableHostRangeEnd).getHostAddress();
    }

    private boolean isValidIP(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private boolean isValidSubnetMask(String subnetMask) {
        try {
            InetAddress.getByName(subnetMask);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private record SubnetCalculatorResult(String networkAddress, String broadcastAddress, String usableHostRange, int numOfHosts) {
    }
}