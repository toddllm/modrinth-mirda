package com.mirdamod.entity.client;

import com.mirdamod.entity.summon.HealingTowerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Simple renderer for the healing tower
 * The actual tower structure is built with blocks, this just renders a core entity
 */
public class HealingTowerRenderer extends EntityRenderer<HealingTowerEntity> {
    public HealingTowerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(HealingTowerEntity entity) {
        return new ResourceLocation("minecraft", "textures/block/diamond_block.png");
    }

    @Override
    public void render(HealingTowerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // The tower is built from blocks, so we don't need to render much here
        // Just render a small glowing core
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
