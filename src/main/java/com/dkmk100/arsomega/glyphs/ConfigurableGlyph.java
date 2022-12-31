package com.dkmk100.arsomega.glyphs;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigurableGlyph {
    void buildExtraConfig(ForgeConfigSpec.Builder builder);
    String getName();
    ResourceLocation getRegistryName();
}
