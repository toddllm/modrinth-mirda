package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.ModEntities;
import com.mirdamod.entity.boss.MirdaEntity;
import com.mirdamod.entity.summon.AdvancedSkeletonEntity;
import com.mirdamod.entity.summon.HealingTowerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

/**
 * Mirda summons minions to help her:
 * - Advanced skeletons with powers
 * - Healing towers
 * - Zombies with netherite armor
 */
public class MirdaSummonGoal extends Goal {
    private final MirdaEntity mirda;
    private int summonCooldown = 0;
    private static final int MAX_SUMMONS = 10;

    public MirdaSummonGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (summonCooldown > 0) {
            summonCooldown--;
            return false;
        }

        // Count existing summons
        long existingSummons = mirda.level().getEntitiesOfClass(
            AdvancedSkeletonEntity.class,
            mirda.getBoundingBox().inflate(32.0)
        ).size();

        return existingSummons < MAX_SUMMONS && mirda.random.nextFloat() < 0.1f && mirda.getEnergy() > 2.0f;
    }

    @Override
    public void start() {
        summonCooldown = 200; // 10 seconds between summons

        // Randomly choose what to summon
        float rand = mirda.random.nextFloat();

        if (rand < 0.4f) {
            summonAdvancedSkeleton();
        } else if (rand < 0.7f) {
            summonNetheriteZombie();
        } else {
            summonHealingTower();
        }

        mirda.addEnergy(-1.0f);
    }

    private void summonAdvancedSkeleton() {
        if (mirda.level().isClientSide) return;

        for (int i = 0; i < 3; i++) { // Summon 3 mini-bone skeletons
            AdvancedSkeletonEntity skeleton = new AdvancedSkeletonEntity(
                ModEntities.ADVANCED_SKELETON.get(),
                mirda.level()
            );

            BlockPos spawnPos = findSpawnPosition();
            skeleton.moveTo(spawnPos, 0, 0);

            mirda.level().addFreshEntity(skeleton);
        }

        mirda.speak("Rise, my skeletal warriors!");
    }

    private void summonNetheriteZombie() {
        if (mirda.level().isClientSide) return;

        Zombie zombie = EntityType.ZOMBIE.create(mirda.level());
        if (zombie != null) {
            BlockPos spawnPos = findSpawnPosition();
            zombie.moveTo(spawnPos, 0, 0);

            // Equip with netherite armor and sword
            zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
            zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
            zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
            zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
            zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));

            // Make equipment permanent
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0f);
            zombie.setDropChance(EquipmentSlot.CHEST, 0.0f);
            zombie.setDropChance(EquipmentSlot.LEGS, 0.0f);
            zombie.setDropChance(EquipmentSlot.FEET, 0.0f);
            zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0f);

            mirda.level().addFreshEntity(zombie);
        }

        mirda.speak("My armored soldiers shall protect me!");
    }

    private void summonHealingTower() {
        if (mirda.level().isClientSide) return;

        // Count existing towers
        long towerCount = mirda.level().getEntitiesOfClass(
            HealingTowerEntity.class,
            mirda.getBoundingBox().inflate(32.0)
        ).size();

        if (towerCount >= 4) return; // Max 4 towers

        HealingTowerEntity tower = new HealingTowerEntity(
            ModEntities.HEALING_TOWER.get(),
            mirda.level()
        );

        BlockPos spawnPos = findTowerPosition();
        tower.moveTo(spawnPos, 0, 0);
        tower.setMirda(mirda);

        mirda.level().addFreshEntity(tower);

        mirda.speak("My towers shall sustain me!");
    }

    private BlockPos findSpawnPosition() {
        // Find a position near Mirda
        for (int i = 0; i < 10; i++) {
            double angle = mirda.random.nextDouble() * Math.PI * 2;
            double radius = 5.0 + mirda.random.nextDouble() * 5.0;
            double x = mirda.getX() + Math.cos(angle) * radius;
            double z = mirda.getZ() + Math.sin(angle) * radius;

            BlockPos pos = BlockPos.containing(x, mirda.getY(), z);

            // Find ground
            while (mirda.level().isEmptyBlock(pos) && pos.getY() > mirda.level().getMinBuildHeight()) {
                pos = pos.below();
            }
            pos = pos.above();

            if (mirda.level().isEmptyBlock(pos) && mirda.level().isEmptyBlock(pos.above())) {
                return pos;
            }
        }

        return mirda.blockPosition();
    }

    private BlockPos findTowerPosition() {
        // Find a position around Mirda in cardinal directions
        int[] offsets = {-8, 8};
        for (int xOff : offsets) {
            for (int zOff : offsets) {
                BlockPos pos = mirda.blockPosition().offset(xOff, 0, zOff);

                // Find ground
                while (mirda.level().isEmptyBlock(pos) && pos.getY() > mirda.level().getMinBuildHeight()) {
                    pos = pos.below();
                }
                pos = pos.above();

                if (mirda.level().isEmptyBlock(pos) && mirda.level().isEmptyBlock(pos.above())) {
                    return pos;
                }
            }
        }

        return mirda.blockPosition();
    }

    @Override
    public boolean canContinueToUse() {
        return false; // One-shot goal
    }
}
