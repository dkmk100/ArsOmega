package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.client.models.RaptorModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoEntityRenderer;

public class RaptorRenderer extends GeoEntityRenderer {
    public RaptorRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RaptorModel());
    }
}
