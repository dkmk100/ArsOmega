package com.dkmk100.arsomega.rituals;

import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigurableRitual {
    void buildConfig(ForgeConfigSpec.Builder builder);
    String getName();
    String getID();
}