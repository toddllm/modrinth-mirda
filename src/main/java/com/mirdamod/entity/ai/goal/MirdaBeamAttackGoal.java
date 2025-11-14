package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import com.mirdamod.entity.boss.MirdaPhase;
import com.mirdamod.entity.projectile.ExplodingBeamEntity;
import com.mirdamod.entity.projectile.LightningBeamEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Goal for Mirda's exploding beam attacks from her hands
 * Golden and Ultra forms fire more beams
 */
public class MirdaBeamAttackGoal extends Goal {
    private final MirdaEntity mirda;
    private int attackTimer = 0;
    private static final int ATTACK_INTERVAL = 40; // 2 seconds

    public MirdaBeamAttackGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.getAttackCooldown() > 0) return false;

        return mirda.getTarget() != null && mirda.random.nextFloat() < 0.15f;
    }

    @Override
    public void start() {
        attackTimer = 0;
        if (mirda.getEnergy() > 1.0f) {
            mirda.speak("Feel my power!");
        }
    }

    @Override
    public void tick() {
        LivingEntity target = mirda.getTarget();
        if (target == null) return;

        mirda.getLookControl().setLookAt(target, 30.0f, 30.0f);
        attackTimer++;

        if (attackTimer == 20) { // Charge up
            // Particle effects would go here
        }

        if (attackTimer >= ATTACK_INTERVAL) {
            fireBeams(target);
            attackTimer = 0;
        }
    }

    private void fireBeams(LivingEntity target) {
        if (mirda.level().isClientSide) return;

        int beamCount = getBeamCount();

        for (int i = 0; i < beamCount; i++) {
            ExplodingBeamEntity beam = new ExplodingBeamEntity(mirda.level(), mirda);

            // Position beam at Mirda's hands
            Vec3 handOffset = new Vec3(
                (i % 2 == 0 ? 1.5 : -1.5) + (i / 2) * 0.5,
                mirda.getBbHeight() * 0.6,
                0
            );
            Vec3 startPos = mirda.position().add(handOffset);
            beam.setPos(startPos);

            // Calculate direction to target with some spread
            Vec3 direction = target.position()
                .add(0, target.getBbHeight() / 2, 0)
                .subtract(startPos)
                .normalize();

            // Add spread for multiple beams
            if (beamCount > 1) {
                double spread = 0.1 * (i - beamCount / 2.0);
                direction = direction.add(spread, 0, spread).normalize();
            }

            beam.shoot(direction.x, direction.y, direction.z, 1.5f, 0);

            mirda.level().addFreshEntity(beam);
        }

        // Use energy
        mirda.addEnergy(-0.5f);
        mirda.setAttackCooldown(30);
    }

    private int getBeamCount() {
        int baseCount = 1;
        MirdaPhase phase = mirda.getPhase();

        // More beams in advanced phases
        if (phase == MirdaPhase.GOLDEN) {
            baseCount = 2;
        } else if (phase == MirdaPhase.ULTRA) {
            baseCount = 3 + mirda.random.nextInt(3); // 3-5 beams
        }

        // Extra hands increase beam count
        baseCount += mirda.getExtraHands();

        return baseCount;
    }

    @Override
    public boolean canContinueToUse() {
        return mirda.getTarget() != null && attackTimer < ATTACK_INTERVAL * 2 && mirda.getEnergy() > 0;
    }
}
