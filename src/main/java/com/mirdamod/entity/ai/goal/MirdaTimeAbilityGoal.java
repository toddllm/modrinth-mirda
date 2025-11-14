package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.EnumSet;
import java.util.List;

/**
 * Mirda's time manipulation ability
 * Lifts nearby mobs, stops them from moving, and increases her defense
 */
public class MirdaTimeAbilityGoal extends Goal {
    private final MirdaEntity mirda;
    private int abilityTimer = 0;
    private static final int ABILITY_DURATION = 100; // 5 seconds
    private double originalArmor;

    public MirdaTimeAbilityGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.isTimeAbilityActive()) return true;

        return mirda.getTarget() != null && mirda.random.nextFloat() < 0.05f && mirda.getEnergy() > 3.0f;
    }

    @Override
    public void start() {
        mirda.setTimeAbilityActive(true);
        abilityTimer = 0;
        mirda.getNavigation().stop();

        // Increase defense
        originalArmor = mirda.getAttribute(Attributes.ARMOR).getBaseValue();
        mirda.getAttribute(Attributes.ARMOR).setBaseValue(originalArmor + 20.0);

        mirda.speak("Time bends to my will!");
    }

    @Override
    public void stop() {
        mirda.setTimeAbilityActive(false);
        abilityTimer = 0;

        // Restore defense
        mirda.getAttribute(Attributes.ARMOR).setBaseValue(originalArmor);
    }

    @Override
    public void tick() {
        abilityTimer++;

        // Find nearby mobs and freeze them
        List<LivingEntity> nearbyEntities = mirda.level().getEntitiesOfClass(
            LivingEntity.class,
            mirda.getBoundingBox().inflate(16.0),
            e -> e != mirda && !e.isAlliedTo(mirda)
        );

        for (LivingEntity entity : nearbyEntities) {
            // Apply slowness and levitation
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 1, false, true));

            // Particle effects
            if (mirda.level().isClientSide && mirda.random.nextFloat() < 0.3f) {
                double x = entity.getX() + (mirda.random.nextDouble() - 0.5);
                double y = entity.getY() + entity.getBbHeight() / 2;
                double z = entity.getZ() + (mirda.random.nextDouble() - 0.5);
                mirda.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.PORTAL,
                    x, y, z,
                    0, 0.1, 0
                );
            }
        }

        // Use energy
        if (abilityTimer % 20 == 0) {
            mirda.addEnergy(-0.2f);
        }

        // Particle effects around Mirda
        if (mirda.level().isClientSide && abilityTimer % 5 == 0) {
            for (int i = 0; i < 5; i++) {
                double angle = (Math.PI * 2 * i) / 5.0 + (abilityTimer * 0.1);
                double radius = 3.0;
                double x = mirda.getX() + Math.cos(angle) * radius;
                double z = mirda.getZ() + Math.sin(angle) * radius;
                double y = mirda.getY() + mirda.getBbHeight() / 2;

                mirda.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    x, y, z,
                    0, 0, 0
                );
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return abilityTimer < ABILITY_DURATION && mirda.getEnergy() > 0;
    }
}
