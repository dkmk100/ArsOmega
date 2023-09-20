package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.models.ColoredItemModel;
import com.dkmk100.arsomega.client.models.GeckolibEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoBone;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.ars_nouveau.geckolib3.util.RenderUtils;

import java.util.Locale;

public class GeckolibEntityRenderer extends GeoEntityRenderer {

    public GeckolibEntityRenderer(EntityRendererProvider.Context renderManager, String name) {
        this(renderManager, new GeckolibEntityModel(name));
    }
    public GeckolibEntityRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String boneNameLower = bone.getName().toLowerCase(Locale.ROOT);
        ItemStack itemstack = null;
        if(boneNameLower.equals("rightitem")) {
            itemstack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if(boneNameLower.equals("leftitem")) {
            itemstack = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
        }
        if(boneNameLower.equals("helmet")) {
            itemstack = animatable.getItemBySlot(EquipmentSlot.HEAD);
        }

        if(itemstack != null){
            poseStack.pushPose();
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);

            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, this.rtb, (int)animatable.blockPosition().asLong());

            poseStack.popPose();
            buffer = getCurrentRTB().getBuffer(RenderType.entityCutout(whTexture));
        }
        super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }
}
