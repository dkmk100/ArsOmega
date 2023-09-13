package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.client.models.ColoredItemModel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.util.Color;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoModel;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.ars_nouveau.geckolib3.util.EModelRenderCycle;

import java.util.Collections;

public class ColoredItemRenderer<T extends Item & IAnimatable, DyeableLeatherItem> extends GeoItemRenderer<T> {
    String name;

    public ColoredItemRenderer(String name) {
        this(new ColoredItemModel<>(name),name);
    }
    public ColoredItemRenderer(AnimatedGeoModel modelProvider, String name) {
        super(modelProvider);
        this.name = name;
    }

    @Override
    public void render(T animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack stack) {
        this.currentItemStack = stack;
        GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelResource(animatable));
        AnimationEvent animationEvent = new AnimationEvent(animatable, 0.0F, 0.0F, Minecraft.getInstance().getFrameTime(), false, Collections.singletonList(stack));
        this.dispatchedMat = poseStack.last().pose().copy();
        this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        this.modelProvider.setLivingAnimations(animatable, this.getInstanceId(animatable), animationEvent);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5099999904632568, 0.5);
        RenderSystem.setShaderTexture(0, this.getTextureLocation(animatable));

        Color renderColor = getRenderColor(animatable, stack);

        RenderType renderType = this.getRenderType(animatable, 0.0F, poseStack, bufferSource, (VertexConsumer)null, packedLight, this.getTextureLocation(animatable));
        this.render(model, animatable, 0.0F, renderType, poseStack, bufferSource, (VertexConsumer)null, packedLight, OverlayTexture.NO_OVERLAY, (float)renderColor.getRed() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
        poseStack.popPose();
    }

    Color getRenderColor(T animatable, ItemStack stack){
        int color = ((net.minecraft.world.item.DyeableLeatherItem) animatable).getColor(stack);
        return Color.ofOpaque(color);
    }
}
