/*

package com.dkmk100.arsomega.init;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.ReflectionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Map;

public class DelayedStructureInit {
    public static StructureFeature<?, ?> CONFIGURED_DEMON_DUNGEON = StructureInit.DEMON_DUNGEON.get().configured(IFeatureConfig.NONE);

    public static void RegisterConfiguredStructures(){

        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;

        Registry.register(registry, new ResourceLocation(ArsOmega.MOD_ID, "configured_demon_dungeon"), CONFIGURED_DEMON_DUNGEON);

        try {
            ((Map<Structure<?>, StructureFeature<?, ?>>)ReflectionHandler.structureFeatures.get(null))
                    .put(StructureInit.DEMON_DUNGEON.get(), CONFIGURED_DEMON_DUNGEON);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

 */