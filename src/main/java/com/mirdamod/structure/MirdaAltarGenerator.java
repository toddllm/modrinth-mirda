package com.mirdamod.structure;

import com.mirdamod.MirdaMod;
import com.mirdamod.entity.ModEntities;
import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Generates Mirda's altar/castle structure
 * A huge obsidian castle with lava, redstone, gold, and diamond
 * Creeper banners on glass windows
 * Lightning strikes around it
 * Hearts scattered inside
 */
public class MirdaAltarGenerator {

    /**
     * Generates the altar at the specified position
     */
    public static void generateAltar(ServerLevel level, BlockPos centerPos) {
        MirdaMod.LOGGER.info("Generating Mirda's altar at " + centerPos);

        // Build the base platform
        buildBasePlatform(level, centerPos);

        // Build the main castle structure
        buildCastleWalls(level, centerPos);

        // Add interior features
        buildInterior(level, centerPos);

        // Add towers
        buildTowers(level, centerPos);

        // Spawn lightning around the altar
        spawnLightningEffects(level, centerPos);

        // Spawn Mirda
        spawnMirda(level, centerPos);

        MirdaMod.LOGGER.info("Mirda's altar completed!");
    }

    private static void buildBasePlatform(ServerLevel level, BlockPos centerPos) {
        int radius = 20;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance <= radius) {
                    BlockPos pos = centerPos.offset(x, -1, z);

                    // Obsidian platform with lava channels
                    if (Math.abs(x) % 4 == 0 || Math.abs(z) % 4 == 0) {
                        setBlock(level, pos, Blocks.LAVA);
                    } else {
                        setBlock(level, pos, Blocks.OBSIDIAN);
                    }

                    // Redstone blocks scattered
                    if ((x + z) % 7 == 0) {
                        setBlock(level, pos.above(), Blocks.REDSTONE_BLOCK);
                    }
                }
            }
        }
    }

    private static void buildCastleWalls(ServerLevel level, BlockPos centerPos) {
        int wallRadius = 15;
        int height = 12;

        for (int y = 0; y < height; y++) {
            for (int x = -wallRadius; x <= wallRadius; x++) {
                for (int z = -wallRadius; z <= wallRadius; z++) {
                    double distance = Math.sqrt(x * x + z * z);

                    // Outer wall (circular)
                    if (distance >= wallRadius - 1 && distance <= wallRadius) {
                        BlockPos pos = centerPos.offset(x, y, z);

                        // Obsidian walls with glass windows
                        if (y >= 4 && y <= 8 && (x % 3 == 0 || z % 3 == 0)) {
                            setBlock(level, pos, Blocks.GLASS);

                            // Creeper banners on windows
                            if (y == 8 && x % 6 == 0) {
                                // Banner placement would go here
                            }
                        } else {
                            setBlock(level, pos, Blocks.OBSIDIAN);
                        }

                        // Gold and diamond accents
                        if (y % 3 == 0) {
                            if ((x + z) % 2 == 0) {
                                setBlock(level, pos, Blocks.GOLD_BLOCK);
                            } else if ((x + z) % 3 == 0) {
                                setBlock(level, pos, Blocks.DIAMOND_BLOCK);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void buildInterior(ServerLevel level, BlockPos centerPos) {
        // Central throne room
        int radius = 8;
        int height = 10;

        // Clear interior
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.sqrt(x * x + z * z) < radius) {
                        BlockPos pos = centerPos.offset(x, y, z);
                        setBlock(level, pos, Blocks.AIR);
                    }
                }
            }
        }

        // Hearts scattered on floor
        for (int i = 0; i < 20; i++) {
            int x = level.random.nextInt(radius * 2) - radius;
            int z = level.random.nextInt(radius * 2) - radius;
            if (Math.sqrt(x * x + z * z) < radius) {
                BlockPos pos = centerPos.offset(x, 0, z);
                // Hearts would be items, can't place as blocks
                // Could use decorated pots or other decorative blocks
                setBlock(level, pos, Blocks.REDSTONE_BLOCK);
            }
        }

        // Central throne
        BlockPos thronePos = centerPos.offset(0, 0, 5);
        setBlock(level, thronePos, Blocks.DIAMOND_BLOCK);
        setBlock(level, thronePos.above(), Blocks.DIAMOND_BLOCK);
        setBlock(level, thronePos.offset(-1, 0, 0), Blocks.DIAMOND_BLOCK);
        setBlock(level, thronePos.offset(1, 0, 0), Blocks.DIAMOND_BLOCK);

        // Lava pools
        for (int i = 0; i < 4; i++) {
            double angle = (Math.PI * 2 * i) / 4.0;
            int x = (int) (Math.cos(angle) * 6);
            int z = (int) (Math.sin(angle) * 6);
            BlockPos lavaPos = centerPos.offset(x, 0, z);

            for (int lx = -1; lx <= 1; lx++) {
                for (int lz = -1; lz <= 1; lz++) {
                    setBlock(level, lavaPos.offset(lx, 0, lz), Blocks.LAVA);
                }
            }
        }
    }

    private static void buildTowers(ServerLevel level, BlockPos centerPos) {
        // Four corner towers
        int[] offsets = {-18, 18};

        for (int x : offsets) {
            for (int z : offsets) {
                buildSingleTower(level, centerPos.offset(x, 0, z));
            }
        }
    }

    private static void buildSingleTower(ServerLevel level, BlockPos basePos) {
        int height = 20;

        for (int y = 0; y < height; y++) {
            // Tower base (3x3)
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = basePos.offset(x, y, z);

                    if (x == 0 && z == 0) {
                        // Hollow center
                        setBlock(level, pos, Blocks.AIR);
                    } else {
                        // Alternate gold/diamond blocks
                        if (y % 2 == 0) {
                            setBlock(level, pos, Blocks.GOLD_BLOCK);
                        } else {
                            setBlock(level, pos, Blocks.DIAMOND_BLOCK);
                        }
                    }
                }
            }
        }

        // Tower top
        BlockPos topPos = basePos.above(height);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    setBlock(level, topPos.offset(x, 0, z), Blocks.GOLD_BLOCK);
                }
            }
        }
    }

    private static void spawnLightningEffects(ServerLevel level, BlockPos centerPos) {
        // Spawn lightning bolts around the altar
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 * i) / 8.0;
            double radius = 20.0;
            double x = centerPos.getX() + Math.cos(angle) * radius;
            double z = centerPos.getZ() + Math.sin(angle) * radius;

            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
            if (lightning != null) {
                lightning.moveTo(x, centerPos.getY(), z);
                level.addFreshEntity(lightning);
            }
        }
    }

    private static void spawnMirda(ServerLevel level, BlockPos centerPos) {
        // Spawn Mirda at the center of the altar
        MirdaEntity mirda = new MirdaEntity(ModEntities.MIRDA.get(), level);
        BlockPos spawnPos = centerPos.offset(0, 1, 0);
        mirda.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        mirda.setAltarPosition(centerPos);

        // Mirda spawns automatically in the altar
        level.addFreshEntity(mirda);

        MirdaMod.LOGGER.info("Mirda spawned at altar!");
    }

    private static void setBlock(ServerLevel level, BlockPos pos, net.minecraft.world.level.block.Block block) {
        level.setBlock(pos, block.defaultBlockState(), 3);
    }
}
