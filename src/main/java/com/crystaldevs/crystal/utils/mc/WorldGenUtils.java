package com.crystaldevs.crystal.utils.mc;

import baritone.api.BaritoneAPI;
import com.crystaldevs.crystal.utils.mc.seeds.Seed;
import com.crystaldevs.crystal.utils.mc.seeds.Seeds;
import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mcfeature.misc.SlimeChunk;
import com.seedfinding.mcfeature.structure.*;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.data.SpiralIterator;
import com.seedfinding.mccore.util.pos.*;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcterrain.TerrainGenerator;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WorldGenUtils {

    private static final Logger LOG = LogManager.getLogger();

    private static final HashMap<Feature, List<Block>> FEATURE_BLOCKS = new HashMap<>(){{
        put(Feature.nether_fortress, Arrays.asList(
            Blocks.NETHER_BRICKS,
            Blocks.NETHER_BRICK_FENCE,
            Blocks.NETHER_WART
        ));
        put(Feature.ocean_monument, Arrays.asList(
            Blocks.PRISMARINE_BRICKS,
            Blocks.SEA_LANTERN,
            Blocks.DARK_PRISMARINE
        ));
        put(Feature.stronghold, Arrays.asList(
            Blocks.END_PORTAL_FRAME,
            Blocks.END_PORTAL
        ));
        put(Feature.end_city, Arrays.asList(
            Blocks.PURPUR_BLOCK,
            Blocks.PURPUR_PILLAR,
            Blocks.PURPUR_STAIRS,
            Blocks.END_ROD
        ));
        put(Feature.village, Arrays.asList(
            Blocks.BELL,
            Blocks.BREWING_STAND,
            Blocks.SMOKER,
            Blocks.BLAST_FURNACE,
            Blocks.FLETCHING_TABLE,
            Blocks.STONECUTTER,
            Blocks.LOOM,
            Blocks.GRINDSTONE,
            Blocks.LECTERN
        ));
        put(Feature.mineshaft, Collections.singletonList(
            Blocks.RAIL
        ));
        put(Feature.desert_pyramid, Arrays.asList(
            Blocks.TNT,
            Blocks.CHISELED_SANDSTONE,
            Blocks.STONE_PRESSURE_PLATE
        ));
    }};

    private static final HashMap<Feature, List<Class<? extends Entity>>> FEATURE_ENTITIES = new HashMap<>(){{
        put(Feature.ocean_monument, Arrays.asList(
            ElderGuardianEntity.class,
            GuardianEntity.class
        ));
        put(Feature.nether_fortress, Arrays.asList(
            BlazeEntity.class,
            WitherSkeletonEntity.class
        ));
        put(Feature.mansion, Collections.singletonList(
            EvokerEntity.class
        ));
        put(Feature.slime_chunk, Collections.singletonList(
            SlimeEntity.class
        ));
        put(Feature.bastion_remnant, Collections.singletonList(
            PiglinBruteEntity.class
        ));
        put(Feature.end_city, Collections.singletonList(
            ShulkerEntity.class
        ));
        put(Feature.village, Arrays.asList(
            VillagerEntity.class,
            IronGolemEntity.class
        ));
        put(Feature.mineshaft, Collections.singletonList(
            ChestMinecartEntity.class
        ));
    }};

    public enum Feature {
        buried_treasure,
        mansion,
        stronghold,
        nether_fortress,
        ocean_monument,
        bastion_remnant,
        end_city,
        village,
        mineshaft,
        slime_chunk,
        desert_pyramid
    }

    public static BlockPos locateFeature(Feature feature, BlockPos center) {
        Seed seed = Seeds.get().getSeed();
        BlockPos pos = null;
        if (checkIfInDimension(getDimension(feature))) {
            if (seed == null) {
            } else {
                try {
                    pos = locateFeature(seed, feature, center);
                } catch (Exception | Error ex) {
                    LOG.error(ex);
                }
                if (pos != null) return pos;
            }
            if (mc.player != null) {
                ItemStack stack = mc.player.getStackInHand(Hand.MAIN_HAND);
                if (stack.getItem() == Items.FILLED_MAP) {
                } else {
                    stack = mc.player.getStackInHand(Hand.OFF_HAND);
                }
                if (stack.getItem() != Items.FILLED_MAP) {
                } else {
                    try {
                        pos = locateFeatureMap(feature, stack);
                    } catch (Exception | Error ex) {
                        LOG.error(ex);
                    }
                    if (pos != null) return pos;
                }
            }
            try {
                pos = locateFeatureEntities(feature);
            } catch (Exception | Error ex) {
                LOG.error(ex);
            }
            if (pos == null) {
                try {
                    pos = locateFeatureBlocks(feature);
                } catch (Exception | Error ex) {
                    LOG.error(ex);
                }
                return pos;
            } else {
                return pos;
            }
        } else {
            return null;
        }
    }

    private static BlockPos locateFeatureMap(Feature feature, ItemStack stack) {
        if (isValidMap(feature, stack)) {
            return getMapMarker(stack);
        }
        return null;
    }

    private static BlockPos locateFeatureBlocks(Feature feature) {
        List<Block> blocks = FEATURE_BLOCKS.get(feature);
        if (blocks != null) {
            List<BlockPos> posList = BaritoneAPI
                .getProvider()
                .getWorldScanner()
                .scanChunkRadius(
                    BaritoneAPI
                        .getProvider()
                        .getPrimaryBaritone()
                        .getPlayerContext(),
                    blocks, 64, 10, 32);
            if (!posList.isEmpty()) {
                if (posList.size() >= 5) {
                    return posList.get(0);
                }
                ChatUtils.warningPrefix("Locate", "Only %d block(s) found. This search might be a false positive.", posList.size());
                return posList.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static BlockPos locateFeatureEntities(Feature feature) {
        List<Class<? extends Entity>> entities = FEATURE_ENTITIES.get(feature);
        if (entities != null) {
            if (mc.world != null) {
                for (Iterator<Entity> iterator = mc.world.getEntities().iterator(); iterator.hasNext(); ) {
                    Entity e = iterator.next();
                    for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
                        Class<? extends Entity> clazz = entities.get(i);
                        if (clazz.isInstance(e))
                            return e.getBlockPos();
                    }
                }
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static BlockPos locateFeature(Seed seed, Feature feature, BlockPos center) {
        if (feature != Feature.slime_chunk) {
            return locateStructure(seed, feature, center);
        }
        return locateSlimeChunk(seed, center);
    }

    private static BlockPos locateSlimeChunk(Seed seed, BlockPos center) {
        Dimension dimension = getDimension(Feature.slime_chunk);
        MCVersion mcVersion = seed.version;
        CPos centerChunk = new CPos(center.getX() >> 4, center.getZ() >> 4);
        CPos slimeChunkPos = locateSlimeChunk(new SlimeChunk(mcVersion), centerChunk, 6400, seed.seed, new ChunkRand(), dimension);
        if (slimeChunkPos != null) {
            return toBlockPos(slimeChunkPos.toBlockPos());
        }
        return null;
    }

    private static CPos locateSlimeChunk(SlimeChunk slimeChunk, CPos centerChunk, int radius, long seed, ChunkRand rand, Dimension dimension) {
        if (!slimeChunk.isValidDimension(dimension))
            return null;
        SpiralIterator<CPos> spiralIterator = new SpiralIterator<>(centerChunk, new CPos(radius, radius), (x, y, z) -> new CPos(x, z));
        for (Iterator<CPos> iterator = spiralIterator.iterator(); iterator.hasNext(); ) {
            CPos next = iterator.next();
            SlimeChunk.Data data = slimeChunk.at(next.getX(), next.getZ(), true);
            if (!data.testStart(seed, rand)) {
            } else {
                return next;
            }
        }
        return null;
    }

    private static BlockPos locateStructure(Seed seed, Feature feature, BlockPos center) {
        Dimension dimension = getDimension(feature);
        if (dimension != Dimension.OVERWORLD || !seed.version.isNewerThan(MCVersion.v1_18)) {
            MCVersion mcVersion = seed.version;
            Structure<?, ?> structure = getStructure(feature, mcVersion);
            if (structure != null) {
                BiomeSource biomeSource = BiomeSource.of(dimension, mcVersion, seed.seed);
                if (structure.isValidDimension(biomeSource.getDimension())) {
                    BPos structurePos = locateStructure(structure, new BPos(center.getX(), center.getY(), center.getZ()), new ChunkRand(), biomeSource, TerrainGenerator.of(biomeSource));
                    if (structurePos != null) {
                        return toBlockPos(structurePos);
                    }
                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static BPos locateStructure(Structure<?, ?> structure, BPos center, ChunkRand chunkRand, BiomeSource source, TerrainGenerator terrainGenerator) {
        if (!(structure instanceof RegionStructure<?, ?> regionStructure)) {
            if (!(structure instanceof Stronghold strongholdStructure)) {
                if (!(structure instanceof Mineshaft mineshaft)) {
                    return null;
                }
                SpiralIterator<CPos> spiralIterator = new SpiralIterator<>(new CPos(center.getX() >> 4, center.getZ() >> 4), new CPos(6400, 6400), (x, y, z) -> new CPos(x, z));

                return StreamSupport.stream(spiralIterator.spliterator(), false)
                    .filter(cPos -> {
                        com.seedfinding.mcfeature.Feature.Data<Mineshaft> data = mineshaft.at(cPos.getX(), cPos.getZ());
                        return data.testStart(source.getWorldSeed(), chunkRand) && data.testBiome(source) && data.testGenerate(terrainGenerator);
                    })
                    .findAny().map(cPos -> cPos.toBlockPos().add(9, 0, 9)).orElse(null);
            } else {
                CPos currentChunkPos = center.toChunkPos();
                int squaredDistance = Integer.MAX_VALUE;
                CPos closest = new CPos(0, 0);
                CPos[] allStarts = strongholdStructure.getAllStarts(source, chunkRand);
                for (int i = 0, allStartsLength = allStarts.length; i < allStartsLength; i++) {
                    CPos stronghold = allStarts[i];
                    int newSquaredDistance = (currentChunkPos.getX() - stronghold.getX()) * (currentChunkPos.getX() - stronghold.getX()) + (currentChunkPos.getZ() - stronghold.getZ()) * (currentChunkPos.getZ() - stronghold.getZ());
                    if (newSquaredDistance >= squaredDistance) {
                        continue;
                    }
                    squaredDistance = newSquaredDistance;
                    closest = stronghold;
                }
                BPos dimPos = closest.toBlockPos().add(9, 0, 9);
                return new BPos(dimPos.getX(), 0, dimPos.getZ());
            }
        } else {
            int chunkInRegion = regionStructure.getSpacing();
            int regionSize = chunkInRegion * 16;

            final int border = 30_000_000;
            SpiralIterator<RPos> spiralIterator = new SpiralIterator<>(center.toRegionPos(regionSize), new BPos(-border, 0, -border).toRegionPos(regionSize), new BPos(border, 0, border).toRegionPos(regionSize), 1, (x, y, z) -> new RPos(x, z, regionSize));
            return StreamSupport.stream(spiralIterator.spliterator(), false)
                .map(rPos -> regionStructure.getInRegion(source.getWorldSeed(), rPos.getX(), rPos.getZ(), chunkRand))
                .filter(Objects::nonNull)
                .filter(cPos -> (regionStructure.canSpawn(cPos, source)) && (terrainGenerator == null || regionStructure.canGenerate(cPos, terrainGenerator)))
                .findAny().map(cPos -> cPos.toBlockPos().add(9, 0, 9)).orElse(null);
        }
    }

    private static Dimension getDimension(Feature feature) {
        return switch (feature) {
            case buried_treasure -> Dimension.OVERWORLD;
            case mansion -> Dimension.OVERWORLD;
            case stronghold -> Dimension.OVERWORLD;
            case nether_fortress -> Dimension.NETHER;
            case ocean_monument -> Dimension.OVERWORLD;
            case bastion_remnant -> Dimension.NETHER;
            case slime_chunk -> Dimension.OVERWORLD;
            case village -> Dimension.OVERWORLD;
            case mineshaft -> Dimension.OVERWORLD;
            case end_city -> Dimension.END;
            case desert_pyramid -> Dimension.OVERWORLD;
            default -> Dimension.OVERWORLD;
        };
    }

    private static boolean checkIfInDimension(Dimension dimension) {
        return switch (dimension) {
            case OVERWORLD ->
                (PlayerUtils.getDimension() == meteordevelopment.meteorclient.utils.world.Dimension.Overworld);
            case NETHER -> (PlayerUtils.getDimension() == meteordevelopment.meteorclient.utils.world.Dimension.Nether);
            case END -> (PlayerUtils.getDimension() == meteordevelopment.meteorclient.utils.world.Dimension.End);
            default -> throw new IllegalArgumentException();
        };
    }

    private static Structure<?, ?> getStructure(Feature feature, MCVersion version) {
        return switch (feature) {
            case buried_treasure -> new BuriedTreasure(version);
            case mansion -> new Mansion(version);
            case stronghold -> new Stronghold(version);
            case nether_fortress -> new Fortress(version);
            case ocean_monument -> new Monument(version);
            case bastion_remnant -> new BastionRemnant(version);
            case end_city -> new EndCity(version);
            case village -> new Village(version);
            case mineshaft -> new Mineshaft(version);
            case desert_pyramid -> new DesertPyramid(version);
            default -> null;
        };
    }

    private static BlockPos toBlockPos(BPos pos) {
        return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    private static boolean isValidMap(Feature feature, ItemStack stack) {
        if (stack.hasNbt()) {
            assert stack.getNbt() != null;
            if (stack.getNbt().contains("display")) {
                NbtCompound displayTag = stack.getNbt().getCompound("display");
                if (displayTag.contains("Name")) {
                    String nameTag = displayTag.getString("Name");
                    if (nameTag.contains("translate")) {
                        return switch (feature) {
                            case buried_treasure -> nameTag.contains("filled_map.buried_treasure");
                            case ocean_monument -> nameTag.contains("filled_map.monument");
                            case mansion -> nameTag.contains("filled_map.mansion");
                            default -> false;
                        };
                    } else {
                        return false;
                    }

                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static BlockPos getMapMarker(ItemStack stack) {
        if (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).contains("Decorations")) {
            NbtList decorationsTag = stack.getNbt().getList("Decorations", NbtElement.COMPOUND_TYPE);
            if (decorationsTag.size() < 1) {
                return null;
            } else {
                NbtCompound iconTag = decorationsTag.getCompound(0);
                return new BlockPos(
                    (int) iconTag.getDouble("x"),
                    (int) iconTag.getDouble("y"),
                    (int) iconTag.getDouble("z")
                );
            }
        } else {
            return null;
        }
    }
}
