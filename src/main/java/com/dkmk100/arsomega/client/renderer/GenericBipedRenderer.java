package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.HumanoidModel;

import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;

public class GenericBipedRenderer extends HumanoidMobRenderer<Mob, HumanoidModel<Mob>> {
    public ResourceLocation TEXTURE;

    public GenericBipedRenderer(EntityRendererProvider.Context context, String texture) {
        super(context, new HumanoidModel<>(context.getModelSet().bakeLayer(ModelLayers.PLAYER)), 0.5f);
        TEXTURE = new ResourceLocation(ArsOmega.MOD_ID,"textures/entity/" + texture + ".png");

    }

    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        return TEXTURE;
    }
}
