package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import com.mirdamod.entity.projectile.ExplodingBeamEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Mirda spins while shooting projectiles or lightning
 * Looks very cool and damages nearby entities
 */
public class MirdaSpinAttackGoal extends Goal {
    private final MirdaEntity mirda;
    private int spinTimer = 0;
    private static final int SPIN_DURATION = 100; // 5 seconds

    public MirdaSpinAttackGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.isSpinning()) return true;

        return mirda.getTarget() != null && mirda.random.nextFloat() < 0.08f && mirda.getEnergy() > 2.0f;
    }

    @Override
    public void start() {
        mirda.startSpinning();
        spinTimer = 0;
        mirda.speak("Witness my spinning fury!");
    }

    @Override
    public void stop() {
        mirda.stopSpinning();
        spinTimer = 0;
    }

    @Override
    public void tick() {
        spinTimer++;
        mirda.incrementSpinningTicks();

        // Damage nearby entities
        List<LivingEntity> nearby = mirda.level().getEntitiesOfClass(
            LivingEntity.class,
            mirda.getBoundingBox().inflate(3.0),
            e -> e != mirda && !e.isAlliedTo(mirda)
        );

        for (LivingEntity entity : nearby) {
            entity.hurt(mirda.damageSources().mobAttack(mirda), 15.0f);
            // Knock back
            Vec3 direction = entity.position().subtract(mirda.position()).normalize();
            entity.push(direction.x * 0.5, 0.3, direction.z * 0.5);
        }

        // Shoot projectiles while spinning
        if (spinTimer % 10 == 0) { // Every 0.5 seconds
            shootProjectile();
        }

        // Use energy
        if (spinTimer % 20 == 0) {
            mirda.addEnergy(-0.1f);
        }
    }

    private void shootProjectile() {
        if (mirda.level().isClientSide) return;

        // Shoot in 8 directions
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 * i) / 8.0 + (spinTimer * 0.1); // Rotate pattern

            ExplodingBeamEntity beam = new ExplodingBeamEntity(mirda.level(), mirda);
            Vec3 pos = mirda.position().add(0, mirda.getBbHeight() / 2, 0);
            beam.setPos(pos);

            Vec3 direction = new Vec3(Math.cos(angle), 0, Math.sin(angle));
            beam.shoot(direction.x, 0, direction.z, 1.0f, 0);

            mirda.level().addFreshEntity(beam);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return spinTimer < SPIN_DURATION && mirda.getEnergy() > 0;
    }
}
