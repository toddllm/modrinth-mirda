package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Goal for Mirda's lightning attacks
 * Can strike lightning from the sky or from her hands/dragon heads
 */
public class MirdaLightningAttackGoal extends Goal {
    private final MirdaEntity mirda;
    private int attackTimer = 0;
    private static final int ATTACK_INTERVAL = 60; // 3 seconds between attacks

    public MirdaLightningAttackGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.getAttackCooldown() > 0) return false;

        return mirda.getTarget() != null && mirda.random.nextFloat() < 0.2f;
    }

    @Override
    public void start() {
        attackTimer = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = mirda.getTarget();
        if (target == null) return;

        attackTimer++;

        if (attackTimer >= ATTACK_INTERVAL) {
            strikeLightning(target);
            attackTimer = 0;

            // Extra lightning from dragon heads
            for (int i = 0; i < mirda.getDragonHeads(); i++) {
                if (mirda.random.nextFloat() < 0.5f) {
                    strikeLightningNearby(target, 5.0);
                }
            }
        }
    }

    private void strikeLightning(LivingEntity target) {
        if (mirda.level().isClientSide) return;

        BlockPos pos = target.blockPosition();
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(mirda.level());
        if (lightning != null) {
            lightning.moveTo(pos.getX(), pos.getY(), pos.getZ());
            mirda.level().addFreshEntity(lightning);
        }

        mirda.setAttackCooldown(20);
    }

    private void strikeLightningNearby(LivingEntity target, double radius) {
        if (mirda.level().isClientSide) return;

        double x = target.getX() + (mirda.random.nextDouble() - 0.5) * radius * 2;
        double z = target.getZ() + (mirda.random.nextDouble() - 0.5) * radius * 2;

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(mirda.level());
        if (lightning != null) {
            lightning.moveTo(x, target.getY(), z);
            mirda.level().addFreshEntity(lightning);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return mirda.getTarget() != null && attackTimer < ATTACK_INTERVAL * 3;
    }
}
