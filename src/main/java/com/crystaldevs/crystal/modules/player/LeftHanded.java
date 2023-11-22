package com.crystaldevs.crystal.modules.player;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.util.Arm;

public class LeftHanded extends Module {
    public LeftHanded() {
        super(Categories.Player, "left-handed", "CRYSTAL || Changes your main arm to your left arm.");
    }

    @Override
    public void onActivate() {
        if (mc.player != null) mc.player.setMainArm(Arm.LEFT);
    }

    @Override
    public void onDeactivate() {
        if (mc.player != null) mc.player.setMainArm(Arm.RIGHT);
    }
}