package com.dkmk100.arsomega;

import com.dkmk100.arsomega.client.renderer.GenericBipedRenderer;
import com.dkmk100.arsomega.entities.EntityBossDemonKing;
import com.dkmk100.arsomega.entities.EntityDemonBasic;
//import com.dkmk100.arsomega.init.DelayedStructureInit;
import com.dkmk100.arsomega.events.CustomBus;
import com.dkmk100.arsomega.events.CustomEvents;
import com.dkmk100.arsomega.events.RegisterStructuresEvent;
import com.dkmk100.arsomega.init.ExperimentalStructureInit;
import com.dkmk100.arsomega.init.StructureInit;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("arsomega")
public class ArsOmega
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "arsomega";
    public static ArrayList<Biome> biomes= new ArrayList<>();

    public ArsOmega() {
        RegistryHandler.registerGlyphs();
        RegistryHandler.registerRituals();
        CustomBus.BUS.register(CustomEvents.class);
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class,RegistryHandler::registerBlocks);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::finalSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::CursedPlsDontUseWhy);
        RegistryHandler.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void CursedPlsDontUseWhy(final RegistryEvent.NewRegistry event){
        CustomBus.BUS.post(new RegisterStructuresEvent("test"));
    }


    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Reflection Handler");
        try {
            ReflectionHandler.Initialize();
        }
        catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.ERROR,"Exception in reflection handler initialization, mod is likely now in a broken state");
        }


        /*
        event.enqueueWork(() -> {
            StructureInit.setupStructures();
            DelayedStructureInit.RegisterConfiguredStructures();
        });

         //*/
        ExperimentalStructureInit.Initialize(event);

        GlobalEntityTypeAttributes.put(RegistryHandler.BASIC_DEMON.get(), EntityDemonBasic.createAttributes().build());
        GlobalEntityTypeAttributes.put(RegistryHandler.STRONG_DEMON.get(), EntityDemonBasic.createAttributes().build());
        GlobalEntityTypeAttributes.put(RegistryHandler.BOSS_DEMON_KING.get(), EntityBossDemonKing.createAttributes().build());
    }
    private void clientSetup(final FMLClientSetupEvent event)
    {
        RenderTypeLookup.setRenderLayer(RegistryHandler.POISON_FLOWER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.ARCANE_BLOOM.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.BRAMBLE_1.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.BRAMBLE_2.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.BRAMBLE_3.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.BRAMBLE_4.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.GORGON_FIRE.get(), RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(ItemsRegistry.INFINITY_JAR, RenderType.cutout());

        RegisterMobRenderer(RegistryHandler.BASIC_DEMON.get(),"demon_basic");
        RegisterMobRenderer(RegistryHandler.STRONG_DEMON.get(),"demon_strong");
        RegisterMobRenderer(RegistryHandler.BOSS_DEMON_KING.get(),"boss_demon_king");

        //RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.VOID_BEAST.get(), VoidBeastRenderer::new);
    }
    @OnlyIn(Dist.CLIENT)
    private void RegisterMobRenderer(EntityType<? extends MobEntity> entity, String registryName){
        RenderingRegistry.registerEntityRenderingHandler(entity, (EntityRendererManager managerIn) -> new GenericBipedRenderer(managerIn, registryName));

    }
    private void finalSetup(final FMLLoadCompleteEvent event)
    {
        // some postinit code
    }

}
