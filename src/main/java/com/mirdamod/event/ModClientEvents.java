package com.mirdamod.event;

import com.mirdamod.MirdaMod;
import com.mirdamod.entity.client.model.MirdaModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Client-side only events (model layer registration, etc.)
 */
@Mod.EventBusSubscriber(modid = MirdaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MirdaModel.LAYER_LOCATION, MirdaModel::createBodyLayer);
        MirdaMod.LOGGER.info("Registered Mirda model layer!");
    }
}
