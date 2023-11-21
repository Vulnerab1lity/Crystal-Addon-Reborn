package com.crystaldevs.crystal.modules.world;

import com.crystaldevs.crystal.mixin.crystal.IMinecraftClient;
import com.mojang.datafixers.types.templates.Check;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RaycastContext;

import java.util.Random;

public class RandomBuild extends Module {

    private final Setting<Boolean> CheckLineOfsight;
    private final Setting<Boolean> CheckItem;
    private final Setting<Boolean> SwingHand;
    private final Setting<Boolean> FastPlace;
    private final Setting<Boolean> PlaceWhileBreaking;
    private final Setting<Boolean> PlaceWhileRiding;
    private final Setting<Boolean> Indicator;

    private final Setting<Integer> MaxAttempts;
    private final Setting<Integer> Range;

    private final Random random = new Random();
    private BlockPos lastPos;

    public RandomBuild() {
        super(Categories.World, "Random Build", "CRYSTAL || Randomly Builds objects.");
        SettingGroup sgGeneral = settings.getDefaultGroup();
        CheckLineOfsight = sgGeneral.add(new BoolSetting.Builder()
            .name("Check Line Of sight")
            .description("Check your line of sight.")
            .defaultValue(false)
            .build()
        );
        CheckItem = sgGeneral.add(new BoolSetting.Builder()
            .name("Check Item")
            .description("Only builds when you have a block in your hand.")
            .defaultValue(true)
            .build()
        );
        SwingHand = sgGeneral.add(new BoolSetting.Builder()
            .name("Swing Hand")
            .description("If RandomBuild should swing your hand or not.")
            .defaultValue(true)
            .build()
        );
        FastPlace = sgGeneral.add(new BoolSetting.Builder()
            .name("Fast Place")
            .description("Builds with a faster placing time.")
            .defaultValue(false)
            .build()
        );
        PlaceWhileBreaking = sgGeneral.add(new BoolSetting.Builder()
            .name("Place While Breaking")
            .description("Places blocks while you are breaking others.")
            .defaultValue(false)
            .build()
        );
        PlaceWhileRiding = sgGeneral.add(new BoolSetting.Builder()
            .name("Place While Riding")
            .description("{Place blocks while you are riding an entity.")
            .defaultValue(false)
            .build()
        );
        Indicator = sgGeneral.add(new BoolSetting.Builder()
            .name("Indicator")
            .description("Shows you where the blocks will be placed.")
            .defaultValue(true)
            .build()
        );
        MaxAttempts = sgGeneral.add(new IntSetting.Builder()
            .name("Max Attempts")
            .description("Maximum number os positions it will try to place in.")
            .min(1)
            .max(1024)
            .sliderMax(1024)
            .sliderMin(1)
            .defaultValue(128)
            .build()
        );
        Range = sgGeneral.add(new IntSetting.Builder()
            .name("Range")
            .description("How far away blocks can be placed from the player.")
            .min(1)
            .max(1024)
            .sliderMax(1024)
            .sliderMin(1)
            .defaultValue(128)
            .build()
        );
    }

    public void onDeactivate() {
        lastPos = null;
    }

    public void onTick() {
        lastPos = null;
        if(Modules.get().isActive(Freecam.class)) {
            return;
        }

        if (mc.player != null && !FastPlace.get() && mc.player.getItemUseTimeLeft() > 0) {
            return;
        }

        if (mc.player != null && CheckItem.get() && !mc.player.isHolding(stack -> !stack.isEmpty() && stack.getItem() instanceof BlockItem)) {
            return;
        }

        if (mc.interactionManager != null && !PlaceWhileBreaking.get() && mc.interactionManager.isBreakingBlock()) {
            return;
        }

        int MaxAttempts = this.MaxAttempts.get();
        int blockRange = Range.get();
        int bound = blockRange * 2 + 1;
        int attempts = 0;

        BlockPos pos;

        do {
            pos = BlockPos.ofFloored(mc.player.getEyePos().add(random.nextInt(bound) - blockRange, random.nextInt(bound) - blockRange, random.nextInt(bound) - blockRange));
            attempts++;
        }while(attempts < MaxAttempts && !tryToPlaceBlock(pos));
    }

    private boolean tryToPlaceBlock(BlockPos pos) {
        if (mc.world != null && mc.world.getBlockState(pos).isReplaceable()) {
            return false;
        }


        return true;
    }

