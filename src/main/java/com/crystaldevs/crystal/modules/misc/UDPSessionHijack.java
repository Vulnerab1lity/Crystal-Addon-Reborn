package com.crystaldevs.crystal.modules.misc;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.systems.modules.Module;

public class UDPSessionHijack extends Module {
    public UDPSessionHijack() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "UDP Session Hijack", "CRYSTAL || UDP Session Hijacking attack on a Minecraft server uses a carefully crafted malicious packet to misc vulnerabilities and disrupt the server's session.");
    }
}
