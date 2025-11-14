package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

/**
 * Mirda consumes other mobs to get stronger and gain health
 * Only consumes one thing at a time
 */
public class MirdaConsumeGoal extends Goal {
    private final MirdaEntity mirda;
    private LivingEntity targetToConsume;
    private int consumeTimer = 0;
    private static final int CONSUME_DURATION = 40; // 2 seconds to consume

    public MirdaConsumeGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;

        // Look for nearby mobs to consume (prefer low health ones)
        List<LivingEntity> nearbyMobs = mirda.level().getEntitiesOfClass(
            LivingEntity.class,
            mirda.getBoundingBox().inflate(5.0),
            this::canConsume
        );

        if (nearbyMobs.isEmpty()) return false;

        // Find lowest health mob
        targetToConsume = nearbyMobs.stream()
            .min((a, b) -> Float.compare(a.getHealth(), b.getHealth()))
            .orElse(null);

        return targetToConsume != null && mirda.random.nextFloat() < 0.1f;
    }

    private boolean canConsume(LivingEntity entity) {
        if (entity instanceof MirdaEntity) return false;
        if (entity.isAlliedTo(mirda)) return false;
        if (!entity.isAlive()) return false;

        // Prefer weakened enemies
        return entity.getHealth() < entity.getMaxHealth() * 0.5f;
    }

    @Override
    public void start() {
        consumeTimer = 0;
        mirda.getNavigation().stop();
    }

    @Override
    public void stop() {
        targetToConsume = null;
        consumeTimer = 0;
    }

    @Override
    public void tick() {
        if (targetToConsume == null || !targetToConsume.isAlive()) {
            return;
        }

        mirda.getLookControl().setLookAt(targetToConsume);
        consumeTimer++;

        // Pull target towards Mirda
        if (mirda.distanceTo(targetToConsume) > 2.0) {
            double dx = mirda.getX() - targetToConsume.getX();
            double dy = mirda.getY() - targetToConsume.getY();
            double dz = mirda.getZ() - targetToConsume.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist > 0) {
                targetToConsume.push(dx / dist * 0.1, dy / dist * 0.1 + 0.05, dz / dist * 0.1);
            }
        }

        // Consume after timer
        if (consumeTimer >= CONSUME_DURATION && mirda.distanceTo(targetToConsume) < 3.0) {
            mirda.consumeMob(targetToConsume);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return targetToConsume != null && targetToConsume.isAlive() && consumeTimer < CONSUME_DURATION + 20;
    }
}
