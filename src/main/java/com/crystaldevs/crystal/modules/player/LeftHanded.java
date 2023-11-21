package com.crystaldevs.crystal.modules.player;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.util.Arm;

import java.util.UUID;

public class LeftHanded extends Module {
    public LeftHanded() {
        super(Categories.Player, "Left handed", "CRYSTAL || Changes your main arm to your left arm.");
    }

    public void onActivate() {
        if(mc.player != null && mc.world != null) {
            mc.player.setMainArm(Arm.LEFT);
        }
    }

    public void onDeactivate() {
        if(mc.player != null && mc.world != null) {
            mc.player.setMainArm(Arm.RIGHT);
        }
    }
}
