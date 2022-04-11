package com.dkmk100.arsomega.world;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.init.ExperimentalStructureInit;
import com.dkmk100.arsomega.init.StructureInit;
import com.dkmk100.arsomega.structures.CustomStructure;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.mojang.serialization.Codec;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FeatureGen {
    @SubscribeEvent
    public static void populateBiomes(BiomeLoadingEvent event) {
        RegistryKey<Biome> key = RegistryKey.create(Registry.BIOME_REGISTRY, event.getName());
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);
        List<Supplier<StructureFeature<?, ?>>> structures = event.getGeneration().getStructures();
        String[] allDemonBiomes = new String[]{"demon_biome"};
        ///*
        //if (isCorrectBiome(event, allDemonBiomes)) {
            //structures.add(() -> DelayedStructureInit.CONFIGURED_DEMON_DUNGEON);
        //}
        // */

        int id = 0;
        for (RegistryObject<Structure<?>> structure : ExperimentalStructureInit.STRUCTURES.getEntries()) {
            int finalId = id;
            if (structure.get() instanceof CustomStructure) {
                CustomStructure custom = (CustomStructure) structure.get();
                if (custom.biomes.length == 0 && (types.contains(BiomeDictionary.Type.PLAINS))) {
                    //this is totally gonna crash someday lol
                    //I'm sorry
                    structures.add(() -> ExperimentalStructureInit.features.get(finalId));
                } else if (custom.biomes.length > 0 && isCorrectBiome(event, custom.biomes)) {
                    //this is totally gonna crash someday lol
                    //I'm sorry
                    ArsOmega.LOGGER.info("adding structure to biome: "+event.getName().toString());
                    structures.add(() -> ExperimentalStructureInit.features.get(finalId));
                }
            }
            else{
                ArsOmega.LOGGER.error("non-custom structure found...");
            }
            id += 1;
        }
    }
    private static boolean isCorrectBiome(BiomeLoadingEvent event, String biomeName){
        return event.getName().toString().equals(new ResourceLocation(ArsOmega.MOD_ID,biomeName).toString());
    }
    private static boolean isCorrectBiome(BiomeLoadingEvent event, String[] biomeNames){
        for (String biomeName : biomeNames) {
            if(isCorrectBiome(event,biomeName)){
                return true;
            }
        }
        return false;
    }
    private static Method GETCODEC_METHOD;
    @SubscribeEvent
    public static void addDimensionalSpacing(WorldEvent.Load event){
        if(event.getWorld() instanceof ServerWorld){
            Logger LOGGER = LogManager.getLogger();
            ServerWorld world = (ServerWorld) event.getWorld();

            try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(world.getChunkSource().generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){

                LOGGER.error("Was unable to check if " + world.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }

            if(world.getChunkSource().getGenerator() instanceof FlatChunkGenerator &&
                    world.dimension().equals(World.OVERWORLD)){
                return;
            }

            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(world.getChunkSource().generator.getSettings().structureConfig());

            //tempMap.putIfAbsent(StructureInit.DEMON_DUNGEON.get(), DimensionStructuresSettings.DEFAULTS.get(StructureInit.DEMON_DUNGEON.get()));

            int id = 0;
            ArrayList<RegistryObject<Structure<?>>> list = new ArrayList<>(ExperimentalStructureInit.STRUCTURES.getEntries());
            for (RegistryObject<Structure<?>> structure : list) {
                int finalId = id;
                if (structure.get() instanceof CustomStructure) {
                    tempMap.putIfAbsent(structure.get(),DimensionStructuresSettings.DEFAULTS.get(list.get(finalId).get()));
                    ArsOmega.LOGGER.info("added structure: "+structure.get().getFeatureName() + "to dim: " + ((ServerWorld) event.getWorld()).dimension().toString());
                }
                else{
                    ArsOmega.LOGGER.info("non-custom structure: "+structure.get().getFeatureName());
                }
                id += 1;
            }

            try {
                ReflectionHandler.structureConfig.set(world.getChunkSource().generator.getSettings(),tempMap);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
