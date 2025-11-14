package com.mirdamod.entity;

import com.mirdamod.MirdaMod;
import com.mirdamod.entity.boss.MirdaEntity;
import com.mirdamod.entity.boss.OmensoulEntity;
import com.mirdamod.entity.projectile.ExplodingBeamEntity;
import com.mirdamod.entity.projectile.LightningBeamEntity;
import com.mirdamod.entity.summon.AdvancedSkeletonEntity;
import com.mirdamod.entity.summon.HealingTowerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, MirdaMod.MODID);

    // Main boss entity
    public static final DeferredHolder<EntityType<?>, EntityType<MirdaEntity>> MIRDA =
        ENTITY_TYPES.register("mirda", () -> EntityType.Builder.of(MirdaEntity::new, MobCategory.MONSTER)
            .sized(3.6f, 10.8f) // Giant-sized (3x the size of a normal player)
            .fireImmune()
            .canSpawnFarFromPlayer()
            .clientTrackingRange(128)
            .build("mirda"));

    // Omensoul entity - spawned during Mirda's sitting phase
    public static final DeferredHolder<EntityType<?>, EntityType<OmensoulEntity>> OMENSOUL =
        ENTITY_TYPES.register("omensoul", () -> EntityType.Builder.of(OmensoulEntity::new, MobCategory.MONSTER)
            .sized(1.5f, 1.5f)
            .fireImmune()
            .clientTrackingRange(64)
            .build("omensoul"));

    // Advanced skeleton summons
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedSkeletonEntity>> ADVANCED_SKELETON =
        ENTITY_TYPES.register("advanced_skeleton", () -> EntityType.Builder.of(AdvancedSkeletonEntity::new, MobCategory.MONSTER)
            .sized(0.6f, 1.99f)
            .clientTrackingRange(64)
            .build("advanced_skeleton"));

    // Healing tower entity
    public static final DeferredHolder<EntityType<?>, EntityType<HealingTowerEntity>> HEALING_TOWER =
        ENTITY_TYPES.register("healing_tower", () -> EntityType.Builder.of(HealingTowerEntity::new, MobCategory.MISC)
            .sized(1.0f, 3.0f)
            .fireImmune()
            .clientTrackingRange(64)
            .build("healing_tower"));

    // Projectiles
    public static final DeferredHolder<EntityType<?>, EntityType<ExplodingBeamEntity>> EXPLODING_BEAM =
        ENTITY_TYPES.register("exploding_beam", () -> EntityType.Builder.<ExplodingBeamEntity>of(ExplodingBeamEntity::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build("exploding_beam"));

    public static final DeferredHolder<EntityType<?>, EntityType<LightningBeamEntity>> LIGHTNING_BEAM =
        ENTITY_TYPES.register("lightning_beam", () -> EntityType.Builder.<LightningBeamEntity>of(LightningBeamEntity::new, MobCategory.MISC)
            .sized(0.3f, 0.3f)
            .clientTrackingRange(64)
            .updateInterval(1)
            .build("lightning_beam"));
}
