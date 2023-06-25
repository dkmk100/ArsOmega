package com.dkmk100.arsomega.client.block;

import com.dkmk100.arsomega.blocks.DemonicLightTile;
import com.dkmk100.arsomega.client.particle.DarkGlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Random;

public class DemonicLightRenderer implements BlockEntityRenderer<DemonicLightTile> {
    public DemonicLightRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
    }

    public void render(DemonicLightTile lightTile, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        Level world = lightTile.getLevel();
        BlockPos pos = lightTile.getBlockPos();
        RandomSource rand = world.random;
        if (!Minecraft.getInstance().isPaused()) {
            world.addParticle(DarkGlowParticleData.createData(lightTile.color, 0.25F, 0.9F, 36), (double)pos.getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), (double)pos.getY() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), (double)pos.getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), 0.0, 0.0, 0.0);
        }
    }

    int RandColor(int max,RandomSource rand){
        int x = (max/3) + 1;
        return Math.max(rand.nextInt(x)+max-x,1);
    }
}