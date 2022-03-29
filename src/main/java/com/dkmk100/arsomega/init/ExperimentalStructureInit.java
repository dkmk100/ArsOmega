package com.dkmk100.arsomega.init;


import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.events.CustomBus;
import com.dkmk100.arsomega.events.RegisterStructuresEvent;
import com.dkmk100.arsomega.structures.CustomStructure;
import com.dkmk100.arsomega.structures.DemonicDungeonStructure;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.common.entity.pathfinding.FMLEventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//use generic structure class, along with a list, to almost completely automate structures and only require the one line. Not sure how yet, I'll figure it out.
public class ExperimentalStructureInit {
    public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, ArsOmega.MOD_ID);

    public static void RegisterStructures(IEventBus bus) {
        STRUCTURES.register(bus);
    }

    public static final String[] demonBiomes = {"demon_biome"};

    public static final RegistryObject<Structure<NoFeatureConfig>> GORGON_CAVE = STRUCTURES.register("gorgon_cave", () -> new
            CustomStructure("gorgon_cave",new String[]{},false,0,215,245));



    public static RegistryObject<Structure<NoFeatureConfig>> RegisterStructure(CustomStructure structure){
        return STRUCTURES.register(structure.name,() -> structure);
    }


    public static List<StructureFeature<?, ?>> features = new ArrayList<>();


    public static void Initialize(final FMLCommonSetupEvent event){
        ArsOmega.LOGGER.info("Initializing custom structures");
        event.enqueueWork(() -> {
            setupStructures();
            registerConfiguredStructures();
        });
    }
    static void setupStructures(){
        int amountRegistered = 0;
        for(RegistryObject<Structure<?>> structure : STRUCTURES.getEntries()){
            if(structure.get() instanceof CustomStructure){
                CustomStructure custom = (CustomStructure)structure.get();
                StructureInit.setupMapSpacingAndLand(custom,
                        new StructureSeparationSettings(custom.maxSpacing,custom.minSpacing, 32131805 + 4327*amountRegistered),
                        true);
            }
            else{
                StructureInit.setupMapSpacingAndLand(structure.get(),
                        new StructureSeparationSettings(150,100, 30914807 + 5234*amountRegistered),
                        true);
            }
            amountRegistered+=1;
        }
    }
    static void registerConfiguredStructures(){
        for(RegistryObject<Structure<?>> structure : STRUCTURES.getEntries()) {
            try{
                features.add(((Structure<NoFeatureConfig>)structure.get()).configured(IFeatureConfig.NONE));
            } catch (Exception e){
                e.printStackTrace();
                ArsOmega.LOGGER.error("Exception on registering configured structure");
            }
        }
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;

        for(StructureFeature<?, ?> feature : features){
            if(feature.feature instanceof Structure) {
                Registry.register(registry, new ResourceLocation(((Structure)feature.feature).getFeatureName()),feature);
                //flat generator fix
                try {
                    ((Map<Structure<?>, StructureFeature<?, ?>>) ReflectionHandler.structureFeatures.get(null))
                            .put(feature.feature, feature);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else{
                ArsOmega.LOGGER.error("Feature not structure");
            }
        }
    }
}
