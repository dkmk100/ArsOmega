package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.model.VoidBossModel;
import com.dkmk100.arsomega.entities.VoidBossEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class VoidBossRenderer extends MobRenderer<VoidBossEntity, VoidBossModel<VoidBossEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(ArsOmega.MOD_ID,"textures/entity/void_boss.png");
    public VoidBossRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VoidBossModel<VoidBossEntity>(1), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidBossEntity entity) {
        return TEXTURE;
    }
}
