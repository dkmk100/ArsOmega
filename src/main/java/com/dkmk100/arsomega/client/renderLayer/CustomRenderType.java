package com.dkmk100.arsomega.client.renderLayer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;

public class CustomRenderType extends RenderType {
    public CustomRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    protected static final RenderStateShard.TexturingStateShard PETRIFICATION_TEXTURING = new
            TexturingStateShard("petrification",() -> {}, () -> {});

    protected static final RenderStateShard.TextureStateShard PETRIFICATION_TEXTURE = new
            TextureStateShard(new ResourceLocation("arsomega","textures/render_layers/petrification.png"),false,false);

    protected static final RenderStateShard.TextureStateShard PETRIFICATION_TEXTURE_WEAK = new
            TextureStateShard(new ResourceLocation("arsomega","textures/render_layers/petrification_transparent.png"),false,false);


    public static final RenderType PETRIFICATION = create("petrification",
            DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 512, true, true,
            RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
            .setTextureState(PETRIFICATION_TEXTURE)
            .setLightmapState(LIGHTMAP)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .setCullState(NO_CULL)
            .setDepthTestState(EQUAL_DEPTH_TEST)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setTexturingState(PETRIFICATION_TEXTURING)
            .setOverlayState(OVERLAY)
            .createCompositeState(true));

    public static final RenderType PETRIFICATION_WEAK = create("petrification_weak",
            DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 512, true, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
                    .setTextureState(PETRIFICATION_TEXTURE_WEAK)
                    .setLightmapState(LIGHTMAP)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTexturingState(PETRIFICATION_TEXTURING)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));

    //called by ClientRegisterEvents
    public static void registerShaders(final RegisterShadersEvent event){

    }

}
