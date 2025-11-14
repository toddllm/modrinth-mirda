package com.mirdamod.entity.summon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Advanced mini-bone skeleton with enhanced powers and abilities
 * Summoned by Mirda to aid in battle
 */
public class AdvancedSkeletonEntity extends AbstractSkeleton {

    public AdvancedSkeletonEntity(EntityType<? extends AbstractSkeleton> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D) // More health than normal skeleton
            .add(Attributes.ATTACK_DAMAGE, 8.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D) // Faster than normal
            .add(Attributes.ARMOR, 10.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        // Advanced powers - periodic buffs
        if (!level().isClientSide && this.tickCount % 100 == 0) {
            // Self buff
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));

            // Regeneration
            if (this.getHealth() < this.getMaxHealth()) {
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 2));
            }
        }

        // Teleport ability when hit
        if (this.hurtTime > 0 && this.random.nextFloat() < 0.1f) {
            teleportRandomly();
        }
    }

    private void teleportRandomly() {
        if (!level().isClientSide) {
            double x = this.getX() + (this.random.nextDouble() - 0.5) * 16.0;
            double y = this.getY() + (this.random.nextInt(16) - 8);
            double z = this.getZ() + (this.random.nextDouble() - 0.5) * 16.0;

            this.teleportTo(x, y, z);
        }
    }

    @Override
    protected boolean isSunBurnTick() {
        return false; // Advanced skeletons don't burn in sun
    }
}
