package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.model.VoidBeastModel;
import com.dkmk100.arsomega.entities.VoidBeastEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class VoidBeastRenderer extends MobRenderer<VoidBeastEntity, VoidBeastModel<VoidBeastEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(ArsOmega.MOD_ID,"textures/entity/void_beast.png");
    public VoidBeastRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VoidBeastModel<VoidBeastEntity>(), 0.5f);
    }
    @Override
    public ResourceLocation getTextureLocation(VoidBeastEntity entity) {
        return TEXTURE;
    }
}
