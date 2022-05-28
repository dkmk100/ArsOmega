package com.dkmk100.arsomega.glyphs;

import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigurableGlyph {
    void buildExtraConfig(ForgeConfigSpec.Builder builder);
    void setConfig(ForgeConfigSpec spec);
    String getName();
    String getId();
}
