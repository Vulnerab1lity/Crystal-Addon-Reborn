package com.crystaldevs.crystal.modules.dupe;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

import java.util.List;

public class ItemFrameDupe extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Double> destroyTime = settings.getDefaultGroup().add(new DoubleSetting.Builder()
            .name("destroy-time")
            .description("Delay for breaking / replacing.")
            .defaultValue(50)
            .min(1)
            .max(1000)
            .sliderMin(50)
            .sliderMax(1000)
            .build()
    );

    private final Setting<Boolean> alwaysActive = settings.getDefaultGroup().add(new BoolSetting.Builder()
            .name("always-on")
            .description("Always dupes without interaction.")
            .defaultValue(false)
            .build()
    );

    public boolean isDuping = false;
    private Thread thread = null;

    public ItemFrameDupe() {
        super(CrystalAddon.CRYSTAL_DUPE_CATEGORY, "item-frame-dupe", "CRYSTAL || Dupes an item by quickly replacing an item frame.");
    }

    @Override
    public void onActivate() {
        thread = new Thread(this::doItemFrameDupe);
        thread.start();
    }

    @Override
    public void onDeactivate() {
        if (thread != null && thread.isAlive()) thread.stop();
    }

    public boolean getShouldDupe() {
        if (!alwaysActive.get()) return mc.mouse.wasRightButtonClicked();
        return true;
    }

    @EventHandler
    public void onInteractItemFrame(InteractEntityEvent event) {
        if (getShouldDupe()) {
            if (!isDuping) {
                if (!(event.entity instanceof ItemFrameEntity)) return;
                thread = new Thread(this::doItemFrameDupe);
                thread.start();
            }
        }
    }

    public void doItemFrameDupe() throws AssertionError {
        isDuping = true;

        while (getShouldDupe()) {
            try {
                long sleepTime = (long) (destroyTime.get() * 0.5);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Box box = new Box(mc.player.getEyePos().add(-3, -3, -3), mc.player.getEyePos().add(3, 3, 3));
            List<ItemFrameEntity> itemFrames = mc.world.getEntitiesByClass(ItemFrameEntity.class, box, itemFrameEntity -> true);

            if (!itemFrames.isEmpty()) {
                ItemFrameEntity itemFrame = itemFrames.get(0);

                mc.interactionManager.interactEntity(mc.player, itemFrame, Hand.MAIN_HAND);

                try {
                    Thread.sleep((long) (destroyTime.get() * 0.7));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (itemFrame.getHeldItemStack().getCount() > 0)
                    mc.interactionManager.interactEntity(mc.player, itemFrame, Hand.MAIN_HAND);

                try {
                    Thread.sleep((long) (destroyTime.get() * 0.7));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mc.interactionManager.attackEntity(mc.player, itemFrame);

                try {
                    Thread.sleep((long) (destroyTime.get() * 0.7));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else break;
        }

        isDuping = false;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }
}