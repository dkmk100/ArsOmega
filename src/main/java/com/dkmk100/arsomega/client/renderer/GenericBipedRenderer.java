package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class GenericBipedRenderer extends MobRenderer<MobEntity, BipedModel<MobEntity>> {
    public ResourceLocation TEXTURE;
    public GenericBipedRenderer(EntityRendererManager renderManagerIn, String registryName) {
        super(renderManagerIn, new BipedModel<MobEntity>(1), 0.5f);
        TEXTURE = new ResourceLocation(ArsOmega.MOD_ID,"textures/entity/" + registryName + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(MobEntity entity) {
        return TEXTURE;
    }
}
