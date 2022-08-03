package com.dkmk100.arsomega.glyphs;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IConfigurable {

    void buildExtraConfig(ForgeConfigSpec.Builder builder);
    String getTag();
}
