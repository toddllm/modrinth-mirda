package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.EvokerFangs;

import java.util.EnumSet;

/**
 * Goal for summoning evoker fangs
 * Represents spikes/bones rising from the ground
 * Can create patterns and follow players
 */
public class MirdaEvokerFangGoal extends Goal {
    private final MirdaEntity mirda;
    private int attackTimer = 0;
    private static final int ATTACK_INTERVAL = 50; // 2.5 seconds

    public MirdaEvokerFangGoal(MirdaEntity mirda) {
        this.mirda = mirda;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.getAttackCooldown() > 0) return false;

        return mirda.getTarget() != null && mirda.random.nextFloat() < 0.25f;
    }

    @Override
    public void start() {
        attackTimer = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = mirda.getTarget();
        if (target == null) return;

        attackTimer++;

        if (attackTimer >= ATTACK_INTERVAL) {
            summonEvokerFangs(target);
            attackTimer = 0;
        }
    }

    private void summonEvokerFangs(LivingEntity target) {
        if (mirda.level().isClientSide) return;

        // Create a line of fangs towards the target
        BlockPos mirdaPos = mirda.blockPosition();
        BlockPos targetPos = target.blockPosition();

        double dx = targetPos.getX() - mirdaPos.getX();
        double dz = targetPos.getZ() - mirdaPos.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0) {
            dx /= distance;
            dz /= distance;

            // Create fangs in a line
            int fangCount = Math.min(16, (int) distance);
            for (int i = 1; i <= fangCount; i++) {
                double x = mirdaPos.getX() + dx * i;
                double z = mirdaPos.getZ() + dz * i;
                BlockPos fangPos = BlockPos.containing(x, mirdaPos.getY(), z);

                // Find ground level
                while (mirda.level().isEmptyBlock(fangPos) && fangPos.getY() > mirda.level().getMinBuildHeight()) {
                    fangPos = fangPos.below();
                }
                fangPos = fangPos.above();

                spawnFang(fangPos, i * 2); // Delay based on distance
            }

            // Circle pattern around target for advanced phases
            if (mirda.getPhase().getId() >= 1) {
                createCirclePattern(targetPos);
            }
        }

        mirda.setAttackCooldown(25);
    }

    private void createCirclePattern(BlockPos center) {
        int radius = 3;
        int points = 8;

        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2 * i) / points;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            BlockPos fangPos = BlockPos.containing(x, center.getY(), z);

            // Find ground
            while (mirda.level().isEmptyBlock(fangPos) && fangPos.getY() > mirda.level().getMinBuildHeight()) {
                fangPos = fangPos.below();
            }
            fangPos = fangPos.above();

            spawnFang(fangPos, 10 + i * 2);
        }
    }

    private void spawnFang(BlockPos pos, int delay) {
        EvokerFangs fangs = EntityType.EVOKER_FANGS.create(mirda.level());
        if (fangs != null) {
            fangs.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            fangs.setOwner(mirda);
            mirda.level().addFreshEntity(fangs);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return mirda.getTarget() != null && attackTimer < ATTACK_INTERVAL * 2;
    }
}
