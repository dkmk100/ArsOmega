package com.dkmk100.arsomega.rituals;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigurableRitual {
    void buildConfig(ForgeConfigSpec.Builder builder);
    String getName();
    ResourceLocation getRegistryName();

    public AbstractRitual getRitual();
}