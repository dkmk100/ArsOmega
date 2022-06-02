package com.dkmk100.arsomega;

import com.dkmk100.arsomega.blocks.ChalkLineBlock;
import com.dkmk100.arsomega.client.renderer.GenericBipedRenderer;
import com.dkmk100.arsomega.client.renderer.PlainRenderer;
import com.dkmk100.arsomega.client.renderer.RaptorRenderer;
import com.dkmk100.arsomega.entities.*;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
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
        ArsOmega.LOGGER.info("Ars Omega Constructor");
        RegistryHandler.registerGlyphs();
        RegistryHandler.registerRituals();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createRegistries);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(RecipeSerializer.class,this::registerSerializers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class,RegistryHandler::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::finalSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::RegisterEntityAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::RegisterRenderers);
        RegistryHandler.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void createRegistries(final NewRegistryEvent event){
        RegistryHandler.registerGlyphConfig();
    }

    private void registerSerializers(final RegistryEvent.Register<RecipeSerializer<?>> event){
        RegistryHandler.RegisterRecipeTypes();
        RegistryHandler.RegisterRecipeSerializers(event);
    }


    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Reflection Handler");
        try {
            ReflectionHandler.Initialize();
        }
        catch (Exception e){
            //let's be honest, this is unrecoverable
            throw new RuntimeException(e);
        }
        RegistryHandler.addAugments();
        event.enqueueWork(() -> {
            RegistryHandler.RegisterFunctions();
            ModPotions.RegisterPotionRecipes();
        });

        //structures?
    }
    private void RegisterEntityAttributes(EntityAttributeCreationEvent event){
        event.put(RegistryHandler.BASIC_DEMON.get(), EntityDemonBasic.createAttributes().build());
        event.put(RegistryHandler.STRONG_DEMON.get(), EntityDemonBasic.createAttributes().build());
        event.put(RegistryHandler.BOSS_DEMON_KING.get(), EntityBossDemonKing.createAttributes().build());
        event.put(RegistryHandler.RAPTOR_DEMON.get(), EntityDemonRaptor.createAttributes().build());
        event.put(RegistryHandler.CLAY_GOLEM.get(), EntityClayGolem.createAttributes().build());
        event.put(RegistryHandler.WITHER_BOUND.get(), WitherBoss.createAttributes().build());
    }
    private void clientSetup(final FMLClientSetupEvent event)
    {
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.POISON_FLOWER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.ARCANE_BLOOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.BRAMBLE_1.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.BRAMBLE_2.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.BRAMBLE_3.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.BRAMBLE_4.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.GORGON_FIRE.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ItemsRegistry.INFINITY_JAR, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.CHALK_LINE_1.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.CHALK_LINE_2.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.CHALK_LINE_3.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryHandler.CHALK_LINE_4.get(), RenderType.cutout());
    }


    private void RegisterRenderers(EntityRenderersEvent.RegisterRenderers event){
        RegisterMobRenderers(event);
    }
    @OnlyIn(Dist.CLIENT)
    private void RegisterMobRenderers(EntityRenderersEvent.RegisterRenderers event){
        RegisterMobRenderer(RegistryHandler.BASIC_DEMON.get(),"demon_basic",event);
        RegisterMobRenderer(RegistryHandler.STRONG_DEMON.get(),"demon_strong",event);
        event.registerEntityRenderer(RegistryHandler.RAPTOR_DEMON.get(), (EntityRendererProvider.Context context) -> new RaptorRenderer(context));
        RegisterMobRenderer(RegistryHandler.BOSS_DEMON_KING.get(),"boss_demon_king",event);
        RegisterMobRenderer(RegistryHandler.CLAY_GOLEM.get(),"clay_golem",event);
        event.registerEntityRenderer(RegistryHandler.WITHER_BOUND.get(), (EntityRendererProvider.Context context) -> new WitherBossRenderer(context));
        event.registerEntityRenderer(RegistryHandler.TORNADO.get(), (EntityRendererProvider.Context context) -> new PlainRenderer(context));
        event.registerEntityRenderer(RegistryHandler.EARTHQUAKE.get(), (EntityRendererProvider.Context context) -> new PlainRenderer(context));
        event.registerEntityRenderer(RegistryHandler.DIVINE_SMITE.get(), (EntityRendererProvider.Context context) -> new LightningBoltRenderer(context));
        event.registerEntityRenderer(RegistryHandler.WHIRLPOOL.get(), (EntityRendererProvider.Context context) -> new PlainRenderer(context));

    }
    @OnlyIn(Dist.CLIENT)
    private void RegisterMobRenderer(EntityType<? extends Mob> entity, String registryName, EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(entity, (EntityRendererProvider.Context context) -> new GenericBipedRenderer(context, registryName));
    }
    private void finalSetup(final FMLLoadCompleteEvent event)
    {
        // some postinit code
    }

    public static CreativeModeTab itemGroup = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), "arsomega") {
        public ItemStack makeIcon() {
            return ItemsRegistry.ALCHEMY_FOCUS.getDefaultInstance();
        }
    };

}
