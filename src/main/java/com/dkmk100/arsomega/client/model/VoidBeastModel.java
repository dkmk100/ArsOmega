package com.dkmk100.arsomega.client.model;

import com.dkmk100.arsomega.entities.VoidBeastEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidBeastModel<T extends VoidBeastEntity> extends EntityModel<T> {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer cube_r1;
    private final ModelRenderer legs;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;
    private final ModelRenderer tail;
    private final ModelRenderer cube_r2;

    public VoidBeastModel() {
        texWidth = 16;
        texHeight = 16;

        body = new ModelRenderer(this);
        body.setPos(0.0F, 24.0F, 0.0F);
        body.texOffs(0, 0).addBox(-2.0F, -7.0F, -4.0F, 4.0F, 4.0F, 9.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(0.0F, 24.0F, 0.0F);


        cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(0.0F, -7.0F, -4.0F);
        head.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.0436F, 0.0F, 0.0F);
        cube_r1.texOffs(0, 0).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);

        legs = new ModelRenderer(this);
        legs.setPos(0.0F, 21.0F, 1.0F);


        leg1 = new ModelRenderer(this);
        leg1.setPos(-2.0F, 0.0F, -4.0F);
        legs.addChild(leg1);
        leg1.texOffs(10, 1).addBox(0.0F, -0.1F, -0.8F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        leg2 = new ModelRenderer(this);
        leg2.setPos(-2.0F, 0.0F, 3.0F);
        legs.addChild(leg2);
        leg2.texOffs(10, 1).addBox(0.0F, -0.1F, -0.2F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        leg3 = new ModelRenderer(this);
        leg3.setPos(-2.0F, 0.0F, 3.0F);
        legs.addChild(leg3);
        leg3.texOffs(10, 1).addBox(3.2F, -0.1F, -0.2F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        leg4 = new ModelRenderer(this);
        leg4.setPos(-2.0F, 0.0F, 3.0F);
        legs.addChild(leg4);
        leg4.texOffs(10, 0).addBox(3.0F, -0.1F, -7.8F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        tail = new ModelRenderer(this);
        tail.setPos(0.0F, 19.0F, -3.0F);


        cube_r2 = new ModelRenderer(this);
        cube_r2.setPos (0.0F, -2.0F, 8.0F);
        tail.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.2182F, 0.0F, 0.0F);
        cube_r2.texOffs(17, 9).addBox(-1.0F, -0.4F, -0.2F, 1.0F, 1.0F, 3.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        
    }
    

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        body.render(matrixStack, buffer, packedLight, packedOverlay);
        head.render(matrixStack, buffer, packedLight, packedOverlay);
        legs.render(matrixStack, buffer, packedLight, packedOverlay);
        tail.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}