package com.dkmk100.arsomega.glyphs;

import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigurableGlyph {
    void buildExtraConfig(ForgeConfigSpec.Builder builder);
    String getName();
    String getId();
}
