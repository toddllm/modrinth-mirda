package com.mirdamod.entity.boss;

import com.mirdamod.entity.ModEntities;
import com.mirdamod.entity.ai.goal.*;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirda - The Ultimate Boss Entity
 * A goddess-like dragon creature with multiple phases, abilities, and attack patterns
 */
public class MirdaEntity extends Monster {
    // Data trackers for syncing client/server
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ENERGY = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FLYING = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IRON_ARMOR_ACTIVE = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_CYCLE_TICK = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MELEE_COMBO = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TIME_ABILITY_ACTIVE = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DRAGON_HEADS_COUNT = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EXTRA_HANDS_COUNT = SynchedEntityData.defineId(MirdaEntity.class, EntityDataSerializers.INT);

    // Boss bar
    private final ServerBossEvent bossEvent = new ServerBossEvent(
        this.getDisplayName(),
        BossEvent.BossBarColor.PINK,
        BossEvent.BossBarOverlay.PROGRESS
    );

    // Combat tracking
    private int attackCooldown = 0;
    private int phaseCooldown = 0;
    private int healingTowerCount = 0;
    private int summonedMobCount = 0;
    private boolean hasTransformedHalfHealth = false;
    private boolean isInvulnerable = false;
    private int invulnerabilityTicks = 0;
    private int ironBlockArmorHealth = 0;
    private int speechCooldown = 0;
    private int currentAttackIndex = 0;
    private List<Entity> consumedMobs = new ArrayList<>();
    private BlockPos altarPosition = null;
    private boolean hasBocow = false;
    private boolean isCheckmated = false;
    private int dodgeCounter = 0;
    private int spinningTicks = 0;
    private boolean isSpinning = false;

