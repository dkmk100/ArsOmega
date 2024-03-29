package com.dkmk100.arsomega.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class ParticleRenderTypes {
    public static final ParticleRenderType DARK_EMBER_RENDER = new ParticleRenderType() {
        public void begin(BufferBuilder buffer, TextureManager textureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.enableBlend();
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator tessellator) {
            tessellator.end();
        }

        public String toString() {
            return "ars_nouveau:em_rend";
        }
    };
    public static final ParticleRenderType DARK_EMBER_RENDER_NO_MASK = new ParticleRenderType() {
        public void begin(BufferBuilder buffer, TextureManager textureManager) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.enableCull();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator tessellator) {
            tessellator.end();
            RenderSystem.enableDepthTest();
        }

        public String toString() {
            return "ars_nouveau:em_rend_no_mask";
        }
    };
}
