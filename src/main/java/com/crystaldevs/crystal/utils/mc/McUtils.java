package com.crystaldevs.crystal.utils.mc;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class McUtils {

    public static Style deserializeStyle(NbtCompound compound) {
        Style style = Style.EMPTY;

        if (compound.contains("color")) {
            style = style.withColor(TextColor.fromRgb(compound.getInt("color")));
        }
        if (!compound.contains("bold")) {
        } else {
            style = style.withBold(compound.getBoolean("bold"));
        }
        if (!compound.contains("italic")) {
        } else {
            style = style.withItalic(compound.getBoolean("italic"));
        }
        if (!compound.contains("underlined")) {
        } else {
            style = style.withUnderline(compound.getBoolean("underlined"));
        }
        if (!compound.contains("strikethrough")) {
        } else {
            style = style.withStrikethrough(compound.getBoolean("strikethrough"));
        }
        if (!compound.contains("obfuscated")) {
        } else {
            style = style.withObfuscated(compound.getBoolean("obfuscated"));
        }
        if (!compound.contains("font")) {
            return style;
        }
        try {
            style = style.withFont(Identifier.tryParse(compound.getString("font")));
        } catch (InvalidIdentifierException ignored) {}

        return style;
    }

    public static NbtCompound serializeEnum(Enum<?> enumValue) {
        NbtCompound compound = new NbtCompound();
        compound.putInt("ordinal", enumValue.ordinal());
        return compound;
    }

    public static <T extends Enum<?>> T deserializeEnum(NbtCompound compound, Class<T> clazz) {
        int ordinal = compound.getInt("ordinal");
        return clazz.getEnumConstants()[ordinal];
    }
    public String getPlayerGameMode() {
        assert mc.player != null;
        if(mc.player.getAbilities().creativeMode)
        {
            return "Creative";
        } else if(mc.player.isSpectator()) {
            return "Spectator";
        } else {
            return "Survival";
        }
    }

    public static float getPlayerPitch() {
        if (mc.player != null) {
            return mc.player.getPitch();
        }
        return 0;
    }

    public static float getPlayerYaw() {
        if (mc.player != null) {
            return mc.player.getYaw();
        }
        return 0;
    }

    public static float getPlayerHeadYaw() {
        if (mc.player != null) {
            return mc.player.getHeadYaw();
        }
        return 0;
    }


    public static float getPlayerBodyYaw() {
        if (mc.player != null) {
            return mc.player.getBodyYaw();
        }
        return 0;
    }

    public Optional<GlobalPos> getPlayerLastDeathPos() {
        if (mc.player != null) {
            return mc.player.getLastDeathPos();
        }
        return Optional.empty();
    }

    public boolean isPlayerRiding() {
        if (mc.player != null) {
            return mc.player.isRiding();
        }
        return false;
    }

    public boolean isAlive() {
        if (mc.player != null) {
            return mc.player.isAlive();
        }
        return false;
    }

    public boolean isDead() {
        if (mc.player != null) {
            return mc.player.isDead();
        }
        return false;
    }

    public boolean isSwimming() {
        if (mc.player != null) {
            return mc.player.isSwimming();
        }
        return false;
    }

    public static PlayerInventory getInventory() {
        if (mc.player != null) {
            return mc.player.getInventory();
        }
        return null;
    }

    public static void putNullable(NbtCompound compound, Optional<?> o, String key) {
        if (o.isPresent()) {
            NbtElement e = element(o.get());
            if (e == null) {
                return;
            }
            compound.put(key, e);
        }
    }

    public static NbtElement element(Object o) {
        return o instanceof Boolean b ? NbtByte.of(b) : o instanceof Byte b ? NbtByte.of(b) : o instanceof Double d ? NbtDouble.of(d) : o instanceof CharSequence c ? NbtString.of(c.toString()) : o instanceof Float f ? NbtFloat.of(f) : o instanceof Integer i ? NbtInt.of(i) : o instanceof Long l ? NbtLong.of(l) : null;

    }

    public static boolean insertCreativeStack(ItemStack stack) {
        ItemStack clone = stack.copy();

        int occupied = getOccupiedSlotWithRoomForStack(stack);
        int slot = occupied == -1 ? getEmptySlot() : occupied;

        if ((slot < 0) || (mc.player == null || !mc.player.getInventory().insertStack(stack))) {
            return false;
        } else {
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CreativeInventoryActionC2SPacket(slot, clone));

            return true;
        }
    }

    public static int getEmptySlot() {
        if (mc.player != null && mc.player.getInventory().getStack(0).isEmpty()) {
            return 36;
        }
        if (mc.player.getInventory().getStack(1).isEmpty()) {
            return 37;
        }
        if (mc.player.getInventory().getStack(2).isEmpty()) {
            return 38;
        }
        if (mc.player.getInventory().getStack(3).isEmpty()) {
            return 39;
        }
        if (mc.player.getInventory().getStack(4).isEmpty()) {
            return 40;
        }
        if (mc.player.getInventory().getStack(5).isEmpty()) {
            return 41;
        }
        if (mc.player.getInventory().getStack(6).isEmpty()) {
            return 42;
        }
        if (mc.player.getInventory().getStack(7).isEmpty()) {
            return 43;
        }
        if (mc.player.getInventory().getStack(8).isEmpty()) {
            return 44;
        }

        return IntStream.range(0, mc.player.getInventory().main.size()).filter(i -> mc.player.getInventory().main.get(i).isEmpty()).findFirst().orElse(-1);

    }

    public static int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (!canStackAddMore(mc.player != null ? mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot) : null, stack)) {
            if (!canStackAddMore(mc.player.getInventory().getStack(40), stack)) {
                if (canStackAddMore(mc.player.getInventory().getStack(0), stack)) {
                    return 36;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(1), stack)) {
                    return 37;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(2), stack)) {
                    return 38;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(3), stack)) {
                    return 39;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(4), stack)) {
                    return 40;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(5), stack)) {
                    return 41;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(6), stack)) {
                    return 42;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(7), stack)) {
                    return 43;
                }
                if (canStackAddMore(mc.player.getInventory().getStack(8), stack)) {
                    return 44;
                }

                return IntStream.range(0, mc.player.getInventory().main.size()).filter(i -> canStackAddMore(mc.player.getInventory().main.get(i), stack)).findFirst().orElse(-1);

            } else {
                return 40;
            }
        } else {
            return mc.player.getInventory().selectedSlot;
        }
    }

    public static boolean canStackAddMore(ItemStack existing, ItemStack stack) {
        return !existing.isEmpty() && ItemStack.canCombine(existing, stack) && existing.isStackable() && existing.getCount() < existing.getMaxCount() && existing.getCount() < 64;
    }

    private long nanoTime = -1L;
    public void reset() {
        nanoTime = System.nanoTime();
    }
    public void setTicks(long ticks) { nanoTime = System.nanoTime() - convertTicksToNano(ticks); }
    public void setNano(long time) { nanoTime = System.nanoTime() - time; }
    public void setMicro(long time) { nanoTime = System.nanoTime() - convertMicroToNano(time); }
    public void setMillis(long time) { nanoTime = System.nanoTime() - convertMillisToNano(time); }
    public void setSec(long time) { nanoTime = System.nanoTime() - convertSecToNano(time); }
    public long getTicks() { return convertNanoToTicks(nanoTime); }
    public long getNano() { return nanoTime; }
    public long getMicro() { return convertNanoToMicro(nanoTime); }
    public long getMillis() { return convertNanoToMillis(nanoTime); }
    public long getSec() { return convertNanoToSec(nanoTime); }
    public boolean passedTicks(long ticks) { return passedNano(convertTicksToNano(ticks)); }
    public boolean passedNano(long time) { return System.nanoTime() - nanoTime >= time; }
    public boolean passedMicro(long time) { return passedNano(convertMicroToNano(time)); }
    public boolean passedMillis(long time) { return passedNano(convertMillisToNano(time)); }
    public boolean passedSec(long time) { return passedNano(convertSecToNano(time)); }
    public long convertMillisToTicks(long time) { return time / 50; }
    public long convertTicksToMillis(long ticks) { return ticks * 50; }
    public long convertNanoToTicks(long time) { return convertMillisToTicks(convertNanoToMillis(time)); }
    public long convertTicksToNano(long ticks) { return convertMillisToNano(convertTicksToMillis(ticks)); }
    public long convertSecToMillis(long time) { return time * 1000L; }
    public long convertSecToMicro(long time) { return convertMillisToMicro(convertSecToMillis(time)); }
    public long convertSecToNano(long time) { return convertMicroToNano(convertMillisToMicro(convertSecToMillis(time))); }

    public long convertMillisToMicro(long time) { return time * 1000L; }
    public long convertMillisToNano(long time) { return convertMicroToNano(convertMillisToMicro(time)); }

    public long convertMicroToNano(long time) { return time * 1000L; }
    public long convertNanoToMicro(long time) { return time / 1000L; }
    public long convertNanoToMillis(long time) { return convertMicroToMillis(convertNanoToMicro(time)); }
    public long convertNanoToSec(long time) { return convertMillisToSec(convertMicroToMillis(convertNanoToMicro(time))); }

    public long convertMicroToMillis(long time) { return time / 1000L; }
    public long convertMicroToSec(long time) { return convertMillisToSec(convertMicroToMillis(time)); }
    public long convertMillisToSec(long time) { return time / 1000L; }
    public static double getTPSMatch(boolean TPSSync) {
        return TPSSync ? (TickRate.INSTANCE.getTickRate() / 20) : 1;
    }

    private static final Map<UUID, String> hashCache = new HashMap<>();

    public static UUID getCurrentUuid() {
        return Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getProfile().getId();
    }

    public static String hashUuid(UUID uuid) {
        return hashCache.computeIfAbsent(uuid, obj -> {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
            buffer.putLong(obj.getLeastSignificantBits());
            buffer.putLong(obj.getMostSignificantBits());
            return DigestUtils.sha1Hex(buffer.array());
        });
    }

    public static String hashCurrentUuid() {
        return hashUuid(getCurrentUuid());
    }

    public enum Side {

        LEFT, RIGHT
    }

    private static boolean disablecurrent;

    public static void ImmediateBlockIteratorRegister(int horizontalradius, int verticalradius, BiConsumer<BlockPos, BlockState> function, Block... blocks) {
        if (mc.world == null || mc.player == null) return;


        int px = (int) mc.player.getX();
        int py = (int) mc.player.getY();
        int pz = (int) mc.player.getZ();

        int x = px - horizontalradius;
        while (x <= px + horizontalradius) {
            int z = pz - horizontalradius;
            while (z <= pz + horizontalradius) {
                int y = Math.max(mc.world.getBottomY(), py - verticalradius);
                while (y <= py + verticalradius) {
                    if (y <= mc.world.getTopY()) {
                        BlockPos blockPos = new BlockPos(x, y, z);

                        BlockState blockState = mc.world.getBlockState(blockPos);

                        int dx = Math.abs(x - px);
                        int dy = Math.abs(y - py);
                        int dz = Math.abs(z - pz);


                        if (dx <= horizontalradius && dy <= verticalradius && dz <= horizontalradius && (blocks.length == 0 || Arrays.stream(blocks).anyMatch(block -> block.equals(blockState.getBlock())))) {
                            disablecurrent = false;
                            function.accept(blockPos, blockState);
                            if (disablecurrent) return;
                        }
                        y++;
                    } else {
                        break;
                    }

                }
                z++;
            }
            x++;
        }
    }

    public static void disableCurrent() {
        disablecurrent = true;
    }

    @Nullable
    public static Account<?> getSelectedAccount() {
        String name = mc.getSession().getUsername();
        for (Account<?> acc : Accounts.get()) {
            if (name.equals(acc.getUsername())) {
                return acc;
            }
        }
        return null;
    }

    public static final String getUsername() {
        if (mc.player != null) {
            return String.valueOf(mc.player.getName());
        } else {
            return "Failed.";
        }
    }

    public static double damageReduction(DamageSource source, double damageAmount) {
        if (!source.isScaledWithDifficulty()) {
        } else {
            damageAmount = getDamageForDifficulty(damageAmount);
        }
        assert mc.player != null;
        damageAmount = resistanceReduction(mc.player, damageAmount);
        damageAmount = DamageUtil.getDamageLeft((float) damageAmount, mc.player.getArmor(), (float) mc.player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
        damageAmount = enchantmentReduction(mc.player, damageAmount, source);
        return damageAmount;
    }

    public static double getDamageForDifficulty(double damage) {
        if (mc.world != null) {
            return switch (mc.world.getDifficulty()) {
                case PEACEFUL -> 0;
                case EASY -> Math.min(damage / 2 + 1, damage);
                case HARD -> damage * 3 / 2;
                default -> damage;
            };
        }
        return damage;
    }

    public static double resistanceReduction(LivingEntity player, double damage) {
        if (!player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            return damage < 0 ? 0 : damage;
        }
        int lvl = (Objects.requireNonNull(player.getStatusEffect(StatusEffects.RESISTANCE)).getAmplifier() + 1);
        damage *= (1 - (lvl * 0.2));

        return damage < 0 ? 0 : damage;
    }

    public static double enchantmentReduction(Entity player, double damage, DamageSource source) {
        int protLevel = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), source);
        if (protLevel <= 20) {
        } else {
            protLevel = 20;
        }

        damage *= (1 - (protLevel / 25.0));
        return damage < 0 ? 0 : damage;
    }

    public static boolean isPlayerMoving() {
        if (mc.player != null) {
            return mc.player.forwardSpeed != 0.0F || mc.player.sidewaysSpeed != 0.0F;
        }
        return false;
    }

    public static void clickSlot(int syncId, int slot, int button, SlotActionType action) {
        if (mc.player != null) {
            clickSlot(syncId, mc.player.currentScreenHandler.getRevision(), slot, button, action, mc.player.currentScreenHandler);
        }
    }

    public static void clickSlot(int syncId, int revision, int slot, int button, SlotActionType action) {
        if (mc.player != null) {
            clickSlot(syncId, revision, slot, button, action, mc.player.currentScreenHandler);
        }
    }

    public static void clickSlot(int syncId, int slot, int button, SlotActionType action, ScreenHandler handler) {
        clickSlot(syncId, handler.getRevision(), slot, button, action, handler.slots, handler.getCursorStack());
    }

    public static void clickSlot(int syncId, int revision, int slot, int button, SlotActionType action, ScreenHandler handler) {
        clickSlot(syncId, revision, slot, button, action, handler.slots, handler.getCursorStack());
    }

    public static void clickSlot(int syncId, int revision, int id, int button, SlotActionType action, DefaultedList<Slot> slots, ItemStack cursorStack) {
        Int2ObjectOpenHashMap<ItemStack> stacks = new Int2ObjectOpenHashMap<>();
        List<ItemStack> list = Lists.newArrayListWithCapacity(slots.size());

        int i = 0, slotsSize = slots.size();
        if (i < slotsSize) {
            do {
                Slot slot = slots.get(i);
                list.add(slot.getStack().copy());
                i++;
            } while (i < slotsSize);
        }

        IntStream.range(0, slots.size()).forEach(slot -> {
            ItemStack stack1 = list.get(slot);
            ItemStack stack2 = slots.get(slot).getStack();
            if (!ItemStack.areEqual(stack1, stack2)) stacks.put(slot, stack2.copy());
        });

        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new ClickSlotC2SPacket(syncId, revision, id, button, action, cursorStack.copy(), stacks));
    }


    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dX = x2 - x1;
        double dY = y2 - y1;
        double dZ = z2 - z1;

        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }


    public static double distance(BlockPos pos1, BlockPos pos2) {
        double dX = pos2.getX() - pos1.getX();
        double dY = pos2.getY() - pos1.getY();
        double dZ = pos2.getZ() - pos1.getZ();

        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public static double distanceXZ(Vec3d pos1, Vec3d pos2) {
        double dX = pos1.getX() - pos2.getX();
        double dZ = pos1.getZ() - pos2.getZ();

        return MathHelper.sqrt((float) (dX * dX + dZ * dZ));
    }

    public static double distanceXZ(double x1, double x2, double z1, double z2) {
        double dX = x1 - x2;
        double dZ = z1 - z2;

        return MathHelper.sqrt((float) (dX * dX + dZ * dZ));
    }

    public static double distanceY(Vec3d pos1, Vec3d pos2) {
        return Math.abs(pos2.y - pos1.y);
    }

    public static double distanceY(double y1, double y2) {
        return Math.abs(y1 - y2);
    }

    public static boolean isClickable(Block block) {
        return block instanceof CraftingTableBlock || block instanceof AnvilBlock || block instanceof ButtonBlock || block instanceof AbstractPressurePlateBlock || block instanceof BlockWithEntity || block instanceof BedBlock || block instanceof FenceGateBlock || block instanceof DoorBlock || block instanceof NoteBlock || block instanceof TrapdoorBlock;
    }

    public static boolean inFov(Entity entity, double fov) {
        if (fov >= 360) return true;
        float[] angle = PlayerUtils.calculateAngle(entity.getBoundingBox().getCenter());
        double xDist = 0;
        if (mc.player != null) {
            xDist = MathHelper.angleBetween(angle[0], mc.player.getYaw());
        }
        double yDist = 0;
        if (mc.player != null) {
            yDist = MathHelper.angleBetween(angle[1], mc.player.getPitch());
        }
        double angleDistance = Math.hypot(xDist, yDist);
        return angleDistance <= fov;
    }

    public static float fullFlightMove(PlayerMoveEvent event, double speed, boolean verticalSpeedMatch){
        if (!PlayerUtils.isMoving()) {
            ((IVec3d) event.movement).setXZ(0, 0);
        } else {
            double dir = getDir();

            double xDir = Math.cos(Math.toRadians(dir + 90));
            double zDir = Math.sin(Math.toRadians(dir + 90));

            ((IVec3d) event.movement).setXZ(xDir * speed, zDir * speed);
        }

        float ySpeed = 0;

        if (mc.options.jumpKey.isPressed()) {
            ySpeed += speed;
        }
        if (mc.options.sneakKey.isPressed()) {
            ySpeed -= speed;
        }
        ((IVec3d) event.movement).setY(verticalSpeedMatch ? ySpeed : ySpeed / 2);

        return ySpeed;
    }

    public static double getDir() {
        double dir = 0;

        if (!meteordevelopment.meteorclient.utils.Utils.canUpdate()) {
            return dir;
        }
        if (mc.player != null) {
            dir = mc.player.getYaw() + ((mc.player.forwardSpeed < 0) ? 180 : 0);
        }

        assert mc.player != null;
        if (!(mc.player.sidewaysSpeed > 0)) {
            if (!(mc.player.sidewaysSpeed < 0)) {
                return dir;
            }
            dir += 90F * ((mc.player.forwardSpeed < 0) ? -0.5F : ((mc.player.forwardSpeed > 0) ? 0.5F : 1F));
        } else {
            dir += -90F * ((mc.player.forwardSpeed < 0) ? -0.5F : ((mc.player.forwardSpeed > 0) ? 0.5F : 1F));
        }
        return dir;
    }

    public static String getServerAddress() {
        ServerInfo server = mc.getCurrentServerEntry();
        if (server != null) {
            String serverIp = ServerAddress.parse(server.address).getAddress();
            return serverIp;
        } else {
            return null;
        }
    }

    public static int getServerPort() {
        ServerInfo server = mc.getCurrentServerEntry();
        if (server != null) {
            int serverPort = ServerAddress.parse(server.address).getPort();
            return serverPort;
        } else {
            return 25565;
        }
    }

    public static String generateMessage(int amount) {
        StringBuilder message = new StringBuilder();

        for(int i = 0; i < amount; ++i) {
            message.append((char)(19968 + (int)(Math.random() * 20902.0)));
        }

        return message.toString();
    }

    public static String randomString(int amount) {
        StringBuilder message = new StringBuilder();
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        for(int i = 0; i < amount; ++i) {
            message.append(chars[(new Random()).nextInt(chars.length)]);
        }

        return message.toString();
    }

    public static Vec3d randomPosBetween(int min, int max) {
        int x = (new Random()).nextInt(min, max);
        int y = 255;
        int z = (new Random()).nextInt(min, max);
        return new Vec3d((double)x, (double)y, (double)z);
    }

    public static Vec3d pickRandomPos() {
        int x = (new Random()).nextInt(16777215);
        int y = 255;
        int z = (new Random()).nextInt(16777215);
        return new Vec3d((double)x, (double)y, (double)z);
    }

    public static ItemStack generateItemWithNbt(String nbt, Item item) {
        try {
            ItemStack stack = new ItemStack(item);
            stack.setNbt(StringNbtReader.parse(nbt));
            return stack;
        } catch (Exception var3) {
            return new ItemStack(item);
        }
    }

    public static Vec3d relativeToAbsolute(Vec3d absRootPos, Vec2f rotation, Vec3d relative) {
        double xOffset = relative.x;
        double yOffset = relative.y;
        double zOffset = relative.z;
        float rot = 0.017453292F;
        float f = MathHelper.cos((rotation.y + 90.0F) * rot);
        float g = MathHelper.sin((rotation.y + 90.0F) * rot);
        float h = MathHelper.cos(-rotation.x * rot);
        float i = MathHelper.sin(-rotation.x * rot);
        float j = MathHelper.cos((-rotation.x + 90.0F) * rot);
        float k = MathHelper.sin((-rotation.x + 90.0F) * rot);
        Vec3d vec3d2 = new Vec3d((double)(f * h), (double)i, (double)(g * h));
        Vec3d vec3d3 = new Vec3d((double)(f * j), (double)k, (double)(g * j));
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0);
        double d = vec3d2.x * zOffset + vec3d3.x * yOffset + vec3d4.x * xOffset;
        double e = vec3d2.y * zOffset + vec3d3.y * yOffset + vec3d4.y * xOffset;
        double l = vec3d2.z * zOffset + vec3d3.z * yOffset + vec3d4.z * xOffset;
        return new Vec3d(absRootPos.x + d, absRootPos.y + e, absRootPos.z + l);
    }

    public static double distance(Vec3d vec1, Vec3d vec2) {
        double dX = vec2.x - vec1.x;
        double dY = vec2.y - vec1.y;
        double dZ = vec2.z - vec1.z;
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    public static double[] directionSpeed(double speed) {
        float forward = mc.player.forwardSpeed;
        float side = mc.player.sidewaysSpeed;
        float yaw = mc.player.prevYaw + (MeteorClient.mc.player.getYaw() - mc.player.prevYaw);
        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float)(forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float)(forward > 0.0F ? 45 : -45);
            }

            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double dx = (double)forward * speed * cos + (double)side * speed * sin;
        double dz = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{dx, dz};
    }

    public static int rainbowColors(int colors) {
        int color = 0;
        switch (colors) {
            case 0:
                color = 16711680;
                break;
            case 1:
                color = 16732675;
                break;
            case 2:
                color = 16754178;
                break;
            case 3:
                color = 16768770;
                break;
            case 4:
                color = 14155522;
                break;
            case 5:
                color = 9502464;
                break;
            case 6:
                color = 5373696;
                break;
            case 7:
                color = 65360;
                break;
            case 8:
                color = 65432;
                break;
            case 9:
                color = 65521;
                break;
            case 10:
                color = 45055;
                break;
            case 11:
                color = 15359;
                break;
            case 12:
                color = 3211519;
                break;
            case 13:
                color = 9634047;
                break;
            case 14:
                color = 13762815;
                break;
            case 15:
                color = 16711913;
                break;
            case 16:
                color = 16711859;
                break;
            case 17:
                color = 16711792;
                break;
            case 18:
                color = 16711725;
        }

        return color;
    }


    private static void useBuffer(VertexFormat.DrawMode mode, VertexFormat format, Supplier<ShaderProgram> shader, Consumer<BufferBuilder> runner) {
        Tessellator t = Tessellator.getInstance();
        BufferBuilder bb = t.getBuffer();
        bb.begin(mode, format);
        runner.accept(bb);
        setupRender();
        RenderSystem.setShader(shader);
        BufferRenderer.drawWithGlobalProgram(bb.end());
        endRender();
    }

    private static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void endRender() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

}