    private VoxelShape getOutlineShape(BlockPos pos)
    {
        if (mc.world != null) {
            return mc.world.getBlockState(pos).getOutlineShape(mc.world, pos);
        }
        return null;
    }

    public BlockPlacingParams getBlockPlacingParams(BlockPos pos)
    {
        if(getOutlineShape(pos) != VoxelShapes.empty() && mc.world.getBlockState(pos).isReplaceable())
        {
            BlockBreakingParams breakParams =
                getBlockBreakingParams(pos);

            if(breakParams == null)
                return null;

            return new BlockPlacingParams(pos, breakParams.side(),
                breakParams.hitVec(), breakParams.distanceSq(),
                breakParams.lineOfSight());
        }

        Direction[] sides = Direction.values();
        Vec3d[] hitVecs = new Vec3d[sides.length];

        {
            int i = 0;
            while (true) {
                if (i < sides.length) {
                    BlockPos neighbor = pos.offset(sides[i]);
                    BlockState state = null;
                    if (mc.world != null) {
                        state = mc.world.getBlockState(neighbor);
                    }
                    VoxelShape shape = null;
                    if (state != null) {
                        shape = state.getOutlineShape(mc.world, neighbor);
                    }

                    if (state != null && !shape.isEmpty() && !state.isReplaceable()) {
                        Box box = shape.getBoundingBox();
                        Vec3d halfSize = new Vec3d(box.maxX - box.minX, box.maxY - box.minY,
                            box.maxZ - box.minZ).multiply(0.5);
                        Vec3d center = Vec3d.of(neighbor).add(box.getCenter());

                        Vec3i dirVec = sides[i].getOpposite().getVector();
                        Vec3d relHitVec = new Vec3d(halfSize.x * dirVec.getX(),
                            halfSize.y * dirVec.getY(), halfSize.z * dirVec.getZ());
                        hitVecs[i] = center.add(relHitVec);
                    }

                    i++;
                } else {
                    break;
                }
            }
        }

        Vec3d eyesPos = null;
        if (mc.player != null) {
            eyesPos = mc.player.getEyePos();
        }
        Vec3d posVec = Vec3d.ofCenter(pos);

        double distanceSqToPosVec = 0;
        if (eyesPos != null) {
            distanceSqToPosVec = eyesPos.squaredDistanceTo(posVec);
        }
        double[] distancesSq = new double[sides.length];
        boolean[] linesOfSight = new boolean[sides.length];

        for(int i = 0; i < sides.length; i++)
        {
            if(hitVecs[i] == null)
            {
                distancesSq[i] = Double.MAX_VALUE;
                continue;
            }
            if (eyesPos != null) {
                distancesSq[i] = eyesPos.squaredDistanceTo(hitVecs[i]);
            }
            if(distancesSq[i] <= distanceSqToPosVec)
                continue;

            linesOfSight[i] = mc.world
                .raycast(new RaycastContext(eyesPos, hitVecs[i],
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, mc.player))
                .getType() == HitResult.Type.MISS;
        }

        Direction side = sides[0];
        for(int i = 1; i < sides.length; i++)
        {
            int bestSide = side.ordinal();

            if(hitVecs[i] == null)
                continue;

            if(!linesOfSight[bestSide] && linesOfSight[i])
            {
                side = sides[i];
                continue;
            }

            if(linesOfSight[bestSide] && !linesOfSight[i])
                continue;

            if(distancesSq[i] > distancesSq[bestSide])
                side = sides[i];
        }

        if(hitVecs[side.ordinal()] == null)
            return null;

        return new BlockPlacingParams(pos.offset(side), side.getOpposite(),
            hitVecs[side.ordinal()], distancesSq[side.ordinal()],
            linesOfSight[side.ordinal()]);
    }

    public static record BlockPlacingParams(BlockPos neighbor, Direction side, Vec3d hitVec, double distanceSq, boolean lineOfSight)
    {
        public BlockHitResult toHitResult()
        {
            return new BlockHitResult(hitVec, side, neighbor, false);
        }
    }

