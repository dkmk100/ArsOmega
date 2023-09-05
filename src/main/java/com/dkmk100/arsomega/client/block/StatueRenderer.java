package com.dkmk100.arsomega.client.block;

import com.dkmk100.arsomega.blocks.StatueBlock;
import com.dkmk100.arsomega.blocks.StatueTile;
import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapability;
import com.dkmk100.arsomega.capabilitysyncer.OmegaStatusesCapabilityAttacher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

public class StatueRenderer<T extends StatueTile> implements BlockEntityRenderer<T> {

    private final EntityRenderDispatcher entityRend;

    public StatueRenderer(BlockEntityRendererProvider.Context pContext){
        entityRend = pContext.getEntityRenderer();
    }

    @Override
    public void render(T tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Entity entity = tile.getEntity();
        if(!(entity instanceof  LivingEntity living)){
            return;
        }

        Direction direction = tile.getBlockState().getValue(StatueBlock.FACING);
        Vector3f vec = new Vector3f(0.5f,0f,0.5f);

        matrixStackIn.translate( vec.x(),vec.y(),vec.z());

        if(direction == Direction.EAST) {
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            //vec.add(0,0f,0.5f);
        }else if(direction == Direction.WEST){
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
            //vec.add(0,0f,-0.5f);
        }else if(direction == Direction.NORTH){
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            //vec.add(0.5f,0f,0);
        }else if(direction == Direction.SOUTH){
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            //vec.add(0.5f,0f,0.5f);
        }
        matrixStackIn.mulPose(tile.getBlockState().getValue(StatueBlock.FACING).getRotation());



        partialTicks = 0;
        LazyOptional<OmegaStatusesCapability> optional = OmegaStatusesCapabilityAttacher.getLivingEntityCapability(living).cast();
        optional.ifPresent((cap) -> {
            cap.setPetrified(true,2);
            cap.setPetrificationProgress(10);
        });

        entityRend.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
    }
}
