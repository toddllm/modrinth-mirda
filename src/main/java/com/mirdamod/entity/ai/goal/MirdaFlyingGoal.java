package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Goal that makes Mirda fly around
 * Can strike lightning and crash into players while flying
 */
public class MirdaFlyingGoal extends Goal {
    private final MirdaEntity mirda;
    private Vec3 targetPosition;
    private int flyTimer = 0;
    private static final int MAX_FLY_TIME = 300; // 15 seconds

    public MirdaFlyingGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.isFlying()) return true;

        // Start flying randomly or when low health
        float healthPercent = mirda.getHealth() / mirda.getMaxHealth();
        if (healthPercent < 0.5f && mirda.random.nextFloat() < 0.2f) {
            return true;
        }

        return mirda.random.nextFloat() < 0.05f;
    }

    @Override
    public boolean canContinueToUse() {
        return flyTimer < MAX_FLY_TIME && !mirda.isSitting();
    }

    @Override
    public void start() {
        mirda.setFlying(true);
        flyTimer = 0;
        pickNewFlyTarget();
    }

    @Override
    public void stop() {
        mirda.setFlying(false);
        flyTimer = 0;
    }

    @Override
    public void tick() {
        flyTimer++;

        // Move towards target position
        if (targetPosition != null) {
            Vec3 currentPos = mirda.position();
            Vec3 direction = targetPosition.subtract(currentPos).normalize();

            // Apply movement
            mirda.setDeltaMovement(direction.scale(0.3));

            // Check if reached target
            if (currentPos.distanceTo(targetPosition) < 2.0) {
                pickNewFlyTarget();
            }
        }

        // Hover over target and attack
        LivingEntity target = mirda.getTarget();
        if (target != null && mirda.random.nextFloat() < 0.1f) {
            // Crash into player
            Vec3 toTarget = target.position().subtract(mirda.position()).normalize();
            mirda.setDeltaMovement(toTarget.scale(0.8));

            // Check for collision
            if (mirda.distanceTo(target) < 2.0) {
                target.hurt(mirda.damageSources().mobAttack(mirda), 25.0f);
                target.push(toTarget.x, 0.5, toTarget.z);
            }
        }

        // Strike lightning while flying
        if (flyTimer % 40 == 0 && target != null) {
            // Will be implemented in lightning goal
        }

        // Refresh target periodically
        if (flyTimer % 60 == 0) {
            pickNewFlyTarget();
        }
    }

    private void pickNewFlyTarget() {
        LivingEntity target = mirda.getTarget();
        if (target != null) {
            // Fly around target
            double angle = mirda.random.nextDouble() * Math.PI * 2;
            double radius = 10.0 + mirda.random.nextDouble() * 10.0;
            double x = target.getX() + Math.cos(angle) * radius;
            double y = target.getY() + 10.0 + mirda.random.nextDouble() * 10.0;
            double z = target.getZ() + Math.sin(angle) * radius;
            targetPosition = new Vec3(x, y, z);
        } else {
            // Fly randomly
            double x = mirda.getX() + (mirda.random.nextDouble() - 0.5) * 20.0;
            double y = mirda.getY() + (mirda.random.nextDouble() - 0.5) * 10.0;
            double z = mirda.getZ() + (mirda.random.nextDouble() - 0.5) * 20.0;
            targetPosition = new Vec3(x, Math.max(y, mirda.level().getSeaLevel() + 10), z);
        }
    }
}
