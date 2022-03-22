package com.dkmk100.arsomega;

import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
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

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::finalSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        RegistryHandler.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Initializing Reflection Handler");
        ReflectionHandler.Initialize();
        // some preinit code
        /*
        biomes.add(RegistryHandler.WITHERED_VOID_BIOME.get());
        biomes.add(RegistryHandler.LUSH_VOID_BIOME.get());

         */

        //GlobalEntityTypeAttributes.put(RegistryHandler.VOID_BOSS.get(),VoidBossEntity.createAttributes().build());
        //GlobalEntityTypeAttributes.put(RegistryHandler.VOID_BEAST.get(), VoidBeastEntity.createAttributes().build());

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
        //RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.VOID_BOSS.get(), VoidBossRenderer::new);
        //RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.VOID_BEAST.get(), VoidBeastRenderer::new);
    }
    private void finalSetup(final FMLLoadCompleteEvent event)
    {
        // some postinit code
    }
    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    /*
    public static final ItemGroup ITEMTAB = new ItemGroup("abyssItems") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegistryHandler.VOIDSTONE_ITEM.get());
        }
    };
     */

}
