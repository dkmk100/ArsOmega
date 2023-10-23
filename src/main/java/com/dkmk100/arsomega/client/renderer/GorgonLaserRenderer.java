package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.entities.EntityGorgon;
import net.minecraft.resources.ResourceLocation;

public class GorgonLaserRenderer extends LaserRenderer<EntityGorgon> {
    public GorgonLaserRenderer(ResourceLocation texture) {
        super(texture);
    }

    @Override
    int getColor(EntityGorgon entity, float time) {
        return entity.getLaserColor();
    }
}
