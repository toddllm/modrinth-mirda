package com.mirdamod.entity.client.renderer;

import com.mirdamod.entity.boss.MirdaEntity;
import com.mirdamod.entity.client.model.MirdaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom renderer for Mirda using the custom model
 */
public class MirdaRenderer extends MobRenderer<MirdaEntity, MirdaModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mirdamod", "textures/entity/mirda.png");
    private static final ResourceLocation GOLDEN_TEXTURE = new ResourceLocation("mirdamod", "textures/entity/mirda_golden.png");
    private static final ResourceLocation ULTRA_TEXTURE = new ResourceLocation("mirdamod", "textures/entity/mirda_ultra.png");

    public MirdaRenderer(EntityRendererProvider.Context context) {
        super(context, new MirdaModel(context.bakeLayer(MirdaModel.LAYER_LOCATION)), 1.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MirdaEntity entity) {
        // Different textures for different phases
        return switch (entity.getPhase()) {
            case GOLDEN, GODDESS_YELLOW_RED -> GOLDEN_TEXTURE;
            case ULTRA, DEMON_YELLOW -> ULTRA_TEXTURE;
            default -> TEXTURE;
        };
    }

    @Override
    public void render(MirdaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Extra scaling for truly giant size
        poseStack.pushPose();

        // Make her even bigger in certain phases
        float scale = 1.0F;
        switch (entity.getPhase()) {
            case ULTRA, FINAL_FORM -> scale = 1.2F;
            case SUPER_SAYTHREN -> scale = 1.4F;
        }
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        poseStack.popPose();
    }

    @Override
    protected void scale(MirdaEntity entity, PoseStack poseStack, float partialTicks) {
        // Additional per-tick scaling effects
        super.scale(entity, poseStack, partialTicks);
    }
}
