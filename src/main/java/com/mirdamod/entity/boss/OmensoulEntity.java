package com.mirdamod.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Omensoul - A spirit that orbits Mirda
 * Created when Mirda sits, becomes its own boss fight
 * Upon defeat, curses nearby entities and pulls in a mob to possess
 */
public class OmensoulEntity extends Monster {
    private static final EntityDataAccessor<Boolean> IS_POSSESSED =
        SynchedEntityData.defineId(OmensoulEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> POSSESSED_MOB_TYPE =
        SynchedEntityData.defineId(OmensoulEntity.class, EntityDataSerializers.STRING);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
        Component.literal("Omensoul"),
        BossEvent.BossBarColor.PURPLE,
        BossEvent.BossBarOverlay.PROGRESS
    );

    private MirdaEntity mirda;
    private UUID mirdaUUID;
    private int orbitAngle = 0;
    private boolean isOrbiting = true;
    private LivingEntity possessedMob = null;

    public OmensoulEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 300.0D)
            .add(Attributes.ATTACK_DAMAGE, 15.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
            .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_POSSESSED, false);
        this.entityData.define(POSSESSED_MOB_TYPE, "");
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void setMirda(MirdaEntity mirda) {
        this.mirda = mirda;
        this.mirdaUUID = mirda.getUUID();
    }

    public void startBossFight() {
        isOrbiting = false;
        this.setNoGravity(false);
        this.bossEvent.setName(Component.literal("Omensoul"));
    }

    @Override
    public void tick() {
        super.tick();

        // Find Mirda if not set
        if (mirda == null && mirdaUUID != null && !level().isClientSide) {
            Entity entity = ((net.minecraft.server.level.ServerLevel) level()).getEntity(mirdaUUID);
            if (entity instanceof MirdaEntity mirdaEntity) {
                mirda = mirdaEntity;
            }
        }

        // Orbit Mirda if still orbiting
        if (isOrbiting && mirda != null && mirda.isAlive()) {
            orbitMirda();
        }

        // Update boss bar
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        // Possessed form gets stats from possessed mob
        if (isPossessed() && possessedMob != null && possessedMob.isAlive()) {
            // Copy some attributes
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(possessedMob.getMaxHealth() * 1.5);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                possessedMob.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() + 10.0
            );
        }

        // Dark particles
        if (level().isClientSide && tickCount % 5 == 0) {
            spawnDarkParticles();
        }
    }

    private void orbitMirda() {
        if (mirda == null) return;

        orbitAngle += 5; // Degrees per tick
        if (orbitAngle >= 360) orbitAngle = 0;

        double radius = 3.0;
        double angleRad = Math.toRadians(orbitAngle);

        double x = mirda.getX() + Math.cos(angleRad) * radius;
        double y = mirda.getY() + mirda.getBbHeight() * 0.7;
        double z = mirda.getZ() + Math.sin(angleRad) * radius;

        this.setPos(x, y, z);
        this.setYRot((float) orbitAngle);
    }

    private void spawnDarkParticles() {
        for (int i = 0; i < 3; i++) {
            double x = this.getX() + (random.nextDouble() - 0.5) * this.getBbWidth();
            double y = this.getY() + random.nextDouble() * this.getBbHeight();
            double z = this.getZ() + (random.nextDouble() - 0.5) * this.getBbWidth();

            level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SOUL,
                x, y, z,
                0, 0, 0
            );

            level().addParticle(
                net.minecraft.core.particles.ParticleTypes.SMOKE,
                x, y, z,
                (random.nextDouble() - 0.5) * 0.1,
                0.05,
                (random.nextDouble() - 0.5) * 0.1
            );
        }
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();

        if (!level().isClientSide && deathTime == 1) {
            applyCurse();

            if (!isPossessed()) {
                // First death - pull in a mob and possess it
                pullAndPossessMob();
            } else {
                // Second death - truly defeated, break and fade
                breakAndFade();
            }
        }
    }

    private void applyCurse() {
        // Apply Bad Omen to nearby players
        List<Player> nearbyPlayers = level().getEntitiesOfClass(
            Player.class,
            this.getBoundingBox().inflate(16.0)
        );

        for (Player player : nearbyPlayers) {
            player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 6000, 0));
            player.sendSystemMessage(Component.literal("<Omensoul> You have been cursed!"));
        }
    }

    private void pullAndPossessMob() {
        // Find nearby mob to possess
        List<LivingEntity> nearbyMobs = level().getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(16.0),
            e -> e != this && !(e instanceof OmensoulEntity) && !(e instanceof MirdaEntity) && e.isAlive()
        );

        if (!nearbyMobs.isEmpty()) {
            LivingEntity targetMob = nearbyMobs.get(random.nextInt(nearbyMobs.size()));

            // Pull mob to Omensoul
            Vec3 pullVector = this.position().subtract(targetMob.position()).normalize().scale(2.0);
            targetMob.setDeltaMovement(pullVector);

            // Possess the mob
            possessMob(targetMob);
        }
    }

    private void possessMob(LivingEntity mob) {
        setPossessed(true);
        possessedMob = mob;
        setPossessedMobType(mob.getType().getDescriptionId());

        // Remove the original mob
        mob.discard();

        // Respawn Omensoul with new form
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
        this.dead = false;

        // Update boss bar name
        this.bossEvent.setName(Component.literal("Omensoul " + getPossessedMobType()));
        this.bossEvent.setColor(BossEvent.BossBarColor.RED);

        // Gain strength
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(25.0);
    }

    private void breakAndFade() {
        // Particle explosion
        if (!level().isClientSide) {
            for (int i = 0; i < 50; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double speed = random.nextDouble() * 0.5;
                double dx = Math.cos(angle) * speed;
                double dz = Math.sin(angle) * speed;

                level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.SOUL,
                    this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                    dx, 0.3, dz
                );
            }
        }
    }

    public boolean isPossessed() {
        return this.entityData.get(IS_POSSESSED);
    }

    public void setPossessed(boolean possessed) {
        this.entityData.set(IS_POSSESSED, possessed);
    }

    public String getPossessedMobType() {
        return this.entityData.get(POSSESSED_MOB_TYPE);
    }

    public void setPossessedMobType(String type) {
        this.entityData.set(POSSESSED_MOB_TYPE, type);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Possessed", isPossessed());
        tag.putString("PossessedMobType", getPossessedMobType());
        tag.putBoolean("Orbiting", isOrbiting);
        tag.putInt("OrbitAngle", orbitAngle);

        if (mirdaUUID != null) {
            tag.putUUID("MirdaUUID", mirdaUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setPossessed(tag.getBoolean("Possessed"));
        setPossessedMobType(tag.getString("PossessedMobType"));
        isOrbiting = tag.getBoolean("Orbiting");
        orbitAngle = tag.getInt("OrbitAngle");

        if (tag.hasUUID("MirdaUUID")) {
            mirdaUUID = tag.getUUID("MirdaUUID");
        }

        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (!isOrbiting) { // Only show boss bar during fight
            this.bossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VEX_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }
}
