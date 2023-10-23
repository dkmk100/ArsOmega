package com.dkmk100.arsomega.client.renderer;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class LaserRenderer<T> {
    final ResourceLocation location;
    final RenderType renderType;
    public LaserRenderer(ResourceLocation texture){
        location = texture;
        renderType = RenderType.entityCutoutNoCull(texture);
    }

    //expects you to rotate already
    public void render(T entity, float currentTime, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float length, float rotateSpeed){

        VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();

        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        int color = getColor(entity, currentTime);
        float oscilation = currentTime * 0.5F % 1.0F;

        float innerOffset = -1.0F + oscilation;
        float outerOffset = length * 2.5F + innerOffset;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;

        float zero = 0f;
        float half = 0.5f;

        float angle = currentTime * 0.05F * -1.0f * rotateSpeed;

        float width = 0.2f;

        float angle11 = getAngle(angle, 4);
        float angle12 = getAngle(angle, 0);
        float angle21 = getAngle(angle, 2);
        float angle22 = getAngle(angle, 6);


        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle11)*width, length, Mth.sin(angle11)*width,r,g,b,half,outerOffset);
        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle11)*width, zero, Mth.sin(angle11)*width,r,g,b,half,innerOffset);
        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle12)*width, zero, Mth.sin(angle12)*width,r,g,b,zero,innerOffset);
        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle12)*width, length, Mth.sin(angle12)*width,r,g,b,zero,outerOffset);

        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle21)*width, length, Mth.sin(angle21)*width,r,g,b,half,outerOffset);
        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle21)*width, zero, Mth.sin(angle21)*width,r,g,b,half,innerOffset);
        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle22)*width, zero, Mth.sin(angle22)*width,r,g,b,zero,innerOffset);
        vertex(vertexconsumer, poseMatrix, normalMatrix, Mth.cos(angle22)*width, length, Mth.sin(angle22)*width,r,g,b,zero,outerOffset);

    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f pose, Matrix3f normal, float x, float y, float z, int r, int g, int b, float u, float v) {
        vertexConsumer.vertex(pose, x, y, z).color(r, g, b, 255).uv(u,v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    int getColor(T entity, float time){
        return ParticleColor.RED.getColor();
    }

    static float getAngle(float angle, float quarterTurns){
        return angle + ((float)Math.PI * quarterTurns/4f);
    }




}
