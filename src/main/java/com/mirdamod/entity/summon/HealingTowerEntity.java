package com.mirdamod.entity.summon;

import com.mirdamod.entity.boss.MirdaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

/**
 * Gold/Diamond tower that heals Mirda faster
 * Zombies guard the tower
 * Players must destroy the tower to stop healing
 */
public class HealingTowerEntity extends LivingEntity {
    private static final EntityDataAccessor<Float> HEALING_POWER =
        SynchedEntityData.defineId(HealingTowerEntity.class, EntityDataSerializers.FLOAT);

    private MirdaEntity mirda;
    private UUID mirdaUUID;
    private int healingTick = 0;
    private BlockPos towerBase;
    private boolean towerBuilt = false;

    public HealingTowerEntity(EntityType<? extends HealingTowerEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 200.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEALING_POWER, 1.0f);
    }

    public void setMirda(MirdaEntity mirda) {
        this.mirda = mirda;
        this.mirdaUUID = mirda.getUUID();
    }

    @Override
    public void tick() {
        super.tick();

        // Build tower structure on first tick
        if (!towerBuilt && !level().isClientSide) {
            buildTower();
            towerBuilt = true;
        }

        // Find Mirda if not set
        if (mirda == null && mirdaUUID != null && !level().isClientSide) {
            Entity entity = ((net.minecraft.server.level.ServerLevel) level()).getEntity(mirdaUUID);
            if (entity instanceof MirdaEntity mirdaEntity) {
                mirda = mirdaEntity;
            }
        }

        // Heal Mirda
        if (!level().isClientSide && mirda != null && mirda.isAlive()) {
            healingTick++;

            if (healingTick >= 40) { // Every 2 seconds
                float healAmount = 25.0f * getHealingPower();
                mirda.heal(healAmount);

                // Particle effect
                spawnHealingParticles();

                healingTick = 0;
            }
        }

        // Healing particles continuously
        if (level().isClientSide && tickCount % 10 == 0) {
            spawnHealingParticles();
        }
    }

    private void buildTower() {
        towerBase = this.blockPosition();

        // Build a small gold/diamond tower
        for (int y = 0; y < 3; y++) {
            BlockPos pos = towerBase.above(y);

            if (y == 0) {
                // Base - 3x3 gold blocks
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        level().setBlock(pos.offset(x, 0, z), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                    }
                }
            } else if (y == 1) {
                // Middle - diamond blocks
                level().setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                level().setBlock(pos.north(), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                level().setBlock(pos.south(), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                level().setBlock(pos.east(), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                level().setBlock(pos.west(), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
            } else {
                // Top - single diamond block
                level().setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
            }
        }

        // Spawn guardian zombies
        spawnGuardians();
    }

    private void spawnGuardians() {
        if (level().isClientSide) return;

        for (int i = 0; i < 2; i++) {
            net.minecraft.world.entity.monster.Zombie zombie = EntityType.ZOMBIE.create(level());
            if (zombie != null) {
                double angle = (Math.PI * 2 * i) / 2.0;
                double x = towerBase.getX() + Math.cos(angle) * 3.0;
                double z = towerBase.getZ() + Math.sin(angle) * 3.0;

                zombie.moveTo(x, towerBase.getY(), z, 0, 0);
                level().addFreshEntity(zombie);
            }
        }
    }

    private void spawnHealingParticles() {
        if (mirda != null) {
            // Beam from tower to Mirda
            double dx = mirda.getX() - this.getX();
            double dy = mirda.getY() + mirda.getBbHeight() / 2 - this.getY();
            double dz = mirda.getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0) {
                int particleCount = (int) Math.min(distance, 20);
                for (int i = 0; i < particleCount; i++) {
                    double ratio = i / (double) particleCount;
                    double x = this.getX() + dx * ratio;
                    double y = this.getY() + 1.5 + dy * ratio;
                    double z = this.getZ() + dz * ratio;

                    level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                        x, y, z,
                        0, 0.1, 0
                    );
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        if (result && !level().isClientSide) {
            // Reduce healing power as tower takes damage
            float healthPercent = this.getHealth() / this.getMaxHealth();
            setHealingPower(healthPercent);
        }

        return result;
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();

        if (!level().isClientSide && deathTime == 1) {
            // Destroy tower blocks
            if (towerBase != null) {
                for (int y = 0; y < 3; y++) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos pos = towerBase.offset(x, y, z);
                            if (level().getBlockState(pos).is(Blocks.GOLD_BLOCK) ||
                                level().getBlockState(pos).is(Blocks.DIAMOND_BLOCK)) {
                                level().destroyBlock(pos, true);
                            }
                        }
                    }
                }
            }
        }
    }

    public float getHealingPower() {
        return this.entityData.get(HEALING_POWER);
    }

    public void setHealingPower(float power) {
        this.entityData.set(HEALING_POWER, power);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("HealingPower", getHealingPower());
        if (mirdaUUID != null) {
            tag.putUUID("MirdaUUID", mirdaUUID);
        }
        if (towerBase != null) {
            tag.putLong("TowerBase", towerBase.asLong());
        }
        tag.putBoolean("TowerBuilt", towerBuilt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHealingPower(tag.getFloat("HealingPower"));
        if (tag.hasUUID("MirdaUUID")) {
            mirdaUUID = tag.getUUID("MirdaUUID");
        }
        if (tag.contains("TowerBase")) {
            towerBase = BlockPos.of(tag.getLong("TowerBase"));
        }
        towerBuilt = tag.getBoolean("TowerBuilt");
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }
}
