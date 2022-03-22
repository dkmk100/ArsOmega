package com.dkmk100.arsomega.client.model;

import com.dkmk100.arsomega.entities.VoidBossEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidBossModel<T extends VoidBossEntity> extends BipedModel<T> {
    public VoidBossModel(float modelSize) {
        super(modelSize);
    }

}
