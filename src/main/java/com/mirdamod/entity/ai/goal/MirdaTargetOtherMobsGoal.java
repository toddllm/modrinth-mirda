package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;

/**
 * Mirda targets other powerful mobs to consume them
 * Prioritizes bosses and strong creatures
 */
public class MirdaTargetOtherMobsGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final MirdaEntity mirda;

    public MirdaTargetOtherMobsGoal(MirdaEntity mirda) {
        super(mirda, LivingEntity.class, 10, true, false, MirdaTargetOtherMobsGoal::isValidTarget);
        this.mirda = mirda;
    }

    private static boolean isValidTarget(LivingEntity entity) {
        if (entity instanceof MirdaEntity) return false;

        // Prioritize powerful mobs
        return entity instanceof EnderDragon ||
               entity instanceof WitherBoss ||
               entity instanceof IronGolem ||
               (entity instanceof Monster && entity.getMaxHealth() > 20.0f);
    }

    @Override
    public boolean canUse() {
        // Only target other mobs if no player target or looking to consume
        boolean result = super.canUse();
        return result && (mirda.getTarget() == null || mirda.random.nextFloat() < 0.3f);
    }
}
