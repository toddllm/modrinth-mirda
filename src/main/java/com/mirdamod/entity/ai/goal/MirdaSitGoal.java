package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Goal that makes Mirda sit to regenerate health
 * Mirda enjoys sitting and will do so periodically, especially when recovering
 */
public class MirdaSitGoal extends Goal {
    private final MirdaEntity mirda;
    private int sitTimer = 0;
    private static final int SIT_DURATION = 200; // 10 seconds

    public MirdaSitGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Sit if health is low, or randomly, or after certain attack cycles
        if (mirda.isSitting()) return true;

        float healthPercent = mirda.getHealth() / mirda.getMaxHealth();
        if (healthPercent < 0.3f && mirda.random.nextFloat() < 0.1f) {
            return true;
        }

        // After attack cycle, 50% chance to sit
        if (mirda.getAttackCycle() > 400 && mirda.random.nextFloat() < 0.5f) {
            return true;
        }

        // Randomly sit (Mirda enjoys sitting!)
        return mirda.random.nextFloat() < 0.01f;
    }

    @Override
    public boolean canContinueToUse() {
        return sitTimer < SIT_DURATION;
    }

    @Override
    public void start() {
        mirda.setSitting(true);
        mirda.setFlying(false);
        sitTimer = 0;
        mirda.getNavigation().stop();
    }

    @Override
    public void stop() {
        mirda.setSitting(false);
        sitTimer = 0;
        mirda.resetAttackCycle();
    }

    @Override
    public void tick() {
        sitTimer++;
        // Regenerate while sitting
        if (sitTimer % 20 == 0) {
            mirda.heal(mirda.getMaxHealth() * 0.05f);
        }
    }
}