    public boolean breakOneBlock(BlockPos pos)
    {
        BlockBreakingParams params = getBlockBreakingParams(pos);
        if (params != null) {
            if (mc.interactionManager != null && !mc.interactionManager.updateBlockBreakingProgress(pos, params.side)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    public BlockBreakingParams getBlockBreakingParams(BlockPos pos)
    {
        Direction[] sides = Direction.values();

        BlockState state = null;
        if (mc.world != null) {
            state = mc.world.getBlockState(pos);
        }
        VoxelShape shape = null;
        if (state != null) {
            shape = state.getOutlineShape(mc.world, pos);
        }
        if (shape == null || !shape.isEmpty()) {
            Vec3d eyesPos = null;
            if (mc.player != null) {
                eyesPos = mc.player.getEyePos();
            }
            Box box = null;
            if (shape != null) {
                box = shape.getBoundingBox();
            }
            Vec3d halfSize = null;
            if (box != null) {
                halfSize = new Vec3d(box.maxX - box.minX, box.maxY - box.minY,
                    box.maxZ - box.minZ).multiply(0.5);
            }
            Vec3d center = null;
            if (box != null) {
                center = Vec3d.of(pos).add(box.getCenter());
            }

            Vec3d[] hitVecs = new Vec3d[sides.length];
            for (int i = 0; i < sides.length; i++) {
                Vec3i dirVec = sides[i].getVector();
                Vec3d relHitVec = null;
                if (halfSize != null) {
                    relHitVec = new Vec3d(halfSize.x * dirVec.getX(),
                        halfSize.y * dirVec.getY(), halfSize.z * dirVec.getZ());
                }
                if (center != null) {
                    hitVecs[i] = center.add(relHitVec);
                }
            }

            double distanceSqToCenter = 0;
            if (eyesPos != null) {
                distanceSqToCenter = eyesPos.squaredDistanceTo(center);
            }
            double[] distancesSq = new double[sides.length];
            boolean[] linesOfSight = new boolean[sides.length];

            for (int i = 0; i < sides.length; i++) {
                if (eyesPos != null) {
                    distancesSq[i] = eyesPos.squaredDistanceTo(hitVecs[i]);
                }
                if (distancesSq[i] >= distanceSqToCenter)
                    continue;

                linesOfSight[i] = mc.world
                    .raycast(new RaycastContext(eyesPos, hitVecs[i],
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE, mc.player))
                    .getType() == HitResult.Type.MISS;
            }

            Direction side = sides[0];
            for (int i = 1; i < sides.length; i++) {
                int bestSide = side.ordinal();

                if (!linesOfSight[bestSide] && linesOfSight[i]) {
                    side = sides[i];
                    continue;
                }

                if (linesOfSight[bestSide] && !linesOfSight[i])
                    continue;

                if (distancesSq[i] < distancesSq[bestSide])
                    side = sides[i];
            }

            return new BlockBreakingParams(side, hitVecs[side.ordinal()],
                distancesSq[side.ordinal()], linesOfSight[side.ordinal()]);
        } else {
            return null;
        }

    }

    public static record BlockBreakingParams(Direction side, Vec3d hitVec,
                                             double distanceSq, boolean lineOfSight)
    {}

    public void breakBlocksWithPacketSpam(Iterable<BlockPos> blocks)
    {
        Vec3d eyesPos = null;
        if (mc.player != null) {
            eyesPos = mc.player.getEyePos();
        }
        ClientPlayNetworkHandler netHandler = null;
        if (mc.player != null) {
            netHandler = mc.player.networkHandler;
        }

        for(BlockPos pos : blocks)
        {
            Vec3d posVec = Vec3d.ofCenter(pos);
            double distanceSqPosVec = 0;
            if (eyesPos != null) {
                distanceSqPosVec = eyesPos.squaredDistanceTo(posVec);
            }

            Direction[] values = Direction.values();
            int i = 0;
            while (true) {
                if (i < values.length) {
                    Direction side = values[i];
                    Vec3d hitVec =
                        posVec.add(Vec3d.of(side.getVector()).multiply(0.5));

                    if (eyesPos != null && !(eyesPos.squaredDistanceTo(hitVec) >= distanceSqPosVec)) {
                        if (netHandler != null) {
                            netHandler.sendPacket(new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, side));
                        }
                        if (netHandler != null) {
                            netHandler.sendPacket(new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, side));
                        }

                        break;
                    }

                    i++;
                } else {
                    break;
                }
            }
        }
    }
}
