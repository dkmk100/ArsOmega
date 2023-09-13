package com.dkmk100.arsomega.client.staff;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.items.ModularStaff;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoBone;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoModel;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.ars_nouveau.geckolib3.util.EModelRenderCycle;
import software.bernie.ars_nouveau.geckolib3.util.RenderUtils;

import java.util.Collections;
import java.util.List;

public class StaffRenderer<T extends ModularStaff> extends GeoItemRenderer<T> {
    StaffModel staffModelProvider;
    public StaffRenderer(StaffModel modelProvider) {
        super(modelProvider);
        staffModelProvider = modelProvider;
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }

    @Override
    public void render(T animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack stack) {
        this.currentItemStack = stack;
        for(ModularStaff.StaffModelPart part : ModularStaff.StaffModelPart.values()) {
            GeoModel model = this.modelProvider.getModel(staffModelProvider.getModel(stack, part));
            Color color = staffModelProvider.getColor(stack,part);
            ResourceLocation texture = staffModelProvider.getTexture(stack, part);

            this.render(stack, animatable, part, poseStack, bufferSource, packedLight, color, model, texture);
        }
    }



    void render(ItemStack stack, T animatable, ModularStaff.StaffModelPart part, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Color renderColor, GeoModel model, ResourceLocation texture){
        List<Object> extraData = StaffAnimationController.getExtraData(stack, animatable, staffModelProvider, part);
        AnimationEvent animationEvent = new AnimationEvent(animatable, 0.0F, 0.0F, Minecraft.getInstance().getFrameTime(), false, extraData);
        this.dispatchedMat = poseStack.last().pose().copy();
        this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        this.modelProvider.setCustomAnimations(animatable, this.getInstanceId(animatable), animationEvent);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5099999904632568, 0.5);
        RenderSystem.setShaderTexture(0, texture);

        currentTexture = texture;
        RenderType renderType = this.getRenderType(animatable, 0.0F, poseStack, bufferSource, (VertexConsumer)null, packedLight, texture);
        this.render(model, animatable, 0.0F, renderType, poseStack, bufferSource, (VertexConsumer)null, packedLight, OverlayTexture.NO_OVERLAY, (float)renderColor.getRed() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
        poseStack.popPose();
    }

    ResourceLocation currentTexture;

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.isTrackingXform()) {
            Matrix4f poseState = poseStack.last().pose().copy();
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);
            bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
            localMatrix.translate(new Vector3f(this.getRenderOffset(this.animatable, 1.0F)));
            bone.setLocalSpaceXform(localMatrix);
        }

        RenderType renderType = this.getRenderType(this.animatable, 0.0F, poseStack, this.rtb, (VertexConsumer)null, packedLight, currentTexture);
        buffer = ItemRenderer.getFoilBufferDirect(this.rtb, renderType, false, this.currentItemStack != null && this.currentItemStack.hasFoil());

        //from IGeoRenderer
        poseStack.pushPose();
        RenderUtils.prepMatrixForBone(poseStack, bone);
        this.renderCubesOfBone(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.renderChildBones(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}
