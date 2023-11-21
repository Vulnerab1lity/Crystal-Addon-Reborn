package com.crystaldevs.crystal.modules.misc;

import java.util.Iterator;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class FakeHacker extends Module {
    private PlayerEntity target = null;

    public FakeHacker() {
        super(Categories.Misc, "Fake Hacker", "CRYSTAL || Makes it seem like another player is hacking.");
    }


    public void onDeactivate() {
        this.target = null;
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (this.mc.currentScreen == null) {
            if (event.button == 2 && event.action == KeyAction.Press) {
                HitResult var4 = this.mc.crosshairTarget;
                if (var4 instanceof EntityHitResult ehr) {
                    Entity var5 = ehr.getEntity();
                    if (var5 instanceof PlayerEntity) {
                        PlayerEntity pe = (PlayerEntity)var5;
                        this.target = pe;
                    }
                }
            }

        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (this.target != null) {
            Iterator var2 = null;
            if (this.mc.world != null) {
                var2 = this.mc.world.getEntities().iterator();
            }

            if (var2 != null) {
                while(var2.hasNext()) {
                    Entity entity = (Entity)var2.next();
                    if (entity != this.target && entity.isAttackable() && entity.distanceTo(this.target) < 4.0F) {
                        this.target.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getPos().add(0.0, (double)(entity.getHeight() / 2.0F), 0.0));
                        this.target.swingHand(Hand.MAIN_HAND);
                    }
                }
            }

        }
    }

    public String getInfoString() {
        if (this.target != null) {
            return this.target.getEntityName();
        }
        return null;
    }
}
