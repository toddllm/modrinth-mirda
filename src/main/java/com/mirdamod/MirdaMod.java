package com.mirdamod;

import com.mirdamod.entity.ModEntities;
import com.mirdamod.entity.client.ModEntityRenderers;
import com.mirdamod.item.ModItems;
import com.mirdamod.structure.ModStructures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MirdaMod.MODID)
public class MirdaMod {
    public static final String MODID = "mirdamod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MirdaMod.class);

    public MirdaMod(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // Register deferred registries
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        LOGGER.info("Mirda Boss Mod initialized!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModStructures.registerStructures();
            LOGGER.info("Mirda structures registered!");
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ModEntityRenderers::register);
    }
}
