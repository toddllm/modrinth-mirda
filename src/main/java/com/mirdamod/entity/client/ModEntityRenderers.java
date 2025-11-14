package com.mirdamod.entity.client;

import com.mirdamod.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.GiantMobRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;

/**
 * Registers entity renderers for client-side rendering
 */
public class ModEntityRenderers {
    public static void register() {
        // Mirda uses giant mob renderer as a base (will need custom model later)
        EntityRenderers.register(ModEntities.MIRDA.get(), GiantMobRenderer::new);

        // Omensoul uses vex renderer as a base (spirit-like)
        EntityRenderers.register(ModEntities.OMENSOUL.get(), VexRenderer::new);

        // Advanced skeleton uses skeleton renderer
        EntityRenderers.register(ModEntities.ADVANCED_SKELETON.get(), SkeletonRenderer::new);

        // Healing tower - custom renderer would be ideal, but we'll use a simple one for now
        EntityRenderers.register(ModEntities.HEALING_TOWER.get(), context -> new HealingTowerRenderer(context));

        // Projectiles use thrown item renderer
        EntityRenderers.register(ModEntities.EXPLODING_BEAM.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.LIGHTNING_BEAM.get(), ThrownItemRenderer::new);
    }
}