    public MirdaEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5000; // Massive XP reward
        this.setMaxUpStep(2.0f); // Can step up 2 blocks
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 5000.0D) // Starting health - will increase as she consumes mobs
            .add(Attributes.ATTACK_DAMAGE, 50.0D) // Base attack damage
            .add(Attributes.ARMOR, 20.0D) // Diamond/gold armor
            .add(Attributes.ARMOR_TOUGHNESS, 12.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D) // Immune to knockback
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.FOLLOW_RANGE, 128.0D) // Can detect players from very far
            .add(Attributes.FLYING_SPEED, 0.5D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, MirdaPhase.NORMAL.getId());
        this.entityData.define(ENERGY, 1.0f); // Starts with 1 energy
        this.entityData.define(IS_SITTING, false);
        this.entityData.define(IS_FLYING, false);
        this.entityData.define(IRON_ARMOR_ACTIVE, false);
        this.entityData.define(ATTACK_CYCLE_TICK, 0);
        this.entityData.define(MELEE_COMBO, 0);
        this.entityData.define(TIME_ABILITY_ACTIVE, false);
        this.entityData.define(DRAGON_HEADS_COUNT, 0);
        this.entityData.define(EXTRA_HANDS_COUNT, 0);
    }

    @Override
    protected void registerGoals() {
        // Priority 0: Float in water (even though she's immune to drowning)
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Priority 1: Sitting behavior
        this.goalSelector.addGoal(1, new MirdaSitGoal(this));

        // Priority 2: Flying behavior
        this.goalSelector.addGoal(2, new MirdaFlyingGoal(this));

        // Priority 3: Time manipulation ability
        this.goalSelector.addGoal(3, new MirdaTimeAbilityGoal(this));

        // Priority 4: Blackhole attack
        this.goalSelector.addGoal(4, new MirdaBlackholeAttackGoal(this));

        // Priority 5: Lightning attacks
        this.goalSelector.addGoal(5, new MirdaLightningAttackGoal(this));

        // Priority 6: Beam attacks
        this.goalSelector.addGoal(6, new MirdaBeamAttackGoal(this));

        // Priority 7: Evoker fang summoning
        this.goalSelector.addGoal(7, new MirdaEvokerFangGoal(this));

        // Priority 8: Spin attack
        this.goalSelector.addGoal(8, new MirdaSpinAttackGoal(this));

        // Priority 9: Mob consumption
        this.goalSelector.addGoal(9, new MirdaConsumeGoal(this));

        // Priority 10: Summon minions
        this.goalSelector.addGoal(10, new MirdaSummonGoal(this));

        // Priority 11: Iron block armor
        this.goalSelector.addGoal(11, new MirdaIronArmorGoal(this));

        // Priority 12: Melee attack
        this.goalSelector.addGoal(12, new MirdaMeleeAttackGoal(this, 1.2D, false));

        // Priority 13: Look at player
        this.goalSelector.addGoal(13, new LookAtPlayerGoal(this, Player.class, 32.0F));

        // Priority 14: Random looking
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new MirdaTargetOtherMobsGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // Update boss bar
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        // Handle invulnerability
        if (isInvulnerable) {
            invulnerabilityTicks--;
            if (invulnerabilityTicks <= 0) {
                isInvulnerable = false;
            }
        }

        // Check for half-health transformation
        if (!hasTransformedHalfHealth && this.getHealth() <= this.getMaxHealth() / 2) {
            transformToHalfHealthForm();
        }

        // Day/night transformation
        if (!level().isClientSide) {
            long dayTime = level().getDayTime() % 24000;
            boolean isNight = dayTime >= 13000 && dayTime < 23000;

            if (isNight && getPhase() != MirdaPhase.DEMON_YELLOW && getPhase() != MirdaPhase.FINAL_FORM) {
                setPhase(MirdaPhase.DEMON_YELLOW);
                speak("The night empowers me... Nah neh neh!");
            } else if (!isNight && getPhase() == MirdaPhase.DEMON_YELLOW) {
                setPhase(MirdaPhase.GODDESS_YELLOW_RED);
                speak("Daylight reveals my divine form!");
            }
        }

        // Sunlight size change effect
        if (level().canSeeSky(this.blockPosition()) && level().isDay()) {
            handleSunlightExposure();
        }

        // Lava particles when damaged
        if (this.hurtTime > 0 && this.random.nextFloat() < 0.3f) {
            spawnLavaParticles();
        }

        // Handle sitting regeneration
        if (isSitting() && this.tickCount % 20 == 0) {
            this.heal(getMaxHealth() * 0.05f); // 5% per second
        }

        // Cooldown management
        if (attackCooldown > 0) attackCooldown--;
        if (phaseCooldown > 0) phaseCooldown--;
        if (speechCooldown > 0) speechCooldown--;

        // Eye fire effect - set nearby players on fire if they look at her
        if (!level().isClientSide && this.tickCount % 10 == 0) {
            checkEyeContact();
        }

        // Update attack cycle
        incrementAttackCycle();
    }

    /**
     * Transforms Mirda when she reaches half health
     * Becomes immune, regains health, gains extra hands and dragon heads
     */
    private void transformToHalfHealthForm() {
        hasTransformedHalfHealth = true;
        isInvulnerable = true;
        invulnerabilityTicks = 100; // 5 seconds of invulnerability

        speak("You think you can defeat me? I'm just getting started!");

        // Heal back to full
        this.setHealth(this.getMaxHealth());

        // Gain extra hands and dragon heads
        setExtraHands(getExtraHands() + 2);
        setDragonHeads(getDragonHeads() + 2);

        // Lightning strike effect
        if (!level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level());
                if (lightning != null) {
                    lightning.moveTo(this.getX() + random.nextGaussian() * 3, this.getY(), this.getZ() + random.nextGaussian() * 3);
                    level().addFreshEntity(lightning);
                }
            }
        }

        // Increase max health
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() * 1.5);
        this.setHealth(this.getMaxHealth());
    }

    /**
     * Handles size changes when exposed to sunlight
     */
    private void handleSunlightExposure() {
        if (this.tickCount % 40 == 0) { // Every 2 seconds
            // Scale animation between giant and zombie size
            float scale = this.random.nextBoolean() ? 1.0f : 0.3f;
            // Note: Actual scaling would require additional rendering code

            if (speechCooldown <= 0) {
                speak("Exposed to sunlight!");
                speechCooldown = 200;
            }

            // After exposure, use hand lightning instead of sky lightning
            if (!level().isClientSide && random.nextFloat() < 0.1f) {
                useHandLightning();
            }
        }
    }

    /**
     * Spawns lava particles when Mirda takes damage
     */
    private void spawnLavaParticles() {
        if (level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                double x = this.getX() + (random.nextDouble() - 0.5) * this.getBbWidth();
                double y = this.getY() + random.nextDouble() * this.getBbHeight();
                double z = this.getZ() + (random.nextDouble() - 0.5) * this.getBbWidth();
                level().addParticle(net.minecraft.core.particles.ParticleTypes.LAVA, x, y, z, 0, 0, 0);
            }
        }
    }

    /**
     * Checks if players are looking at Mirda's eyes and sets them on fire
     */
    private void checkEyeContact() {
        List<Player> nearbyPlayers = level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16.0));
        for (Player player : nearbyPlayers) {
            Vec3 playerLook = player.getViewVector(1.0f);
            Vec3 toMirda = this.position().subtract(player.position()).normalize();

            double dot = playerLook.dot(toMirda);
            if (dot > 0.95) { // Player is looking at Mirda
                player.setSecondsOnFire(5);
                player.hurt(damageSources().magic(), 4.0f);
            }
        }
    }

    /**
     * Fires lightning from hands instead of sky
     */
    private void useHandLightning() {
        if (this.getTarget() != null) {
            Vec3 targetPos = this.getTarget().position();
            Vec3 handPos = this.position().add(0, this.getBbHeight() * 0.7, 0);

            // Create lightning beam from hand to target
            // This will be implemented in the projectile class
        }
    }

    /**
     * Increases melee combo counter and damage
     */
    public void incrementMeleeCombo() {
        int combo = getMeleeCombo() + 1;
        setMeleeCombo(combo);

        // Increase attack damage based on combo
        double baseDamage = 50.0;
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage + (combo * 5));
    }

    /**
     * Consumes a mob to gain energy and health
     */
    public void consumeMob(LivingEntity mob) {
        if (!level().isClientSide) {
            float energyGain = mob.getMaxHealth() / 20.0f; // Scale energy based on mob health
            addEnergy(energyGain);

            // Gain health
            float healthGain = mob.getMaxHealth() * 0.5f;
            this.setHealth(Math.min(this.getHealth() + healthGain, this.getMaxHealth()));

            // Increase max health
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() + (mob.getMaxHealth() * 0.1));

            // If consumed mob has wither, gain regeneration
            if (mob.hasEffect(MobEffects.WITHER)) {
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
            }

            // Store consumed mob
            consumedMobs.add(mob);

            // Remove the mob
            mob.discard();

            speak("Your power is now mine!");
        }
    }

    /**
     * Makes Mirda speak (sends message to nearby players)
     */
    public void speak(String message) {
        if (!level().isClientSide && speechCooldown <= 0) {
            Component text = Component.literal("<Mirda> " + message);
            List<Player> nearbyPlayers = level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(64.0));
            for (Player player : nearbyPlayers) {
                player.sendSystemMessage(text);
            }
            speechCooldown = 60; // 3 seconds between speeches
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Immunities
        if (source.is(DamageTypes.LAVA) ||
            source.is(DamageTypes.DROWN) ||
            source.is(DamageTypes.LIGHTNING_BOLT) ||
            source.is(DamageTypes.IN_FIRE) ||
            source.is(DamageTypes.ON_FIRE) ||
            source.is(DamageTypes.HOT_FLOOR)) {
            return false;
        }

        // Only player melee can damage (except in certain phases)
        if (!(source.getEntity() instanceof Player)) {
            return false;
        }

        // Check if invulnerable
        if (isInvulnerable || getPhase() == MirdaPhase.FINAL_FORM) {
            return false;
        }

        // Iron block armor absorbs damage
        if (isIronArmorActive()) {
            ironBlockArmorHealth -= amount;
            if (ironBlockArmorHealth <= 0) {
                setIronArmorActive(false);
                speak("My armor has fallen!");
            }
            return false;
        }

        // Checkmated phase - impossible to hit
        if (isCheckmated) {
            dodgeCounter++;
            if (dodgeCounter > 100) { // After many dodges, become ultra form
                setPhase(MirdaPhase.ULTRA);
                isCheckmated = false;
                speak("Enough games! Witness my Ultra Form!");
            }
            return false; // Dodge the attack
        }

        boolean result = super.hurt(source, amount);

        if (result) {
            // Spawn lava when damaged
            spawnLavaBlocks();
        }

        return result;
    }

    /**
     * Spawns lava blocks around Mirda when she takes damage
     */
    private void spawnLavaBlocks() {
        if (!level().isClientSide && random.nextFloat() < 0.3f) {
            BlockPos pos = this.blockPosition().offset(
                random.nextInt(3) - 1,
                0,
                random.nextInt(3) - 1
            );

            BlockState below = level().getBlockState(pos.below());
            if (below.isSolid() && level().getBlockState(pos).isAir()) {
                level().setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
            }
        }
    }

    // Getters and setters for data trackers
    public MirdaPhase getPhase() {
        return MirdaPhase.fromId(this.entityData.get(PHASE));
    }

    public void setPhase(MirdaPhase phase) {
        this.entityData.set(PHASE, phase.getId());
        updateBossBarColor(phase);
    }

    private void updateBossBarColor(MirdaPhase phase) {
        switch (phase) {
            case GOLDEN -> bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
            case ULTRA -> bossEvent.setColor(BossEvent.BossBarColor.RED);
            case DEMON_YELLOW -> bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
            case FINAL_FORM -> bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
            default -> bossEvent.setColor(BossEvent.BossBarColor.PINK);
        }
    }

    public float getEnergy() {
        return this.entityData.get(ENERGY);
    }

    public void setEnergy(float energy) {
        this.entityData.set(ENERGY, energy);
    }

    public void addEnergy(float amount) {
        setEnergy(getEnergy() + amount);
    }

    public boolean isSitting() {
        return this.entityData.get(IS_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(IS_SITTING, sitting);
        if (sitting) {
            this.getNavigation().stop();
        }
    }

    public boolean isFlying() {
        return this.entityData.get(IS_FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(IS_FLYING, flying);
        this.setNoGravity(flying);
    }

    public boolean isIronArmorActive() {
        return this.entityData.get(IRON_ARMOR_ACTIVE);
    }

    public void setIronArmorActive(boolean active) {
        this.entityData.set(IRON_ARMOR_ACTIVE, active);
        if (active) {
            ironBlockArmorHealth = 500; // Takes 500 damage to break
        }
    }

    public int getMeleeCombo() {
        return this.entityData.get(MELEE_COMBO);
    }

    public void setMeleeCombo(int combo) {
        this.entityData.set(MELEE_COMBO, combo);
    }

    public boolean isTimeAbilityActive() {
        return this.entityData.get(TIME_ABILITY_ACTIVE);
    }

    public void setTimeAbilityActive(boolean active) {
        this.entityData.set(TIME_ABILITY_ACTIVE, active);
    }

    public int getDragonHeads() {
        return this.entityData.get(DRAGON_HEADS_COUNT);
    }

    public void setDragonHeads(int count) {
        this.entityData.set(DRAGON_HEADS_COUNT, count);
    }

    public int getExtraHands() {
        return this.entityData.get(EXTRA_HANDS_COUNT);
    }

    public void setExtraHands(int count) {
        this.entityData.set(EXTRA_HANDS_COUNT, count);
    }

    public int getAttackCycle() {
        return this.entityData.get(ATTACK_CYCLE_TICK);
    }

    public void incrementAttackCycle() {
        int cycle = getAttackCycle() + 1;
        this.entityData.set(ATTACK_CYCLE_TICK, cycle);
    }

    public void resetAttackCycle() {
        this.entityData.set(ATTACK_CYCLE_TICK, 0);
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(int ticks) {
        this.attackCooldown = ticks;
    }

    public void setAltarPosition(BlockPos pos) {
        this.altarPosition = pos;
    }

    public BlockPos getAltarPosition() {
        return altarPosition;
    }

    public boolean hasBocow() {
        return hasBocow;
    }

    public void setBocow(boolean has) {
        this.hasBocow = has;
    }

    public boolean isCheckmated() {
        return isCheckmated;
    }

    public void setCheckmated(boolean checkmated) {
        this.isCheckmated = checkmated;
    }

    public void startSpinning() {
        isSpinning = true;
        spinningTicks = 0;
    }

    public void stopSpinning() {
        isSpinning = false;
    }

    public boolean isSpinning() {
        return isSpinning;
    }

    public int getSpinningTicks() {
        return spinningTicks;
    }

    public void incrementSpinningTicks() {
        spinningTicks++;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Phase", getPhase().getId());
        tag.putFloat("Energy", getEnergy());
        tag.putBoolean("Sitting", isSitting());
        tag.putBoolean("Flying", isFlying());
        tag.putBoolean("HasTransformedHalfHealth", hasTransformedHalfHealth);
        tag.putInt("MeleeCombo", getMeleeCombo());
        tag.putInt("DragonHeads", getDragonHeads());
        tag.putInt("ExtraHands", getExtraHands());
        tag.putBoolean("HasBocow", hasBocow);
        tag.putBoolean("IsCheckmated", isCheckmated);

        if (altarPosition != null) {
            tag.putLong("AltarPos", altarPosition.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setPhase(MirdaPhase.fromId(tag.getInt("Phase")));
        setEnergy(tag.getFloat("Energy"));
        setSitting(tag.getBoolean("Sitting"));
        setFlying(tag.getBoolean("Flying"));
        hasTransformedHalfHealth = tag.getBoolean("HasTransformedHalfHealth");
        setMeleeCombo(tag.getInt("MeleeCombo"));
        setDragonHeads(tag.getInt("DragonHeads"));
        setExtraHands(tag.getInt("ExtraHands"));
        hasBocow = tag.getBoolean("HasBocow");
        isCheckmated = tag.getBoolean("IsCheckmated");

        if (tag.contains("AltarPos")) {
            altarPosition = BlockPos.of(tag.getLong("AltarPos"));
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
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDER_DRAGON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDER_DRAGON_DEATH;
    }

    @Override
    public boolean canChangeDimensions() {
        return false; // Mirda cannot be pushed to other dimensions
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
        // Mirda pushes others but cannot be pushed
        if (entity instanceof LivingEntity living && !(entity instanceof MirdaEntity)) {
            entity.push(this);
        }
    }
}
