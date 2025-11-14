package com.mirdamod.entity.ai.goal;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;

/**
 * Mirda's melee attack
 * Gets stronger with each hit (combo system)
 * Can grab players and apply Wither/Poison effects
 */
public class MirdaMeleeAttackGoal extends MeleeAttackGoal {
    private final MirdaEntity mirda;
    private int grabCooldown = 0;

    public MirdaMeleeAttackGoal(MirdaEntity mirda, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mirda, speedModifier, followingTargetEvenIfNotSeen);
        this.mirda = mirda;
    }

    @Override
    public boolean canUse() {
        if (mirda.isSitting()) return false;
        if (mirda.isCheckmated()) return false; // In checkmated mode, can't melee
        return super.canUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distanceSquared) {
        double reachDistance = this.getAttackReachSqr(target);

        if (distanceSquared <= reachDistance && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            performMeleeAttack(target);
        }
    }

    private void performMeleeAttack(LivingEntity target) {
        mirda.swing(InteractionHand.MAIN_HAND);

        // Normal attack
        mirda.doHurtTarget(target);

        // Increment combo
        mirda.incrementMeleeCombo();

        // Chance to grab player
        if (target instanceof Player player && grabCooldown <= 0 && mirda.random.nextFloat() < 0.3f) {
            grabPlayer(player);
            grabCooldown = 100; // 5 second cooldown
        }

        // Use Bocow's eruption ability if equipped
        if (mirda.hasBocow() && mirda.random.nextFloat() < 0.2f) {
            useBocowEruption();
        }
    }

    private void grabPlayer(Player player) {
        // Pull player close
        double dx = mirda.getX() - player.getX();
        double dy = mirda.getY() - player.getY();
        double dz = mirda.getZ() - player.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist > 0) {
            player.push(dx / dist * 0.5, 0.3, dz / dist * 0.5);
        }

        // Apply effects
        if (mirda.random.nextBoolean()) {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2));
        }

        // Extra damage
        player.hurt(mirda.damageSources().mobAttack(mirda), 15.0f);

        mirda.speak("You cannot escape my grasp!");
    }

    private void useBocowEruption() {
        if (mirda.level().isClientSide) return;

        // Create eruption effect around Mirda
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 * i) / 8.0;
            double radius = 3.0;
            double x = mirda.getX() + Math.cos(angle) * radius;
            double z = mirda.getZ() + Math.sin(angle) * radius;

            // Spawn explosion effect
            mirda.level().explode(
                mirda,
                x, mirda.getY(), z,
                2.0f,
                false,
                net.minecraft.world.level.Level.ExplosionInteraction.NONE
            );
        }

        mirda.speak("The Bocow's power is unleashed!");
    }

    @Override
    public void tick() {
        super.tick();
        if (grabCooldown > 0) {
            grabCooldown--;
        }
    }
}
