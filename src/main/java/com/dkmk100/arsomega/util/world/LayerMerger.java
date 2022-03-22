/*
package com.dkmk100.arsomega.util.world;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;

public enum LayerMerger implements IAreaTransformer2, IDimOffset1Transformer {
    INSTANCE;
    @Override
    public int apply(INoiseRandom rand, IArea area1, IArea area2, int x, int z) {
        int plainCheck = area1.getValue(x, z);
        int mixedBiomeValue = area2.getValue(x, z);
        //Checks whether area1 is a plains biome, then we replace the plains with our biomes.
        return (plainCheck) == Registry.BIOME.getId(Biomes.PLAINS) ? mixedBiomeValue : plainCheck;
    }
}
 */
