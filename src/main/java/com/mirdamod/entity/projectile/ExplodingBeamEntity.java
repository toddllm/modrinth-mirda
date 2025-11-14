package com.mirdamod.entity.projectile;

import com.mirdamod.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Exploding beam projectile fired from Mirda's hands
 * Explodes on impact dealing area damage
 */
public class ExplodingBeamEntity extends ThrowableProjectile {
    private static final float DAMAGE = 20.0f;
    private static final float EXPLOSION_POWER = 2.0f;

    public ExplodingBeamEntity(EntityType<? extends ExplodingBeamEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ExplodingBeamEntity(Level level, LivingEntity shooter) {
        super(ModEntities.EXPLODING_BEAM.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        // No additional data needed
    }

    @Override
    public void tick() {
        super.tick();

        // Particle trail
        if (level().isClientSide) {
            Vec3 motion = this.getDeltaMovement();
            for (int i = 0; i < 3; i++) {
                level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.FLAME,
                    this.getX() - motion.x * i * 0.25,
                    this.getY() - motion.y * i * 0.25,
                    this.getZ() - motion.z * i * 0.25,
                    0, 0, 0
                );
                level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX() - motion.x * i * 0.25,
                    this.getY() - motion.y * i * 0.25,
                    this.getZ() - motion.z * i * 0.25,
                    0, 0, 0
                );
            }
        }

        // Remove after 10 seconds
        if (this.tickCount > 200) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            target.hurt(this.damageSources().magic(), DAMAGE);
            explode();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (!level().isClientSide) {
            explode();
        }
    }

    private void explode() {
        if (!level().isClientSide) {
            level().explode(
                this.getOwner(),
                this.getX(),
                this.getY(),
                this.getZ(),
                EXPLOSION_POWER,
                false,
                Level.ExplosionInteraction.NONE
            );

            // Particle effects
            for (int i = 0; i < 20; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double speed = random.nextDouble() * 0.5;
                double dx = Math.cos(angle) * speed;
                double dz = Math.sin(angle) * speed;

                level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                    this.getX(), this.getY(), this.getZ(),
                    dx, 0.2, dz
                );
            }

            this.discard();
        }
    }
}
