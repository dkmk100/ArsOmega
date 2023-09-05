package com.dkmk100.arsomega.client.renderLayer;

import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapability;
import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapabilityAttacher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.IGeoRenderer;

public class PetrificationGeoLayer extends GeoLayerRenderer {
    public PetrificationGeoLayer(IGeoRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource source, int packedLight, Entity entity, float swing, float swingAmount, float partialTicks, float age, float headYaw, float headPitch) {
        if (entity instanceof LivingEntity living && entity instanceof IAnimatable) {
            LazyOptional<OmegaStatusesCapability> optional = OmegaStatusesCapabilityAttacher.getLivingEntityCapability(living).cast();
            if(!optional.isPresent() || !optional.resolve().isPresent()){
                return;
            }
            OmegaStatusesCapability cap = optional.resolve().get();
            if (cap.isPetrified()) {

                //obviously temporary as hell, I'm too intimidated to write a custom shader ATM so that the colors work right.
                RenderType type;
                if(cap.getPetrificationLevel() == 0){
                    type = CustomRenderType.PETRIFICATION_WEAK;
                }
                else if(cap.getPetrificationProgress() < 2){
                    type = CustomRenderType.PETRIFICATION_WEAK;
                }
                else{
                    type = CustomRenderType.PETRIFICATION;
                }
                VertexConsumer consumer = source.getBuffer(type);

                stack.pushPose();
                //float alpha = 0.2f + 0.8f * 0.1f * cap.getPetrificationProgress();
                float alpha = 1f;
                consumer.color(1,1,1,alpha);

                ResourceLocation modelLoc = this.entityRenderer.getGeoModelProvider().getModelResource(entity);

                GeoModel model = this.entityRenderer.getGeoModelProvider().getModel(modelLoc);

                int overlayCoords;
                try {
                    if (this.entityRenderer instanceof GeoEntityRenderer<?> rend) {
                        overlayCoords = rendererOverlayHelper(rend, living);
                    } else {
                        overlayCoords = LivingEntityRenderer.getOverlayCoords(living, 0f);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    overlayCoords = LivingEntityRenderer.getOverlayCoords(living, 0f);
                }

                this.entityRenderer.render(model, entity, partialTicks, type, stack, source, consumer, packedLight, overlayCoords, 1,1,1, alpha);

                stack.popPose();
            }
        }
    }

    private <T extends LivingEntity & IAnimatable> int rendererOverlayHelper(GeoEntityRenderer<T> rend, Entity entity){
        return rend.getOverlay((T) entity,0f);
    }
}
