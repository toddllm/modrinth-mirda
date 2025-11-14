package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Mirda's strongest move - Blackhole Attack
 * Draws and pulls all nearby-far mobs into it, damages them,
 * uses their energy to restore health, then crushes and consumes their souls
 */
public class MirdaBlackholeAttackGoal extends Goal {
    private final MirdaEntity mirda;
    private int blackholeTimer = 0;
    private static final int CHARGE_TIME = 60; // 3 seconds charge
    private static final int ACTIVE_TIME = 100; // 5 seconds active
    private static final int TOTAL_TIME = CHARGE_TIME + ACTIVE_TIME;
    private Vec3 blackholeCenter;

    public MirdaBlackholeAttackGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;

        // Only use when low on health or rarely with high energy
        float healthPercent = mirda.getHealth() / mirda.getMaxHealth();
        boolean lowHealth = healthPercent < 0.3f && mirda.random.nextFloat() < 0.05f;
        boolean randomUse = mirda.getEnergy() > 10.0f && mirda.random.nextFloat() < 0.01f;

        return mirda.getTarget() != null && (lowHealth || randomUse);
    }

    @Override
    public void start() {
        blackholeTimer = 0;
        blackholeCenter = mirda.position().add(0, mirda.getBbHeight() / 2, 0);
        mirda.getNavigation().stop();
        mirda.speak("Witness the void! All shall be consumed!");
    }

    @Override
    public void tick() {
        blackholeTimer++;

        if (blackholeTimer <= CHARGE_TIME) {
            // Charging phase - particle effects and warning
            chargeBlackhole();
        } else {
            // Active phase - pull and damage
            activateBlackhole();
        }
    }

    private void chargeBlackhole() {
        // Particle effects for charging
        if (mirda.level().isClientSide && mirda.random.nextFloat() < 0.5f) {
            for (int i = 0; i < 10; i++) {
                double angle = mirda.random.nextDouble() * Math.PI * 2;
                double radius = 5.0 + mirda.random.nextDouble() * 5.0;
                double x = blackholeCenter.x + Math.cos(angle) * radius;
                double y = blackholeCenter.y + (mirda.random.nextDouble() - 0.5) * 2;
                double z = blackholeCenter.z + Math.sin(angle) * radius;

                // Particles moving towards center
                double dx = (blackholeCenter.x - x) * 0.1;
                double dy = (blackholeCenter.y - y) * 0.1;
                double dz = (blackholeCenter.z - z) * 0.1;

                mirda.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.PORTAL,
                    x, y, z,
                    dx, dy, dz
                );
            }
        }
    }

    private void activateBlackhole() {
        // Pull all nearby entities
        double pullRadius = 32.0; // Large radius
        List<LivingEntity> entities = mirda.level().getEntitiesOfClass(
            LivingEntity.class,
            mirda.getBoundingBox().inflate(pullRadius),
            e -> e != mirda && !e.isAlliedTo(mirda)
        );

        int entitiesAffected = 0;
        float totalEnergyGained = 0;

        for (LivingEntity entity : entities) {
            Vec3 entityPos = entity.position().add(0, entity.getBbHeight() / 2, 0);
            Vec3 toCenter = blackholeCenter.subtract(entityPos);
            double distance = toCenter.length();

            if (distance > 0.1) {
                // Pull force increases as they get closer
                double pullStrength = Math.min(1.0, (pullRadius - distance) / pullRadius);
                Vec3 pullVector = toCenter.normalize().scale(pullStrength * 0.5);

                entity.setDeltaMovement(entity.getDeltaMovement().add(pullVector));
                entity.hurtMarked = true;

                // Damage entities close to center
                if (distance < 3.0) {
                    float damage = 20.0f * (float)(1.0 - distance / 3.0);
                    entity.hurt(mirda.damageSources().magic(), damage);

                    // Gain energy from damaged entities
                    totalEnergyGained += damage / 10.0f;
                    entitiesAffected++;

                    // Crush and consume if very close
                    if (distance < 1.0) {
                        float soulEnergy = entity.getMaxHealth() / 10.0f;
                        mirda.addEnergy(soulEnergy);
                        mirda.heal(entity.getHealth() * 0.5f);

                        // Consume the entity
                        entity.hurt(mirda.damageSources().magic(), 1000.0f);
                    }
                }
            }

            // Particle trail
            if (mirda.level().isClientSide && mirda.random.nextFloat() < 0.3f) {
                mirda.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SOUL,
                    entityPos.x, entityPos.y, entityPos.z,
                    toCenter.x * 0.1, toCenter.y * 0.1, toCenter.z * 0.1
                );
            }
        }

        // Restore Mirda's health based on energy gained
        if (entitiesAffected > 0 && blackholeTimer % 10 == 0) {
            mirda.addEnergy(totalEnergyGained);
            mirda.heal(totalEnergyGained * 2);
        }

        // Massive particle effects at center
        if (mirda.level().isClientSide) {
            for (int i = 0; i < 20; i++) {
                double angle = mirda.random.nextDouble() * Math.PI * 2;
                double speed = mirda.random.nextDouble() * 0.5;
                double dx = Math.cos(angle) * speed;
                double dz = Math.sin(angle) * speed;

                mirda.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,
                    blackholeCenter.x, blackholeCenter.y, blackholeCenter.z,
                    dx, 0, dz
                );
            }
        }
    }

    @Override
    public void stop() {
        blackholeTimer = 0;
        mirda.speak("The void hungers for more...");
    }

    @Override
    public boolean canContinueToUse() {
        return blackholeTimer < TOTAL_TIME;
    }
}
