package com.mirdamod.event;

import com.mirdamod.MirdaMod;
import com.mirdamod.entity.ModEntities;
import com.mirdamod.entity.boss.MirdaEntity;
import com.mirdamod.entity.boss.OmensoulEntity;
import com.mirdamod.entity.summon.AdvancedSkeletonEntity;
import com.mirdamod.entity.summon.HealingTowerEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;

/**
 * Handles mod events for entity attribute registration and spawn placement
 */
@Mod.EventBusSubscriber(modid = MirdaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        // Register entity attributes
        event.put(ModEntities.MIRDA.get(), MirdaEntity.createAttributes().build());
        event.put(ModEntities.OMENSOUL.get(), OmensoulEntity.createAttributes().build());
        event.put(ModEntities.ADVANCED_SKELETON.get(), AdvancedSkeletonEntity.createAttributes().build());
        event.put(ModEntities.HEALING_TOWER.get(), HealingTowerEntity.createAttributes().build());

        MirdaMod.LOGGER.info("Entity attributes registered!");
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        // Mirda spawns in her altar only (custom spawning)
        event.register(
            ModEntities.MIRDA.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        // Advanced skeletons can spawn normally
        event.register(
            ModEntities.ADVANCED_SKELETON.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        MirdaMod.LOGGER.info("Spawn placements registered!");
    }
}
