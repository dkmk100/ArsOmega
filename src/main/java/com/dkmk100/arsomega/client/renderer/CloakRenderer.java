package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.models.CloakModel;
import com.dkmk100.arsomega.client.models.ColoredItemModel;
import com.dkmk100.arsomega.items.Cloak;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoItemRenderer;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.awt.Color;

public class CloakRenderer extends GeoItemRenderer<Cloak> implements ICurioRenderer {

    public CloakRenderer(String name, String texture){
        super(new CloakModel<Cloak>(name, texture));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int light, float v, float v1, float v2, float v3, float pitch, float yaw) {
        Cloak c = (Cloak) itemStack.getItem();
        poseStack.pushPose();

        //TODO: IDK WTF I'm doing here but it's wrong
        //also why is this even a geckolib model wtf use a normal java model

        ICurioRenderer.rotateIfSneaking(poseStack, slotContext.entity());
        ICurioRenderer.translateIfSneaking(poseStack, slotContext.entity());

        float pitchRad = pitch*(float)Math.PI/180;
        float yawRad = yaw*(float)Math.PI/180;

        float pitchRot = -pitchRad;
        float yawRot = (float)Math.PI+yawRad;
        float rollRot = 0;

        //adjustments
        pitchRot = pitchRot/3;
        yawRot = Math.max((float)Math.PI, yawRot);

        poseStack.mulPose(Quaternion.fromXYZ(yawRot, pitchRot, rollRot));
        poseStack.translate(-1, -0.5, -0.9);

        this.render(c, poseStack, multiBufferSource, light, itemStack);

        poseStack.popPose();
    }
}
