
package com.dkmk100.arsomega.init;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.structures.DemonicDungeonStructure;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class StructureInit {
    /*
    public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, ArsOmega.MOD_ID);

    public static final RegistryObject<Structure<NoFeatureConfig>> DEMON_DUNGEON = STRUCTURES.register("demonic_dungeon", () -> new DemonicDungeonStructure(NoFeatureConfig.CODEC));

    public static void RegisterStructures(IEventBus bus) {
        STRUCTURES.register(bus);
    }

    public static void setupStructures() {
        setupMapSpacingAndLand(DEMON_DUNGEON.get(),
                new StructureSeparationSettings(150,100, 30914807),
                true);
    }
    */

    public static <F extends Structure<?>> void setupMapSpacingAndLand(F structure, StructureSeparationSettings structureSeparationSettings,
                                                                       boolean transformSurroundingLand) {
        //add our structures into the map in Structure class
        Structure.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);
        if(transformSurroundingLand){
            try {
                ReflectionHandler.noiseFeatures.set(null,
                        ImmutableList.<Structure<?>>builder()
                                .addAll(Structure.NOISE_AFFECTING_FEATURES)
                                .add(structure)
                                .build());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        try {
            ReflectionHandler.dimensionDefaults.set(null,
                    ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                            .putAll(DimensionStructuresSettings.DEFAULTS)
                            .put(structure, structureSeparationSettings)
                            .build());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
            Map<Structure<?>, StructureSeparationSettings> structureMap = settings.getValue().structureSettings().structureConfig();

            if(structureMap instanceof ImmutableMap){
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, structureSeparationSettings);
                try {
                    ReflectionHandler.structureConfig.set(settings.getValue().structureSettings(),tempMap);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else{
                structureMap.put(structure, structureSeparationSettings);
            }
        });

    }
}

