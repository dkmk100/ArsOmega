package com.dkmk100.arsomega.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Mob;

public class BipedModel<M extends Mob> extends HumanoidModel<M> {

    public BipedModel(ModelPart pRoot) {
        super(pRoot, RenderType::entityTranslucent);
    }

}
