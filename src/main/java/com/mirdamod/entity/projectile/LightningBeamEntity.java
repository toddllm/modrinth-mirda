package com.mirdamod.entity.projectile;

import com.mirdamod.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Lightning beam projectile from Mirda's hands
 * Strikes lightning on impact
 */
public class LightningBeamEntity extends ThrowableProjectile {
    private static final float DAMAGE = 15.0f;

    public LightningBeamEntity(EntityType<? extends LightningBeamEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LightningBeamEntity(Level level, LivingEntity shooter) {
        super(ModEntities.LIGHTNING_BEAM.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        // No additional data needed
    }

    @Override
    public void tick() {
        super.tick();

        // Electric particle trail
        if (level().isClientSide) {
            Vec3 motion = this.getDeltaMovement();
            for (int i = 0; i < 2; i++) {
                level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                    this.getX() - motion.x * i * 0.25,
                    this.getY() - motion.y * i * 0.25,
                    this.getZ() - motion.z * i * 0.25,
                    (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.1
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
            strikeLightning();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (!level().isClientSide) {
            strikeLightning();
        }
    }

    private void strikeLightning() {
        if (!level().isClientSide) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level());
            if (lightning != null) {
                lightning.moveTo(this.getX(), this.getY(), this.getZ());
                level().addFreshEntity(lightning);
            }

            this.discard();
        }
    }
}
