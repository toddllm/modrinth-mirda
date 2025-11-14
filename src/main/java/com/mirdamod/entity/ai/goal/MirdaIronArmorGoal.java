package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Mirda covers herself in iron blocks
 * Players need to break the blocks to hit her
 */
public class MirdaIronArmorGoal extends Goal {
    private final MirdaEntity mirda;
    private List<BlockPos> armorBlocks = new ArrayList<>();
    private int armorTimer = 0;

    public MirdaIronArmorGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.isIronArmorActive()) return true;

        float healthPercent = mirda.getHealth() / mirda.getMaxHealth();
        return healthPercent < 0.4f && mirda.random.nextFloat() < 0.15f;
    }

    @Override
    public void start() {
        mirda.setIronArmorActive(true);
        armorTimer = 0;
        armorBlocks.clear();
        createIronArmor();
        mirda.speak("You cannot touch me now!");
    }

    @Override
    public void stop() {
        removeIronArmor();
        armorBlocks.clear();
        armorTimer = 0;
    }

    @Override
    public void tick() {
        armorTimer++;

        // Check if armor blocks are broken
        int remainingBlocks = 0;
        for (BlockPos pos : armorBlocks) {
            if (mirda.level().getBlockState(pos).is(Blocks.IRON_BLOCK)) {
                remainingBlocks++;
            }
        }

        // If most armor is destroyed, deactivate
        if (remainingBlocks < armorBlocks.size() / 2) {
            mirda.setIronArmorActive(false);
        }

        // Refresh armor blocks occasionally
        if (armorTimer % 100 == 0 && remainingBlocks < armorBlocks.size()) {
            repairArmor();
        }
    }

    private void createIronArmor() {
        if (mirda.level().isClientSide) return;

        BlockPos center = mirda.blockPosition();

        // Create a shell of iron blocks around Mirda
        for (int y = 0; y <= 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    // Skip the center to leave some gaps for Mirda
                    if (x == 0 && z == 0) continue;

                    BlockPos pos = center.offset(x, y, z);
                    if (mirda.level().getBlockState(pos).isAir()) {
                        mirda.level().setBlock(pos, Blocks.IRON_BLOCK.defaultBlockState(), 3);
                        armorBlocks.add(pos);
                    }
                }
            }
        }
    }

    private void repairArmor() {
        if (mirda.level().isClientSide) return;

        for (BlockPos pos : armorBlocks) {
            if (!mirda.level().getBlockState(pos).is(Blocks.IRON_BLOCK)) {
                if (mirda.level().getBlockState(pos).isAir()) {
                    mirda.level().setBlock(pos, Blocks.IRON_BLOCK.defaultBlockState(), 3);
                }
            }
        }
    }

    private void removeIronArmor() {
        if (mirda.level().isClientSide) return;

        for (BlockPos pos : armorBlocks) {
            if (mirda.level().getBlockState(pos).is(Blocks.IRON_BLOCK)) {
                mirda.level().destroyBlock(pos, true);
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return mirda.isIronArmorActive() && armorTimer < 600; // Max 30 seconds
    }
}
