/*
package com.dkmk100.arsomega.util.world;

import com.dkmk100.arsomega.BeyondTheAbyss;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

import java.util.ArrayList;
import java.util.List;

public enum AddModdedBiomes implements IAreaTransformer0 {
    INSTANCE;


    public int apply(INoiseRandom rand, int x, int z) {
        //Plains in this case is the less frequent biome(1/10 chance).
        return Registry.BIOME.getId(BeyondTheAbyss.biomes.get(rand.random(BeyondTheAbyss.biomes.size())));
    }
}
*/