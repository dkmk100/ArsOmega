package com.dkmk100.arsomega.client.renderLayer;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapability;
import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapabilityAttacher;
import com.dkmk100.arsomega.potions.ModPotions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

public class PetrificationLayer extends RenderLayer {
    public PetrificationLayer(RenderLayerParent parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource source, int packedLight, Entity entity, float swing, float swingAmount, float partialTicks, float age, float headYaw, float headPitch) {
        if (entity instanceof LivingEntity living) {
            LazyOptional<OmegaStatusesCapability> optional = OmegaStatusesCapabilityAttacher.getLivingEntityCapability(living).cast();
            if(!optional.isPresent() || !optional.resolve().isPresent()){
                return;
            }
            OmegaStatusesCapability cap = optional.resolve().get();
            if (cap.isPetrified()) {


                VertexConsumer consumer;
                //obviously temporary as hell, I'm too intimidated to write a custom shader ATM so that the colors work right.
                if(cap.getPetrificationLevel() == 0){
                    consumer = source.getBuffer(CustomRenderType.PETRIFICATION_WEAK);
                }
                else if(cap.getPetrificationProgress() < 2){
                    consumer = source.getBuffer(CustomRenderType.PETRIFICATION_WEAK);
                }
                else{
                    consumer = source.getBuffer(CustomRenderType.PETRIFICATION);
                }

                stack.pushPose();
                //float alpha = 0.2f + 0.8f * 0.1f * cap.getPetrificationProgress();
                float alpha = 1f;
                consumer.color(1,1,1,alpha);
                this.getParentModel().renderToBuffer(stack, consumer, packedLight, LivingEntityRenderer.getOverlayCoords(living, 0), 1, 1, 1, alpha);
                stack.popPose();
            }
        }
    }

    //called by ClientRegisterEvents
    public static void addRenderLayers(final EntityRenderersEvent.AddLayers event){
        List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
                ForgeRegistries.ENTITY_TYPES.getValues().stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                        .collect(Collectors.toList()));

        for(EntityType<? extends LivingEntity> type : entityTypes){
            try {
                var renderer = event.getRenderer(type);
                if (renderer != null) {
                    renderer.addLayer(new PetrificationLayer(renderer));
                }
            }
            catch (Exception e){
                ArsOmega.LOGGER.warn("couldn't apply petrification layer to type: "+type.toString());
                e.printStackTrace();
            }
        }

        for (String skinName : event.getSkins()){
            LivingEntityRenderer skin = event.getSkin(skinName);
            skin.addLayer(new PetrificationLayer(skin));
        }
    }

}
