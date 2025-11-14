package com.mirdamod.entity.client.model;

import com.mirdamod.entity.boss.MirdaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Custom model for Mirda - A tall goddess-like figure with flowing robes
 * Based on the concept art showing dark robes, golden crown, and chest orb
 */
public class MirdaModel extends EntityModel<MirdaEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(new ResourceLocation("mirdamod", "mirda"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart robe;
    private final ModelPart crown;
    private final ModelPart chestOrb;
    private final ModelPart rightDragonHead;
    private final ModelPart leftDragonHead;

    public MirdaModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.robe = root.getChild("robe");
        this.crown = root.getChild("crown");
        this.chestOrb = root.getChild("chest_orb");
        this.rightDragonHead = root.getChild("right_dragon_head");
        this.leftDragonHead = root.getChild("left_dragon_head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Head - goddess face (enlarged for giant size)
        PartDefinition head = partdefinition.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -24.0F, 0.0F));

        // Crown - golden glowing halo above head
        PartDefinition crown = partdefinition.addOrReplaceChild("crown",
            CubeListBuilder.create()
                .texOffs(48, 0)
                .addBox(-8.0F, -16.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -24.0F, 0.0F));

        // Body - tall torso
        PartDefinition body = partdefinition.addOrReplaceChild("body",
            CubeListBuilder.create()
                .texOffs(0, 24)
                .addBox(-8.0F, 0.0F, -4.0F, 16.0F, 24.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -24.0F, 0.0F));

        // Chest Orb - golden glowing orb
        PartDefinition chestOrb = partdefinition.addOrReplaceChild("chest_orb",
            CubeListBuilder.create()
                .texOffs(96, 0)
                .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.5F)),
            PartPose.offset(0.0F, -12.0F, -5.0F));

        // Right Arm
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm",
            CubeListBuilder.create()
                .texOffs(48, 24)
                .addBox(-4.0F, -2.0F, -3.0F, 4.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-12.0F, -22.0F, 0.0F));

        // Left Arm
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm",
            CubeListBuilder.create()
                .texOffs(68, 24)
                .addBox(0.0F, -2.0F, -3.0F, 4.0F, 24.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(12.0F, -22.0F, 0.0F));

        // Robe - flowing dark robe covering lower body
        PartDefinition robe = partdefinition.addOrReplaceChild("robe",
            CubeListBuilder.create()
                .texOffs(0, 56)
                .addBox(-12.0F, 0.0F, -6.0F, 24.0F, 32.0F, 12.0F, new CubeDeformation(0.5F)),
            PartPose.offset(0.0F, 0.0F, 0.0F));

        // Dragon heads (for when she gains them at half health)
        PartDefinition rightDragonHead = partdefinition.addOrReplaceChild("right_dragon_head",
            CubeListBuilder.create()
                .texOffs(88, 24)
                .addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-14.0F, -20.0F, 0.0F));

        PartDefinition leftDragonHead = partdefinition.addOrReplaceChild("left_dragon_head",
            CubeListBuilder.create()
                .texOffs(88, 24)
                .addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(14.0F, -20.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(MirdaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Head rotation
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.crown.yRot = this.head.yRot;
        this.crown.xRot = this.head.xRot;

        // Crown glowing/rotating effect
        this.crown.yRot += ageInTicks * 0.01F;

        // Chest orb pulsing
        float orbPulse = Mth.sin(ageInTicks * 0.1F) * 0.1F;
        this.chestOrb.xScale = 1.0F + orbPulse;
        this.chestOrb.yScale = 1.0F + orbPulse;
        this.chestOrb.zScale = 1.0F + orbPulse;

        // Arm animations
        if (entity.isSpinning()) {
            // Spinning attack - arms out
            this.rightArm.xRot = 0.0F;
            this.rightArm.zRot = 1.5F;
            this.leftArm.xRot = 0.0F;
            this.leftArm.zRot = -1.5F;
        } else if (entity.isSitting()) {
            // Sitting pose - arms resting
            this.rightArm.xRot = -0.5F;
            this.rightArm.zRot = 0.1F;
            this.leftArm.xRot = -0.5F;
            this.leftArm.zRot = -0.1F;
        } else if (entity.isFlying()) {
            // Flying pose - arms back
            this.rightArm.xRot = 0.5F;
            this.leftArm.xRot = 0.5F;
        } else {
            // Normal walking/attack animations
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        }

        // Robe sway
        this.robe.yRot = Mth.sin(ageInTicks * 0.05F) * 0.05F;

        // Dragon heads - only visible when entity has them
        boolean hasDragonHeads = entity.getDragonHeads() > 0;
        this.rightDragonHead.visible = hasDragonHeads;
        this.leftDragonHead.visible = hasDragonHeads;

        if (hasDragonHeads) {
            // Dragon heads move independently
            this.rightDragonHead.yRot = Mth.sin(ageInTicks * 0.1F) * 0.3F;
            this.leftDragonHead.yRot = -Mth.sin(ageInTicks * 0.1F + 1.0F) * 0.3F;
        }

        // Time ability effect - pose changes
        if (entity.isTimeAbilityActive()) {
            this.rightArm.zRot = 1.0F;
            this.leftArm.zRot = -1.0F;
            this.rightArm.xRot = -1.5F;
            this.leftArm.xRot = -1.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Scale up for giant size (3x normal size)
        poseStack.pushPose();
        poseStack.scale(3.0F, 3.0F, 3.0F);

        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        crown.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 0.9F, 0.0F, alpha); // Golden glow
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        chestOrb.render(poseStack, vertexConsumer, 240, packedOverlay, 1.0F, 0.9F, 0.0F, alpha); // Glowing orb
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        robe.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        if (rightDragonHead.visible) {
            rightDragonHead.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
        if (leftDragonHead.visible) {
            leftDragonHead.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        poseStack.popPose();
    }
}
