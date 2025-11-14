package com.mirdamod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Altar Compass - Shows players where Mirda's altar is located
 * Spawns near all players to help them find the altar
 */
public class AltarCompassItem extends Item {
    public AltarCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            BlockPos altarPos = findNearestAltar((ServerLevel) level, player.blockPosition());

            if (altarPos != null) {
                double distance = Math.sqrt(player.blockPosition().distSqr(altarPos));
                int blockDistance = (int) distance;

                Component message = Component.literal(
                    "Mirda's Altar is approximately " + blockDistance + " blocks away at " +
                    "X: " + altarPos.getX() + ", Y: " + altarPos.getY() + ", Z: " + altarPos.getZ()
                );

                player.sendSystemMessage(message);
            } else {
                player.sendSystemMessage(Component.literal("No altar found nearby. Mirda's altar has not spawned yet."));
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private BlockPos findNearestAltar(ServerLevel level, BlockPos playerPos) {
        // Search for obsidian structures (altar signature)
        int searchRadius = 1000;

        for (int x = -searchRadius; x <= searchRadius; x += 16) {
            for (int z = -searchRadius; z <= searchRadius; z += 16) {
                BlockPos checkPos = playerPos.offset(x, 0, z);

                // Find surface level
                for (int y = level.getMaxBuildHeight(); y >= level.getMinBuildHeight(); y--) {
                    BlockPos pos = new BlockPos(checkPos.getX(), y, checkPos.getZ());

                    // Check for altar signature (obsidian + lava + redstone)
                    if (isAltarLocation(level, pos)) {
                        return pos;
                    }
                }
            }
        }

        return null;
    }

    private boolean isAltarLocation(ServerLevel level, BlockPos pos) {
        // Simple check for altar blocks
        int obsidianCount = 0;
        int lavaCount = 0;

        for (int x = -5; x <= 5; x++) {
            for (int y = 0; y <= 10; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
                        obsidianCount++;
                    }
                    if (level.getBlockState(checkPos).is(net.minecraft.world.level.block.Blocks.LAVA)) {
                        lavaCount++;
                    }
                }
            }
        }

        // If significant amounts of obsidian and lava, likely an altar
        return obsidianCount > 50 && lavaCount > 10;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Always enchanted glow
    }
}
