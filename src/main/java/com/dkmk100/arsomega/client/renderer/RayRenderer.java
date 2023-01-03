package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.client.models.RayModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoEntityRenderer;

public class RayRenderer  extends GeoEntityRenderer {
    public RayRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RayModel());
    }
}

