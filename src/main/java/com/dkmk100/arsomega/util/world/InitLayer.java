/*
package com.dkmk100.arsomega.util.world;

import com.dkmk100.arsomega.BeyondTheAbyss;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum InitLayer implements IAreaTransformer0 {
    INSTANCE;

    public int apply(INoiseRandom rand, int x, int z) {
        //Plains in this case is the less frequent biome(1/10 chance).
        return rand.random(10) == 0 ? Registry.BIOME.getId(Biomes.PLAINS) : Registry.BIOME.getId(RegistryHandler.VOID_BIOME.get());
    }
}

 */
