package com.dkmk100.arsomega.client.renderer;

import com.dkmk100.arsomega.entities.EntityGorgon;
import com.dkmk100.arsomega.util.ResourceUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GorgonRenderer<T extends EntityGorgon> extends GeckolibEntityRenderer<T>{
    private GorgonLaserRenderer laserRenderer;
    public GorgonRenderer(EntityRendererProvider.Context renderManager, String name) {
        super(renderManager, name);
        laserRenderer = new GorgonLaserRenderer(ResourceUtil.getEntityTextureResource(name+"_laser"));
    }

    @Override
    public boolean shouldRender(T gorgon, Frustum frustum, double x, double y, double z) {
        if(super.shouldRender(gorgon, frustum, x, y, z)){
            return true;
        }
        else{
            LivingEntity livingentity = gorgon.getLaserTarget();
            if (livingentity != null) {
                Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * 0.5D, 1.0F);
                Vec3 vec31 = this.getPosition(gorgon, gorgon.getEyeHeight(), 1.0F);
                return frustum.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
            }
        }
        return false;
    }

    @Override
    public void render(T gorgon, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(gorgon, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        LivingEntity living = gorgon.getLaserTarget();
        if(living != null) {
            float eyePos = gorgon.getEyeHeight();

            //obv push pose so we can pop when done
            poseStack.pushPose();

            //move beam to eye
            poseStack.translate(0.0D, (double)eyePos, 0.0D);

            Vec3 targetPos = this.getPosition(living, (double) living.getBbHeight() * 0.5D, partialTick);
            Vec3 gorgonPos = this.getPosition(gorgon, eyePos, partialTick);
            Vec3 dir = targetPos.subtract(gorgonPos);
            float dist = (float) (dir.length());

            dir = dir.normalize();

            //calculate yaw and heading
            float yaw = (float) Math.acos(dir.y);
            float heading = (float) Math.atan2(dir.z, dir.x);

            //use yaw and heading to move pose stack
            poseStack.mulPose(Vector3f.YP.rotationDegrees((((float) Math.PI / 2F) - heading) * (180F / (float) Math.PI)));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(yaw * (180F / (float) Math.PI)));

            float laserLength = dist + 1;
            float rotateSpeed = 1.5f;

            laserRenderer.render(gorgon, gorgon.level.getGameTime() + partialTick, poseStack, bufferSource, packedLight, laserLength, rotateSpeed);

            //obviously very important
            poseStack.popPose();
        }
    }

    private Vec3 getPosition(LivingEntity p_114803_, double p_114804_, float p_114805_) {
        double d0 = Mth.lerp((double)p_114805_, p_114803_.xOld, p_114803_.getX());
        double d1 = Mth.lerp((double)p_114805_, p_114803_.yOld, p_114803_.getY()) + p_114804_;
        double d2 = Mth.lerp((double)p_114805_, p_114803_.zOld, p_114803_.getZ());
        return new Vec3(d0, d1, d2);
    }
}
