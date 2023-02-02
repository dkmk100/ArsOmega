package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.advancement.BasicTrigger;
import com.dkmk100.arsomega.armors.BasicArmorMaterial;
import com.dkmk100.arsomega.base_blocks.BasicBlock;
import com.dkmk100.arsomega.base_blocks.BlockPropertiesCreator;
import com.dkmk100.arsomega.blocks.*;
import com.dkmk100.arsomega.crafting.*;
import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyAPI;
import com.dkmk100.arsomega.empathy_components.GenericEmpathyIngredient;
import com.dkmk100.arsomega.empathy_components.HarmingEmpathyIngredient;
import com.dkmk100.arsomega.empathy_components.HealingEmpathyIngredient;
import com.dkmk100.arsomega.empathy_components.MultiplierIngredient;
import com.dkmk100.arsomega.enchants.ProactiveEnchant;
import com.dkmk100.arsomega.entities.*;
import com.dkmk100.arsomega.glyphs.*;
import com.dkmk100.arsomega.items.*;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.rituals.*;
import com.dkmk100.arsomega.spell_sigils.PetrifySigil;
import com.dkmk100.arsomega.spell_sigils.ScaldSigil;
import com.dkmk100.arsomega.tools.BasicItemTier;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.NotImplementedException;

public class RegistryHandler{
    public static final ResourceLocation DIMTYPE = new ResourceLocation(ArsOmega.MOD_ID, "demon_realm");

    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();
    public static List<AbstractRitual> registeredRituals = new ArrayList<>();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsOmega.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES,ArsOmega.MOD_ID);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ArsOmega.MOD_ID);

    public static final TagKey<Block> GORGON_FIRE_BURNABLES = BlockTags.create(new ResourceLocation("arsomega", "gorgon_fire_burnables"));

    public static final RecipeSerializer<TransmuteRecipe> TRANSMUTE_SERIALIZER = new TransmuteRecipe.Serializer();
    public static final RecipeType<TransmuteRecipe> TRANSMUTE_TYPE = new CustomRecipeType();

    public static final RecipeSerializer<EnchantRecipe> ENCHANT_SERIALIZER = new EnchantRecipe.Serializer();
    public static final RecipeType<EnchantRecipe> ENCHANT_TYPE = new CustomRecipeType();

    public static final RecipeSerializer<SigilRecipe> SIGIL_SERIALIZER = new SigilRecipe.Serializer();
    public static final RecipeType<SigilRecipe> SIGIL_TYPE = new CustomRecipeType();

    public static final RecipeSerializer<WriteProactiveRecipe> WRITE_PROACTIVE_SERIALIZER = new WriteProactiveRecipe.Serializer();
    public static final RecipeType<WriteProactiveRecipe> WRITE_PROACTIVE_TYPE = new CustomRecipeType();

    public static final RecipeSerializer<ProactiveEnchantRecipe> PROACTIVE_ENCHANT_SERIALIZER = new ProactiveEnchantRecipe.Serializer();
    public static final RecipeType<ProactiveEnchantRecipe> PROACTIVE_ENCHANT_TYPE = new CustomRecipeType();

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ArsOmega.MOD_ID);

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ArsOmega.MOD_ID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,ArsOmega.MOD_ID);

    public static final String FIRE_FOCUS_DAMAGE = "hellflare";

    public static void RegisterRecipeTypes(){
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("arsomega", "transmute"), TRANSMUTE_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("arsomega", "enchant"), ENCHANT_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("arsomega", "sigil"), SIGIL_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("arsomega", "write_proactive"), WRITE_PROACTIVE_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("arsomega", "proactive_enchant"), PROACTIVE_ENCHANT_TYPE);
        ArsNouveauAPI.getInstance().getEnchantingRecipeTypes().add(WRITE_PROACTIVE_TYPE);
        ArsNouveauAPI.getInstance().getEnchantingRecipeTypes().add(PROACTIVE_ENCHANT_TYPE);
    }
    public static void RegisterRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event){
        event.getRegistry().register(TRANSMUTE_SERIALIZER.setRegistryName(new ResourceLocation("arsomega", "transmute")));
        event.getRegistry().register(ENCHANT_SERIALIZER.setRegistryName(new ResourceLocation("arsomega", "enchant")));
        event.getRegistry().register(SIGIL_SERIALIZER.setRegistryName(new ResourceLocation("arsomega", "sigil")));
        event.getRegistry().register(WRITE_PROACTIVE_SERIALIZER.setRegistryName(new ResourceLocation("arsomega", "write_proactive")));
        event.getRegistry().register(PROACTIVE_ENCHANT_SERIALIZER.setRegistryName(new ResourceLocation("arsomega", "proactive_enchant")));
    }

    public static BasicTrigger USE_DEMON_STAFF;
    public static BasicTrigger USE_CURSED_BIND;

    public static BasicTrigger RESTORATION;

    public static BasicTrigger CONTACT;
    public static BasicTrigger POWERS;
    public static BasicTrigger DESTINY;

    public static void RegisterAdvancementTriggers(){
        USE_DEMON_STAFF = CriteriaTriggers.register(new BasicTrigger(new ResourceLocation(ArsOmega.MOD_ID,"use_demon_staff")));
        USE_CURSED_BIND = CriteriaTriggers.register(new BasicTrigger(new ResourceLocation(ArsOmega.MOD_ID,"use_cursed_bind")));
        RESTORATION = CriteriaTriggers.register(new BasicTrigger(new ResourceLocation(ArsOmega.MOD_ID,"restoration")));
        CONTACT = CriteriaTriggers.register(new BasicTrigger(new ResourceLocation(ArsOmega.MOD_ID,"contact")));
        POWERS = CriteriaTriggers.register(new BasicTrigger(new ResourceLocation(ArsOmega.MOD_ID,"powers")));
        DESTINY = CriteriaTriggers.register(new BasicTrigger(new ResourceLocation(ArsOmega.MOD_ID,"destiny")));
    }

    public static final RegistryObject<Enchantment> PROACTIVE_ENCHANT = ENCHANTMENTS.register("proactive",ProactiveEnchant::new);

    public static void init (){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILE_ENTITIES.register(bus);
        ENTITIES.register(bus);
        ENCHANTMENTS.register(bus);
        SOUNDS.register(bus);
        //StructureInit.RegisterStructures(bus);
        //ExperimentalStructureInit.RegisterStructures(bus);
    }

    public static final RegistryObject<SoundEvent> HILLS_MUSIC = SOUNDS.register("blocky_hills",
            () -> new SoundEvent(new ResourceLocation(ArsOmega.MOD_ID,"blocky_hills")));

    public static final RegistryObject<SoundEvent> DEMON_DANCE_MUSIC = SOUNDS.register("demon_dance",
            () -> new SoundEvent(new ResourceLocation(ArsOmega.MOD_ID,"demon_dance")));
    public static final RegistryObject<SoundEvent> DEMON_KING_MUSIC = SOUNDS.register("demon_king",
            () -> new SoundEvent(new ResourceLocation(ArsOmega.MOD_ID,"demon_king")));

    public static void registerBlocks(final RegistryEvent.Register<Block> event){
        event.getRegistry().register(new InfinityCrystal(UNBREAKABLE_BLOCK_PROPERTIES,"infinity_crystal"));
    }

    static final List<ConfigurableGlyph> configurableGlyphs = new ArrayList<>();
    static final List<ConfigurableRitual> configurableRituals = new ArrayList<>();


    public static void registerGlyphConfig(){
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve("arsomega"), "arsomega");
        for(ConfigurableGlyph glyph : configurableGlyphs) {
            ForgeConfigSpec.Builder spellBuilder = new ForgeConfigSpec.Builder();
            glyph.buildExtraConfig(spellBuilder);
            ForgeConfigSpec spec = spellBuilder.build();
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, "arsomega/" + glyph.getId() + ".toml");
        }
    }

    public static void registerRitualConfig(){
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve("arsomega"), "arsomega");
        for(ConfigurableRitual ritual : configurableRituals) {
            ArsOmega.LOGGER.info("defining config for: "+ritual.getID());
            ForgeConfigSpec.Builder spellBuilder = new ForgeConfigSpec.Builder();
            ritual.buildConfig(spellBuilder);
            ForgeConfigSpec spec = spellBuilder.build();
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, "arsomega/ritual_" + ritual.getID() + ".toml");
            ArsOmega.LOGGER.info("defined config for: "+ritual.getID());
        }
    }

    public static void registerGlyphs(){

        register(TrueUnderfoot.INSTANCE);
        register(DiamondGlyph.INSTANCE);
        register(AdvancedAmplify.INSTANCE);
        register(WitherSummon.INSTANCE);
        register(Rot.INSTANCE);
        register(Curse.INSTANCE);
        register(DemonicCurseGlyph.INSTANCE);
        register(Heartstop.INSTANCE);
        register(PoisonFlowerGlyph.INSTANCE);
        register(Brambles.INSTANCE);
        register(AdrenalineGlyph.INSTANCE);
        register(Flatten.INSTANCE);
        register(LeadSkin.INSTANCE);
        register(CarveGlyph.INSTANCE);
        register(AnimateGlyph.INSTANCE);
        register(TransmuteGlyph.INSTANCE);
        register(OverheadGlyph.INSTANCE);
        register(PropagateProjectile.INSTANCE);
        register(PropagateSelf.INSTANCE);
        register(SwapTargetGlyph.INSTANCE);
        register(PropagateOrbit.INSTANCE);
        register(EnchantGlyph.INSTANCE);
        register(FormMissile.INSTANCE);
        register(DisenchantGlyph.INSTANCE);
        register(PropagateMissile.INSTANCE);
        register(PropagateOverhead.INSTANCE);
        register(PropagateUnderfoot.INSTANCE);
        register(Melt.INSTANCE);
        register(AcidGlyph.INSTANCE);
        register(Scald.INSTANCE);
        register(Soulfire.INSTANCE);
        register(Hellfire.INSTANCE);
        register(VineGrab.INSTANCE);
        register(Drown.INSTANCE);
        register(DemonicLight.INSTANCE);
        register(Fireball.INSTANCE);
        register(HellFlare.INSTANCE);
        register(CursedBind.INSTANCE);
        register(TornadoGlyph.INSTANCE);
        register(GlyphRaiseEarth.INSTANCE);
        register(AdvancedGrow.INSTANCE);
        register(DivineSmite.INSTANCE);
        register(Earthquake.INSTANCE);
        register(GlyphWhirlpool.INSTANCE);
        register(DilateTime.INSTANCE);
        register(FormProjectileBurst.INSTANCE);
        register(Dispellant.INSTANCE);
        register(Regen.INSTANCE);
        register(DemonicCleanse.INSTANCE);
        register(DemonicAnchoring.INSTANCE);
        register(TemporalCollapse.INSTANCE);
        register(ClotGlyph.INSTANCE);
        register(LesserAOE.INSTANCE);
        register(GreaterAOE.INSTANCE);
        register(Absorption.INSTANCE);
        register(AdvancedEvaporate.INSTANCE);

        register(AugmentRandomizeColor.INSTANCE);
        register(RandomChance.LOW_CHANCE);
        register(RandomChance.MID_CHANCE);
        register(RandomChance.HIGH_CHANCE);
        register(RandomColorEffect.INSTANCE);
    }

    public static void registerRituals()
    {
        register(new RitualPermaFlight());
        register(new RitualTribute());
        register(new RitualBanishment());
        register(new RitualProtection());
        register(new RitualPetrification());
        register(new RitualCleansing());
        register(new RitualSummoning());
        register(new RitualFatigue());
        register(new RitualAura());
        register(new RitualChangeBiome());
        register(new RitualAdvancedOvergrowth());
        register(new RitualConjuring());
        register(new RitualFlowingTime());
        register(new RitualDemonicSummoning());
        register(new RitualOpenPortal());
        register(new RitualDispel());
        register(new RitualShaping());
    }

    public static void registerEmpathyIngredients(){
        register(new HarmingEmpathyIngredient(() -> Items.SPIDER_EYE,2,2));
        register(new HarmingEmpathyIngredient(() -> Items.FERMENTED_SPIDER_EYE,3,2,(a,m) -> new MobEffectInstance(MobEffects.POISON,Math.round(80*m),a-1)));
        register(new HarmingEmpathyIngredient(() -> POISON_FLOWER_ITEM.get(),0,5,(a,m) -> new MobEffectInstance(MobEffects.POISON,150 + Math.round(130* m * a),4)));
        register(new HarmingEmpathyIngredient(() -> Items.WITHER_ROSE,0,(a,m) -> new MobEffectInstance(MobEffects.WITHER,100 + Math.round(100 * a * m),1)));
        register(new HealingEmpathyIngredient(() -> Items.GLISTERING_MELON_SLICE,2,2,(a,m) -> new MobEffectInstance(MobEffects.REGENERATION,60 + Math.round(50 * a*m),0)));
        register(new HealingEmpathyIngredient(() -> Items.MELON_SLICE,1));
        register(new HealingEmpathyIngredient(() -> Items.BREAD,1));
        register(new HealingEmpathyIngredient(() -> ESSENCE_LIFE.get(),2,2,(a,m) -> new MobEffectInstance(MobEffects.REGENERATION,80 + Math.round(100 * m),a)));
        register(new MultiplierIngredient(() -> Items.GHAST_TEAR,0.75f,true,true));
        register(new MultiplierIngredient(() -> Items.GUNPOWDER,0.25f, 3,true,true));
        register(new MultiplierIngredient(() -> Items.COAL,-0.5f, true,true));
        register(new HarmingEmpathyIngredient(() -> Items.ROTTEN_FLESH,3,1));
        register(new HarmingEmpathyIngredient(() -> Items.BONE,3,1));

        HarmingEmpathyIngredient gorgon_gem = (new HarmingEmpathyIngredient(() -> GORGON_GEM.get(),0,10, (a, m) -> {throw new NotImplementedException("");}));
        GenericEmpathyIngredient binding_sigil = (new HarmingEmpathyIngredient(() -> SIGIL_BINDING_ACTIVE.get(),0,1,
                (a,m,s) ->
                {
                    ArsOmega.LOGGER.info("binding sigil curse activated, amount: " + a + ", strength: " + m);
                    if(s.getIngredient(gorgon_gem)!=null){
                        int a2 = s.getIngredient(gorgon_gem).getAmount();
                        if(a2 > 5) {
                            return new MobEffectInstance(ModPotions.STONE_PETRIFICATION, 100, m >= 0.5 ? 1 : 0);
                        }
                        else{
                            return new MobEffectInstance(ModPotions.STONE_PETRIFICATION, 120 + Math.round(50*m*(a+a2)), 0);
                        }
                    }
                    else {
                        return new MobEffectInstance(ModPotions.STONE_PETRIFICATION, 120, 0);
                    }
                }
        ));
        gorgon_gem.setEffect((a,m,s) -> {
            if(s.getIngredient(binding_sigil)!=null){
                return null;
            }
            else {
                return new MobEffectInstance(ModPotions.STONE_PETRIFICATION,80 + Math.round(50*m*a), 0);
            }
        });
        //no longer incompatible, synergy instead!
        register(gorgon_gem);
        register(binding_sigil);
    }

    public static void addAugments(){
        for(AbstractSpellPart part : ArsNouveauAPI.getInstance().getSpellpartMap().values()){
            if(part.compatibleAugments.contains(AugmentAmplify.INSTANCE)&&!part.compatibleAugments.contains(AdvancedAmplify.INSTANCE)){
                part.compatibleAugments.add(AdvancedAmplify.INSTANCE);
            }
            if(part.compatibleAugments.contains(AugmentExtendTime.INSTANCE)&&!part.compatibleAugments.contains(DilateTime.INSTANCE)){
                part.compatibleAugments.add(DilateTime.INSTANCE);
            }
            if(part.compatibleAugments.contains(AugmentDurationDown.INSTANCE)&&!part.compatibleAugments.contains(TemporalCollapse.INSTANCE)){
                part.compatibleAugments.add(TemporalCollapse.INSTANCE);
            }
            if(part.compatibleAugments.contains(AugmentAOE.INSTANCE)&&!part.compatibleAugments.contains(LesserAOE.INSTANCE)){
                part.compatibleAugments.add(LesserAOE.INSTANCE);
            }
            if(part.compatibleAugments.contains(AugmentAOE.INSTANCE)&&!part.compatibleAugments.contains(GreaterAOE.INSTANCE)){
                part.compatibleAugments.add(GreaterAOE.INSTANCE);
            }
            if(part instanceof AbstractCastMethod){
                part.compatibleAugments.add(AugmentRandomizeColor.INSTANCE);
            }
        }
    }

    public static LootItemFunctionType SET_SPELL_TYPE;

    public static void RegisterFunctions(){
        SET_SPELL_TYPE = register("set_spell",new SetSpellFunction.Serializer());
    }

    static LootItemFunctionType register(String p_80763_, Serializer<? extends LootItemFunction> p_80764_) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation("arsomega",p_80763_), new LootItemFunctionType(p_80764_));
    }

    public static void register(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart.getId(), spellPart);
        registeredSpells.add(spellPart);
        if(spellPart instanceof ConfigurableGlyph){
            configurableGlyphs.add((ConfigurableGlyph) spellPart);
        }
    }
    public static void register(AbstractRitual ritual) {
        ArsNouveauAPI.getInstance().registerRitual(ritual.getID(),ritual);
        registeredRituals.add(ritual);
        if(ritual instanceof ConfigurableRitual){
            configurableRituals.add((ConfigurableRitual) ritual);
        }
    }

    static int empReg = 0;

    private static void register(AbstractEmpathyIngredient ingredient) {
        EmpathyAPI.INSTANCE.RegisterIngredient("ingredient_"+empReg, ingredient);
        empReg+=1;
    }



    static final ItemPropertiesCreator itemPropertiesCreator = new ItemPropertiesCreator();
    static final Item.Properties ITEM_PROPERTIES = itemPropertiesCreator.create(ArsOmega.itemGroup,64);

    static final Item.Properties EGG_PROPERTIES = itemPropertiesCreator.create(CreativeModeTab.TAB_MISC,64);

    static final Item.Properties ITEM_PROPERTIES_FIRE = itemPropertiesCreator.create(ArsOmega.itemGroup,64).fireResistant();
    static final Item.Properties UNSTACKABLE_FIRE = itemPropertiesCreator.create(ArsOmega.itemGroup,1).fireResistant();

    public static final FoodProperties ENCHANTED_DIAMOND_APPLE = (new FoodProperties.Builder()).nutrition(5).saturationMod(1.2F).effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 8000, 4),1).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 800, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 8000, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 7000, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 5000, 5), 1.0F).alwaysEat().build();
    public static final FoodProperties ARCANE_APPLE = (new FoodProperties.Builder()).nutrition(7).saturationMod(1.5F).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 8000, 1),1).effect(() -> new MobEffectInstance(ModPotions.LEAD_SKIN, 8000, 1),1).effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 8000, 6),1).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 800, 2), 1.0F).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10000, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9000, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 9000, 8), 1.0F).alwaysEat().build();


    public static void RegisterItems(RegistryEvent.Register<Item> event) {

        event.getRegistry().register(new BlockItem(ItemsRegistry.INFINITY_JAR, ITEM_PROPERTIES).setRegistryName("infinity_crystal"));
        event.getRegistry().register(new HexedItem(UNSTACKABLE_FIRE, "hexed_book", false));
        event.getRegistry().register(new HexedItem(UNSTACKABLE_FIRE, "hexed_writable_book", false));
        event.getRegistry().register(new HexedItem(UNSTACKABLE_FIRE, "hexed_written_book", true));
    }



    //Block Properties
    static final BlockPropertiesCreator blockPropertiesCreator = new BlockPropertiesCreator();
    static final Block.Properties STONE_PROPERTIES = blockPropertiesCreator.create(Material.STONE,3.5f,2f, SoundType.STONE, true);

    static final Block.Properties MAGIC_CHALK_PROPERTIES = blockPropertiesCreator.create(Material.CLAY,1.0f,9f, SoundType.CALCITE, false).noCollission().noOcclusion();;
    static final Block.Properties CHALK_PROPERTIES = blockPropertiesCreator.create(Material.DECORATION,0.2f,2f, SoundType.WOOD, false).noCollission().noOcclusion();;

    static final Block.Properties FLOWER_PROPERTIES = blockPropertiesCreator.create(Material.PLANT,0.01f,0f, SoundType.CROP, false).noCollission().noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES = blockPropertiesCreator.create(Material.PLANT, 2f, 0.5f,  SoundType.HARD_CROP, false).noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES_2 = blockPropertiesCreator.create(Material.PLANT, 4f, 1f,  SoundType.HARD_CROP, false).noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES_3 = blockPropertiesCreator.create(Material.PLANT, 6f, 2f,  SoundType.HARD_CROP, true).noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES_4 = blockPropertiesCreator.create(Material.PLANT, 10f, 4f,  SoundType.HARD_CROP, true).noOcclusion();
    static final BlockBehaviour.Properties CLAY_PROPERTIES = blockPropertiesCreator.create(Material.CLAY, 3f, 1f,  SoundType.GRAVEL, false);
    static final BlockBehaviour.Properties GLASS_PROPERTIES = blockPropertiesCreator.create(Material.GLASS, 0.8f, 4f,  SoundType.GLASS, false).noOcclusion();

    static final BlockBehaviour.Properties WOOL_PROPERTIES = blockPropertiesCreator.create(Material.WOOL, 8f, 800f,  SoundType.WOOL, false);
    static final BlockBehaviour.Properties FIRE_PROPERTIES = blockPropertiesCreator.create(Material.FIRE, 0, 0, SoundType.SAND, false).noOcclusion().lightLevel((BlockState state) -> 15);
    static final Block.Properties GLOW_PROPERTIES = blockPropertiesCreator.create(Material.STONE,1.5f,1f, SoundType.STONE, false).lightLevel((BlockState state) -> 15);

    private static Boolean never(BlockState p_235427_0_, BlockGetter p_235427_1_, BlockPos p_235427_2_, EntityType<?> p_235427_3_) {
        return (boolean)false;
    }
    private static boolean never(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }
    static final Block.Properties UNBREAKABLE_BLOCK_PROPERTIES = blockPropertiesCreator.create(Material.STONE,-1,12000f, SoundType.STONE, true).noDrops().noOcclusion().isValidSpawn(RegistryHandler::never).isRedstoneConductor(RegistryHandler::never).isSuffocating(RegistryHandler::never).isViewBlocking(RegistryHandler::never);

    static final Block.Properties PORTAL_PROPERTIES = blockPropertiesCreator.create(Material.PORTAL,-1,120000f, SoundType.NETHERITE_BLOCK, false).noDrops().noOcclusion();
    public static final RegistryObject<Block> DEMONIC_STONE = BLOCKS.register("demonic_stone",() -> new Block(STONE_PROPERTIES));
    public static final RegistryObject<Block> DEMONIC_ORE = BLOCKS.register("demonic_ore",() -> new OreBlock(STONE_PROPERTIES));

    public static final RegistryObject<Block> DEMONIC_RIFT = BLOCKS.register("demonic_rift",() -> new DemonicRift(STONE_PROPERTIES));
    public static final RegistryObject<Block> GORGON_STONE = BLOCKS.register("gorgon_stone",() -> new Block(STONE_PROPERTIES));
    public static final RegistryObject<Block> GORGON_FIRE = BLOCKS.register("gorgon_fire",() -> new GorgonFire(FIRE_PROPERTIES));
    public static final RegistryObject<Block> DEMONIC_GLOWSTONE = BLOCKS.register("demonic_glowstone",() -> new Block(GLOW_PROPERTIES));
    public static final RegistryObject<Block> FLESH_BLOCK = BLOCKS.register("flesh_block",() -> new Block(STONE_PROPERTIES));

    public static final RegistryObject<Block> INFUSED_GLASS = BLOCKS.register("infused_glass",() -> new GlassBlock(GLASS_PROPERTIES));

    public static final RegistryObject<Block> POTION_EXTENDER = BLOCKS.register("potion_extender",() -> new PotionExtender(STONE_PROPERTIES));
    public static final RegistryObject<Block> POTION_AMPLIFIER = BLOCKS.register("potion_amplifier",() -> new PotionAmplifier(STONE_PROPERTIES));
    public static final RegistryObject<Block> POTION_BOTTLER = BLOCKS.register("potion_bottler",() -> new PotionBottler(STONE_PROPERTIES));

    public static final RegistryObject<Block> CURSE_ALTAR = BLOCKS.register("curse_altar",() -> new CurseAltarBlock(UNBREAKABLE_BLOCK_PROPERTIES));

    public static final RegistryObject<Block> CURSED_EARTH = BLOCKS.register("cursed_earth",() -> new CursedEarth(CLAY_PROPERTIES));
    public static final RegistryObject<Block> VENGEFUL_SOUL_SAND = BLOCKS.register("vengeful_soul_sand",() -> new CursedEarth(CLAY_PROPERTIES));

    static EntityType<? extends Entity> getClayGolem(){
        return CLAY_GOLEM_BETA.get();
    }
    static EntityType<? extends Entity> getMarvelousGolem(){
        return CLAY_GOLEM_MARVELOUS.get();
    }
    static EntityType<? extends Entity> getMysticGolem(){
        return CLAY_GOLEM_MYSTIC.get();
    }
    static EntityType<? extends Entity> getArcaneGolem(){
        return CLAY_GOLEM_ARCANE.get();
    }
    public static final RegistryObject<Block> MAGIC_CLAY_BLOCK = BLOCKS.register("magic_clay_block",() -> new Block(CLAY_PROPERTIES));
    public static final RegistryObject<Block> MAGIC_CLAY_CARVED = BLOCKS.register("magic_clay_carved",() -> new CarvedClay(CLAY_PROPERTIES,false,MAGIC_CLAY_BLOCK.get(), RegistryHandler::getClayGolem));

    public static final RegistryObject<Block> MARVELOUS_CLAY_BLOCK = BLOCKS.register("marvelous_clay_block",() -> new Block(CLAY_PROPERTIES));
    public static final RegistryObject<Block> MARVELOUS_CLAY_CARVED = BLOCKS.register("marvelous_clay_carved",() -> new CarvedClay(CLAY_PROPERTIES,false,MARVELOUS_CLAY_BLOCK.get(), RegistryHandler::getMarvelousGolem));

    public static final RegistryObject<Block> MYSTIC_CLAY_BLOCK = BLOCKS.register("mystic_clay_block",() -> new Block(CLAY_PROPERTIES));
    public static final RegistryObject<Block> MYSTIC_CLAY_CARVED = BLOCKS.register("mystic_clay_carved",() -> new CarvedClay(CLAY_PROPERTIES,false,MYSTIC_CLAY_BLOCK.get(), RegistryHandler::getMysticGolem));

    public static final RegistryObject<Block> ARCANE_CLAY_BLOCK = BLOCKS.register("arcane_clay_block",() -> new Block(CLAY_PROPERTIES));
    public static final RegistryObject<Block> ARCANE_CLAY_CARVED = BLOCKS.register("arcane_clay_carved",() -> new CarvedClay(CLAY_PROPERTIES,false, ARCANE_CLAY_BLOCK.get(), RegistryHandler::getArcaneGolem));


    public static final RegistryObject<Block> ENCHANTERS_WOOL = BLOCKS.register("enchanters_wool",() -> new Block(WOOL_PROPERTIES));
    public static final RegistryObject<Block> ARCANE_BLOOM = BLOCKS.register("arcane_bloom_crop",() -> new ArcaneBloomCrop());

    public static final RegistryObject<Block> POISON_FLOWER = BLOCKS.register("poison_flower",() -> new PoisonFlower(MobEffects.POISON,FLOWER_PROPERTIES));
    public static final RegistryObject<Block> BRAMBLE_1 = BLOCKS.register("bramble_1",() -> new BramblesBlock(BRAMBLE_PROPERTIES,1));
    public static final RegistryObject<Block> BRAMBLE_2 = BLOCKS.register("bramble_2",() -> new BramblesBlock(BRAMBLE_PROPERTIES_2,2));
    public static final RegistryObject<Block> BRAMBLE_3 = BLOCKS.register("bramble_3",() -> new BramblesBlock(BRAMBLE_PROPERTIES_3,3));
    public static final RegistryObject<Block> BRAMBLE_4 = BLOCKS.register("bramble_4",() -> new BramblesBlock(BRAMBLE_PROPERTIES_4,4));

    public static final RegistryObject<Block> INFUSED_DIAMOND_BLOCK = BLOCKS.register("infused_diamond_block",() -> new BasicBlock(STONE_PROPERTIES));
    public static final RegistryObject<Block> ENCHANTED_DIAMOND_BLOCK = BLOCKS.register("enchanted_diamond_block",() -> new BasicBlock(STONE_PROPERTIES));
    public static final RegistryObject<Block> ARCANE_DIAMOND_BLOCK = BLOCKS.register("arcane_diamond_block",() -> new BasicBlock(STONE_PROPERTIES));

    public static final RegistryObject<Block> DEMONIC_GEM_BLOCK = BLOCKS.register("demonic_gem_block",() -> new BasicBlock(STONE_PROPERTIES));
    public static final RegistryObject<Block> ALCHEMICAL_DIAMOND_ORE = BLOCKS.register("alchemical_diamond_ore",() -> new BasicBlock(STONE_PROPERTIES));
    public static final RegistryObject<Block> ALCHEMICAL_DIAMOND_BLOCK = BLOCKS.register("alchemical_diamond_block",() -> new BasicBlock(STONE_PROPERTIES));
    public static final RegistryObject<Block> ALCHEMICAL_STONE = BLOCKS.register("alchemical_stone",() -> new BasicBlock(STONE_PROPERTIES));


    //IDK about the balancing on the charges and costs of these.
    //the colors look pretty good though
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_1 = BLOCKS.register("magic_chalk_line",() ->
            new ChalkLineBlock(MAGIC_CHALK_PROPERTIES,3,1.0f,new ChalkColor(0.3f,0.2f,0.3f,0.5f,0.3f,0.5f)));
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_2 = BLOCKS.register("marvelous_chalk_line",() ->
            new ChalkLineBlock(MAGIC_CHALK_PROPERTIES,6,0.95f,new ChalkColor(0.3f,0.3f,0.15f,0.6f,0.6f,0.3f)));
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_3 = BLOCKS.register("mystic_chalk_line",() ->
            new ChalkLineBlock(MAGIC_CHALK_PROPERTIES,10,0.9f,new ChalkColor(0.1f,0.4f,0.2f,0.1f,0.6f,0.3f)));
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_4 = BLOCKS.register("arcane_chalk_line",() ->
            new ChalkLineBlock(MAGIC_CHALK_PROPERTIES,16,0.8f,new ChalkColor(0.3f,0.03f,0.3f,0.55f,0.05f,0.6f)));

    public static final RegistryObject<BasicChalk> CHALK_BLOCK = BLOCKS.register("chalk",() ->
            new BasicChalk(CHALK_PROPERTIES,new ChalkColor(1.0f,1.0f,1.0f,0f,0f,0f)));

    public static final RegistryObject<Block> POTION_RELAY = BLOCKS.register("potion_relay",() -> new PotionRelay());

    public static final RegistryObject<PortalBlock> PORTAL_BLOCK = BLOCKS.register("portal_block",() -> new PortalBlock(PORTAL_PROPERTIES));
    public static final RegistryObject<MirrorPortalBlock> MIRROR_PORTAL_BLOCK = BLOCKS.register("mirror_portal_block",() -> new MirrorPortalBlock(PORTAL_PROPERTIES));

    public static final RegistryObject<DemonicLightBlock> DEMONIC_LIGHT = BLOCKS.register("demonic_light",() -> new DemonicLightBlock());


    public static RegistryObject<BlockEntityType<DemonicLightTile>> DemonicLightType = TILE_ENTITIES.register("demonic_light_tile",() -> BlockEntityType.Builder.of(DemonicLightTile::new,DEMONIC_LIGHT.get()).build(null));



    public static RegistryObject<BlockEntityType<PotionExtenderTile>> PotionExtenderType = TILE_ENTITIES.register("potion_extender_tile",() -> BlockEntityType.Builder.of(PotionExtenderTile::new,POTION_EXTENDER.get()).build(null));
    public static RegistryObject<BlockEntityType<PotionAmplifierTile>> PotionAmplifierType = TILE_ENTITIES.register("potion_amplifier_tile",() -> BlockEntityType.Builder.of(PotionAmplifierTile::new,POTION_AMPLIFIER.get()).build(null));
    public static RegistryObject<BlockEntityType<BottlerTile>> PotionBottlerType = TILE_ENTITIES.register("potion_bottler_tile",() -> BlockEntityType.Builder.of(BottlerTile::new,POTION_BOTTLER.get()).build(null));

    public static RegistryObject<BlockEntityType<InfinityCrystalTile>> InfinityCrystalType = TILE_ENTITIES.register("infinity_crystal_tile",() -> BlockEntityType.Builder.of(InfinityCrystalTile::new, ItemsRegistry.INFINITY_JAR).build(null));

    public static RegistryObject<BlockEntityType<PotionRelayTile>> PotionRelayType = TILE_ENTITIES.register("potion_relay_tile",() -> BlockEntityType.Builder.of(PotionRelayTile::new, POTION_RELAY.get()).build(null));

    public static RegistryObject<BlockEntityType<ChalkTile>> ChalkTileType = TILE_ENTITIES.register("chalk_tile",() -> BlockEntityType.Builder.of(ChalkTile::new, CHALK_LINE_1.get(),CHALK_LINE_2.get(),CHALK_LINE_3.get(),CHALK_LINE_4.get()).build(null));
    public static RegistryObject<BlockEntityType<PortalBlockEntity>> PortalType = TILE_ENTITIES.register("portal_tile",() -> BlockEntityType.Builder.of(PortalBlockEntity::new, PORTAL_BLOCK.get()).build(null));
    public static RegistryObject<BlockEntityType<MirrorPortalBlockEntity>> MirrorPortalType = TILE_ENTITIES.register("mirror_portal_tile",() -> BlockEntityType.Builder.of(MirrorPortalBlockEntity::new, MIRROR_PORTAL_BLOCK.get()).build(null));

    public static RegistryObject<BlockEntityType<CurseAltarTile>> CurseAltarType = TILE_ENTITIES.register("curse_altar_tile",() -> BlockEntityType.Builder.of(CurseAltarTile::new, CURSE_ALTAR.get()).build(null));




    /*
    Items are here lol
    ITEMS
    =====================================================================================================================================
     */

    public static final RegistryObject<Item> TIER_FOUR_BOOK = ITEMS.register("arcane_book", () -> new SpellBook(TierFourEffect.FOUR));

    public static final RegistryObject<Item> GREATER_MANA_AMULET = ITEMS.register("greater_mana_amulet", () -> new MagicCurio(500,1));
    public static final RegistryObject<Item> GREATER_REGEN_AMULET = ITEMS.register("greater_regen_amulet", () -> new MagicCurio(10,25));
    public static final RegistryObject<Item> FOCUS_OF_MANA = ITEMS.register("focus_of_mana", () -> new MagicCurio(3250,-30));
    public static final RegistryObject<Item> FOCUS_OF_ALCHEMY = ITEMS.register("focus_of_alchemy", () -> new MagicCurio(-50,-4));
    public static final RegistryObject<Item> FOCUS_OF_ADVANCED_ALCHEMY = ITEMS.register("focus_of_advanced_alchemy", () -> new MagicCurio(-300,-13));
    public static final RegistryObject<Item> FOCUS_OF_REGEN = ITEMS.register("focus_of_regen", () -> new MagicCurio(-300,100));
    public static final RegistryObject<Item> RING_REGEN = ITEMS.register("ring_regen", () -> new MagicCurio(0,5,5));
    public static final RegistryObject<Item> RING_BOOST = ITEMS.register("ring_boost", () -> new MagicCurio(100,0,5));
    public static final RegistryObject<Item> RING_ARCANE_DISCOUNT = ITEMS.register("ring_arcane_discount", () -> new MagicCurio(20,1,50));

    public static final RegistryObject<Item> STAFF = ITEMS.register("staff", () -> new Staff(BasicItemTier.Staff,2,-2.4f,2, AugmentAmplify.INSTANCE,2));
    public static final RegistryObject<Item> STAFF_2 = ITEMS.register("archmage_staff", () -> new Staff(BasicItemTier.Staff2,2,-2.4f,3, AdvancedAmplify.INSTANCE,2));
    public static final RegistryObject<Item> STAFF_3 = ITEMS.register("arcane_staff", () -> new Staff(BasicItemTier.Staff3,2,-2.4f,3, AdvancedAmplify.INSTANCE,3,true));

    public static final RegistryObject<Item> ARCANE_HELMET = ITEMS.register("arcane_helmet", () -> new EnchantedArmor(EquipmentSlot.HEAD,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE));
    public static final RegistryObject<Item> ARCANE_CHEST = ITEMS.register("arcane_chestplate", () -> new EnchantedArmor(EquipmentSlot.CHEST,350,12, BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE));
    public static final RegistryObject<Item> ARCANE_LEGGINGS = ITEMS.register("arcane_leggings", () -> new EnchantedArmor(EquipmentSlot.LEGS,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE));
    public static final RegistryObject<Item> ARCANE_BOOTS = ITEMS.register("arcane_boots", () -> new EnchantedArmor(EquipmentSlot.FEET,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE));

    public static final RegistryObject<Item> DEFENSE_HELMET = ITEMS.register("defense_helmet", () -> new EnchantedArmor(EquipmentSlot.HEAD,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE));
    public static final RegistryObject<Item> DEFENSE_CHEST = ITEMS.register("defense_chestplate", () -> new EnchantedArmor(EquipmentSlot.CHEST,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE));
    public static final RegistryObject<Item> DEFENSE_LEGGINGS = ITEMS.register("defense_leggings", () -> new EnchantedArmor(EquipmentSlot.LEGS,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE));
    public static final RegistryObject<Item> DEFENSE_BOOTS = ITEMS.register("defense_boots", () -> new EnchantedArmor(EquipmentSlot.FEET,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE));

    public static final RegistryObject<Item> POISON_FLOWER_ITEM = ITEMS.register("poison_flower", () -> new BasicBlockItem(POISON_FLOWER.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> DEMONIC_STONE_ITEM = ITEMS.register("demonic_stone", () -> new BasicBlockItem(DEMONIC_STONE.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> DEMONIC_ORE_ITEM = ITEMS.register("demonic_ore", () -> new BasicBlockItem(DEMONIC_ORE.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> DEMON_GEM = ITEMS.register("demonic_gem", () -> new BasicItem(ITEM_PROPERTIES_FIRE));
    public static final RegistryObject<Item> CLEANSING_GEM = ITEMS.register("cleansing_gem", () -> new BasicItem(ITEM_PROPERTIES_FIRE));
    public static final RegistryObject<Item> DEMON_CRYSTAL = ITEMS.register("demonic_crystal", () -> new DescribedItem(ITEM_PROPERTIES_FIRE,"Drops from the Demon King boss, who can be summond with a demonic staff."));
    public static final RegistryObject<Item> ARCANE_BLOOM_CROP = ITEMS.register("arcane_bloom_crop", () -> new BasicBlockItem(ARCANE_BLOOM.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> ENCHANTERS_WOOL_ITEM = ITEMS.register("enchanters_wool", () -> new BasicBlockItem(ENCHANTERS_WOOL.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> GORGON_GEM = ITEMS.register("gorgon_gem", () -> new DescribedItem(ITEM_PROPERTIES_FIRE,"Found in mysterious structures."));

    public static final RegistryObject<Item> SPLASH_BOTTLE = ITEMS.register("splash_bottle", () -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> LINGERING_BOTTLE = ITEMS.register("lingering_bottle", () -> new BasicItem(ITEM_PROPERTIES));


    public static final RegistryObject<Item> ANCIENT_MIRROR_SHARD = ITEMS.register("ancient_mirror_shard", () -> new DescribedItem(ITEM_PROPERTIES_FIRE,"A shard from an ancient mirror.",true));

    public static final RegistryObject<Item> SEARING_FLESH = ITEMS.register("searing_flesh", () -> new DescribedItem(ITEM_PROPERTIES_FIRE,"Dropped by demons from the demon realm"));
    public static final RegistryObject<Item> DEMONIC_TOOTH = ITEMS.register("demonic_tooth", () -> new DescribedItem(ITEM_PROPERTIES,"Dropped by demons from the demon realm"));


    public static final RegistryObject<Item> INFUSED_GLASS_ITEM = ITEMS.register("infused_glass", () -> new BasicBlockItem(INFUSED_GLASS.get(),ITEM_PROPERTIES));

    public static final RegistryObject<Item> CURSE_ALTAR_ITEM = ITEMS.register("curse_altar", () -> new BasicBlockItem(CURSE_ALTAR.get(),ITEM_PROPERTIES));

    public static final RegistryObject<Item> REFLECTIVE_COATING = ITEMS.register("reflective_coating",() -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ENCHANTED_MIRROR_SHARD = ITEMS.register("enchanted_mirror_shard", () -> new BasicItem(ITEM_PROPERTIES, true));

    public static final RegistryObject<Item> ENCHANTERS_CLOAK  = ITEMS.register("enchanters_cloak", () -> new BasicItem(ITEM_PROPERTIES, true));
    public static final RegistryObject<Item> STABILITY_CLOAK = ITEMS.register("stability_cloak", () ->new BasicItem(ITEM_PROPERTIES, true));

    public static final RegistryObject<Item> ARCANE_BLOOM_ITEM = ITEMS.register("arcane_bloom", () -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ARCANE_FIBER = ITEMS.register("arcane_fiber", () -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ARCANE_CLOTH = ITEMS.register("arcane_cloth", () -> new BasicItem(ITEM_PROPERTIES_FIRE));
    public static final RegistryObject<Item> MAGIC_CLAY = ITEMS.register("magic_clay", () -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> MARVELOUS_CLAY = ITEMS.register("marvelous_clay", () -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> MYSTIC_CLAY = ITEMS.register("mystic_clay", () -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ARCANE_CLAY = ITEMS.register("arcane_clay", () -> new BasicItem(ITEM_PROPERTIES));

    public static final RegistryObject<Item> ESSENCE_ARCANE = ITEMS.register("arcane_essence", () -> new EssenceItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ESSENCE_LIFE = ITEMS.register("life_essence", () -> new EssenceItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ESSENCE_ALCHEMY = ITEMS.register("alchemy_essence", () -> new EssenceItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ESSENCE_NATURE = ITEMS.register("nature_essence", () -> new EssenceItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ESSENCE_DEMONIC = ITEMS.register("demonic_essence", () -> new EssenceItem(ITEM_PROPERTIES));

    public static final RegistryObject<Item> BIOME_CRYSTAL = ITEMS.register("biome_crystal", () ->  new BiomeCrystal(ITEM_PROPERTIES));

    public static final RegistryObject<Item> DIMENSION_CRYSTAL = ITEMS.register("dimension_crystal", () -> new DimensionCrystal(ITEM_PROPERTIES));

    public static final RegistryObject<Item> INFUSED_DIAMOND = ITEMS.register("infused_diamond", () -> new BasicItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ENCHANTED_DIAMOND = ITEMS.register("enchanted_diamond", () -> new BasicItem(ITEM_PROPERTIES,true));
    public static final RegistryObject<Item> ARCANE_DIAMOND = ITEMS.register("arcane_diamond", () -> new BasicItem(ITEM_PROPERTIES_FIRE,true));
    public static final RegistryObject<Item> ENCHANTED_DEMONIC_GEM = ITEMS.register("enchanted_demonic_gem", () -> new BasicItem(ITEM_PROPERTIES_FIRE,true));
    public static final RegistryObject<Item> DEMONIC_GEM_BLOCK_ITEM = ITEMS.register("demonic_gem_block", () -> new BasicBlockItem(DEMONIC_GEM_BLOCK.get(),ITEM_PROPERTIES_FIRE));

    public static final RegistryObject<Item> INFUSED_DIAMOND_BLOCK_ITEM = ITEMS.register("infused_diamond_block", () -> new BasicBlockItem(INFUSED_DIAMOND_BLOCK.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> ENCHANTED_DIAMOND_BLOCK_ITEM =  ITEMS.register("enchanted_diamond_block", () -> new BasicBlockItem(ENCHANTED_DIAMOND_BLOCK.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> ARCANE_DIAMOND_BLOCK_ITEM = ITEMS.register("arcane_diamond_block", () -> new BasicBlockItem(ARCANE_DIAMOND_BLOCK.get(),ITEM_PROPERTIES));

    public static final RegistryObject<Item> ROPE = ITEMS.register("rope", () -> new BasicItem(ITEM_PROPERTIES_FIRE));
    public static final RegistryObject<Item> MAGIC_ROPE = ITEMS.register("magic_rope", () -> new BasicItem(ITEM_PROPERTIES_FIRE));
    public static final RegistryObject<Item> ENCHANTED_ROPE = ITEMS.register("enchanted_rope", () -> new BasicItem(ITEM_PROPERTIES_FIRE));


    public static final RegistryObject<Item> ESSENCE_ELEMENTAL_BASIC = ITEMS.register("basic_elemental_essence", () -> new EssenceItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ESSENCE_ELEMENTAL_ADVANCED = ITEMS.register("advanced_elemental_essence", () -> new EssenceItem(ITEM_PROPERTIES));

    //public static final RegistryObject<Item> INFINITY_CRYSTAL_ITEM = ITEMS.register("infinity_crystal", () -> new BasicBlockItem(INFINITY_CRYSTAL.get(), ITEM_PROPERTIES));
    public static final RegistryObject<Item> FLESH_BLOCK_ITEM = ITEMS.register("flesh_block", () -> new BasicBlockItem(FLESH_BLOCK.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> DEMONIC_RIFT_ITEM = ITEMS.register("demonic_rift", () -> new BasicBlockItem(DEMONIC_RIFT.get(),ITEM_PROPERTIES));

    public static final RegistryObject<Item> MAGIC_CLAY_BLOCK_ITEM = ITEMS.register("magic_clay_block", () -> new BasicBlockItem(MAGIC_CLAY_BLOCK.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> MAGIC_CLAY_CARVED_ITEM = ITEMS.register("magic_clay_carved", () -> new BasicBlockItem(MAGIC_CLAY_CARVED.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> GORGON_STONE_ITEM = ITEMS.register("gorgon_stone", () -> new BasicBlockItem(GORGON_STONE.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> DEMONIC_GLOWSTONE_ITEM = ITEMS.register("demonic_glowstone", () -> new BasicBlockItem(DEMONIC_GLOWSTONE.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> DEMONIC_GLOWSTONE_DUST = ITEMS.register("demonic_glowstone_dust", () -> new BasicItem(ITEM_PROPERTIES));

    //event.register(new ResourceLocation("minecraft","hexed_book"), new HexedItem(UNSTACKABLE_FIRE,"",false));
    //event.register(new ResourceLocation("minecraft","hexed_book"), new HexedItem(UNSTACKABLE_FIRE,"hexed_writable_book",false));
    //event.register(new ResourceLocation("minecraft","hexed_book"), new HexedItem(UNSTACKABLE_FIRE,"hexed_written_book",true));

    public static final RegistryObject<Item> POTION_EXTENDER_ITEM = ITEMS.register("potion_extender", () -> new BasicBlockItem(POTION_EXTENDER.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> POTION_AMPLIFIER_ITEM = ITEMS.register("potion_amplifier", () -> new BasicBlockItem(POTION_AMPLIFIER.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> POTION_RELAY_ITEM = ITEMS.register("potion_relay", () -> new BasicBlockItem(POTION_RELAY.get(),ITEM_PROPERTIES));

    //public static final RegistryObject<Item> CHALK_LINE_ITEM = new BlockItem(CHALK_LINE_1.get(),ITEM_PROPERTIES).setRegistryName("chalk_line");

    public static final RegistryObject<Item> DEMON_STAFF = ITEMS.register("demon_staff", () -> new DemonStaff(UNSTACKABLE_FIRE));

    //magic chalk items
    public static final RegistryObject<Item> MAGIC_CHALK = ITEMS.register("magic_chalk", () -> new MagicChalk(() -> CHALK_LINE_1.get(),16));
    public static final RegistryObject<Item> MARVELOUS_CHALK = ITEMS.register("marvelous_chalk", () -> new MagicChalk(() -> CHALK_LINE_2.get(),32));
    public static final RegistryObject<Item> MYSTIC_CHALK = ITEMS.register("mystic_chalk", () -> new MagicChalk(() -> CHALK_LINE_3.get(),128));
    public static final RegistryObject<Item> ARCANE_CHALK = ITEMS.register("arcane_chalk", () -> new MagicChalk(() -> CHALK_LINE_4.get(),1024));

    //chalk for crafting stuff, currently doesn't do much on its own.
    public static final RegistryObject<Item> CHALK_ITEM =  ITEMS.register("chalk", () -> new BasicChalkItem(() -> CHALK_BLOCK.get(), ITEM_PROPERTIES,"A crafting ingredient used for crafting magic chalk. \nCan also be used in a ritual of shaping to craft sigils."));

    public static final RegistryObject<Item> ALCHEMICAL_DIAMOND = ITEMS.register("alchemical_diamond", () ->  new DescribedItem(ITEM_PROPERTIES,"A diamond created using arcane alchemical processes. \nPhysically, it is a perfect gem, but it cannot withstand strong magical energies, and thus cannot be used to craft many advanced magical items."));

    public static final RegistryObject<Item> ALCHEMICAL_DIAMOND_ORE_ITEM = ITEMS.register("alchemical_diamond_ore", () ->  new BasicBlockItem(ALCHEMICAL_DIAMOND_ORE.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> ALCHEMICAL_DIAMOND_BLOCK_ITEM = ITEMS.register("alchemical_diamond_block", () ->  new BasicBlockItem(ALCHEMICAL_DIAMOND_BLOCK.get(),ITEM_PROPERTIES));
    public static final RegistryObject<Item> ALCHEMICAL_STONE_ITEM = ITEMS.register("alchemical_stone", () -> new BasicBlockItem(ALCHEMICAL_STONE.get(),ITEM_PROPERTIES));

    public static final RegistryObject<Item> SIGIL_WATER = ITEMS.register("sigil_water", () -> new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SIGIL_FIRE = ITEMS.register("sigil_fire", () -> new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SIGIL_EARTH = ITEMS.register("sigil_earth", () -> new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SIGIL_AIR = ITEMS.register("sigil_air", () -> new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SIGIL_BINDING = ITEMS.register("sigil_binding", () -> new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item>  SIGIL_WATER_ACTIVE = ITEMS.register("sigil_water_active", () -> new SigilItem(ITEM_PROPERTIES,false,true));
    public static final RegistryObject<Item> SIGIL_FIRE_ACTIVE = ITEMS.register("sigil_fire_active", () -> new SigilItem(ITEM_PROPERTIES,false,true));
    public static final RegistryObject<Item> SIGIL_EARTH_ACTIVE = ITEMS.register("sigil_earth_active", () -> new SigilItem(ITEM_PROPERTIES,false,true));
    public static final RegistryObject<Item> SIGIL_AIR_ACTIVE = ITEMS.register("sigil_air_active", () -> new SigilItem(ITEM_PROPERTIES,false,true));
    public static final RegistryObject<Item> SIGIL_BINDING_ACTIVE = ITEMS.register("sigil_binding_active", () ->  new SigilItem(ITEM_PROPERTIES,false,true));
    public static final RegistryObject<Item> SIGIL_ALCHEMY = ITEMS.register("sigil_alchemy", () ->  new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SIGIL_LIFE = ITEMS.register("sigil_life", () ->  new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SIGIL_NATURE = ITEMS.register("sigil_nature", () ->  new SigilItem(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SIGIL_ALCHEMY_ACTIVE = ITEMS.register("sigil_alchemy_active", () ->  new SigilItem(ITEM_PROPERTIES,false,true));
    public static final RegistryObject<Item> SIGIL_LIFE_ACTIVE = ITEMS.register("sigil_life_active", () ->  new SigilItem(ITEM_PROPERTIES,false,true));
    public static final RegistryObject<Item> SIGIL_NATURE_ACTIVE = ITEMS.register("sigil_nature_active", () ->  new SigilItem(ITEM_PROPERTIES,false,true));

    public static final RegistryObject<Item> SPELL_SIGIL_SCALD = ITEMS.register("spell_sigil_scald", () ->new ScaldSigil());
    public static final RegistryObject<Item> SPELL_SIGIL_PETRIFY = ITEMS.register("spell_sigil_petrify", () ->new PetrifySigil());
    //public static final RegistryObject<Item>  = (new DescribedItem("spell_sigil_smite", ITEM_PROPERTIES,"This spell sigil currently has no functionality, it will be implemented in another beta for the update."));


    public static final RegistryObject<Item> IRON_NEEDLE = ITEMS.register("iron_needle", () ->new ItemPlayerStorage(UNSTACKABLE_FIRE));

    public static final RegistryObject<Item> ENCHANTED_DIAMOND_APPLE_ITEM = ITEMS.register("enchanted_diamond_apple", () ->new BasicItem((new Item.Properties()).tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.EPIC).food(ENCHANTED_DIAMOND_APPLE),true));
    public static final RegistryObject<Item> ARCANE_APPLE_ITEM = ITEMS.register("arcane_apple", () ->new BasicItem((new Item.Properties()).tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.EPIC).food(ARCANE_APPLE),true));


    public static final RegistryObject<Item> CURSED_PENDANT = ITEMS.register("cursed_pendant", () -> new CursedPendant(1,243543,1));
    public static final RegistryObject<Item> CURSED_PENDANT_STRONG = ITEMS.register("cursed_pendant_strong", () -> new CursedPendant(1,243543,2));
    public static final RegistryObject<Item> CURSED_PENDANT_DOUBLE = ITEMS.register("cursed_pendant_double", () -> new CursedPendant(2,243543,1));
    public static final RegistryObject<Item> CURSED_PENDANT_DOUBLE_STRONG = ITEMS.register("cursed_pendant_double_strong", () -> new CursedPendant(2,243543,2));
    public static final RegistryObject<Item> CURSED_PENDANT_ULTIMATE = ITEMS.register("cursed_pendant_ultimate", () -> new CursedPendant(5,243543,3));

    public static final RegistryObject<Item> ARCANE_COMPENDIUM = ITEMS.register("arcane_compendium", () ->new DescribedItem(ITEM_PROPERTIES,"The Arcane Library's collection of Arcane Magics."));
    public static final RegistryObject<Item> MARIA_ROSA = ITEMS.register("maria_rosa", () ->new DescribedItem(ITEM_PROPERTIES,"A damaged hand-written notebook..."));
    public static final RegistryObject<Item> KAZ_CARTER = ITEMS.register("kaz_carter", () ->new DescribedItem(ITEM_PROPERTIES,"A scorched collection of notebook pages..."));

    public static final RegistryObject<Item> SALT = ITEMS.register("salt", () ->new DescribedItem(ITEM_PROPERTIES,"An item used in crafting."));

    public static final RegistryObject<Item> CELESTIAL_STAFF = ITEMS.register("celestial_staff", () ->new CelestialStaff(ITEM_PROPERTIES_FIRE));



    public static final RegistryObject<Item> POTION_BOTTLER_ITEM = ITEMS.register("potion_bottler", () -> new BasicBlockItem(POTION_BOTTLER.get(),ITEM_PROPERTIES));


    public static final RegistryObject<Item> BLOCKY_HILLS_DISC = ITEMS.register("blocky_hills_music_disc", () -> new RecordItem(7,HILLS_MUSIC,new Item.Properties().stacksTo(1).rarity(Rarity.RARE).tab(CreativeModeTab.TAB_MISC)));


    public static final RegistryObject<Item> MARVELOUS_CLAY_BLOCK_ITEM = ITEMS.register("marvelous_clay_block", () ->(new BasicBlockItem(MARVELOUS_CLAY_BLOCK.get(),ITEM_PROPERTIES)));
    public static final RegistryObject<Item> MARVELOUS_CLAY_CARVED_BLOCK_ITEM = ITEMS.register("marvelous_clay_carved", () ->(new BasicBlockItem(MARVELOUS_CLAY_CARVED.get(),ITEM_PROPERTIES)));
    public static final RegistryObject<Item> MYSTIC_CLAY_BLOCK_ITEM = ITEMS.register("mystic_clay_block", () ->(new BasicBlockItem(MYSTIC_CLAY_BLOCK.get(),ITEM_PROPERTIES)));
    public static final RegistryObject<Item> MYSTIC_CLAY_CARVED_BLOCK_ITEM = ITEMS.register("mystic_clay_carved", () ->(new BasicBlockItem(MYSTIC_CLAY_CARVED.get(),ITEM_PROPERTIES)));
    public static final RegistryObject<Item> ARCANE_CLAY_BLOCK_ITEM  = ITEMS.register("arcane_clay_block", () ->(new BasicBlockItem(ARCANE_CLAY_BLOCK.get(),ITEM_PROPERTIES)));
    public static final RegistryObject<Item> ARCANE_CLAY_CARVED_BLOCK_ITEM  = ITEMS.register("arcane_clay_carved", () ->(new BasicBlockItem(ARCANE_CLAY_CARVED.get(),ITEM_PROPERTIES)));


    public static final RegistryObject<Item> DEMON_DANCE_DISC = ITEMS.register("demon_dance_music_disc", () -> new RecordItem(8,DEMON_DANCE_MUSIC,new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.RARE).tab(CreativeModeTab.TAB_MISC)));






    public static final RegistryObject<EntityType<? extends Monster>> BASIC_DEMON = ENTITIES.register("demon_basic", () -> EntityType.Builder.of(EntityDemonBasic::new, MobCategory.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_basic").toString()));
    public static final RegistryObject<EntityType<?  extends Monster>> STRONG_DEMON = ENTITIES.register("demon_strong", () -> EntityType.Builder.of(EntityDemonStrong::new, MobCategory.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_strong").toString()));
    public static final RegistryObject<EntityType<?  extends Monster>> BOSS_DEMON_KING = ENTITIES.register("boss_demon_king", () -> EntityType.Builder.of(EntityBossDemonKing::new, MobCategory.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "boss_demon_king").toString()));

    public static final RegistryObject<EntityType<?  extends Monster>> RAPTOR_DEMON = ENTITIES.register("demon_raptor", () -> EntityType.Builder.of(EntityDemonRaptor::new, MobCategory.MONSTER).sized(0.6F, 1.4F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_raptor").toString()));
    public static final RegistryObject<EntityType<? extends Mob>> RAY_DEMON = ENTITIES.register("demon_ray", () -> EntityType.Builder.of(EntityDemonRay::new, MobCategory.AMBIENT).sized(1.5F, 1.1F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_ray").toString()));

    public static final RegistryObject<EntityType<? extends EntityClayGolem>> CLAY_GOLEM_BETA = ENTITIES.register("clay_golem", () -> EntityType.Builder.<EntityClayGolem>of((e,l) -> new EntityClayGolem(e,l, EntityClayGolem.Tier.MAGIC), MobCategory.MISC).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "clay_golem").toString()));
    public static final RegistryObject<EntityType<? extends EntityClayGolem>> CLAY_GOLEM_MARVELOUS = ENTITIES.register("clay_golem_marvelous", () -> EntityType.Builder.<EntityClayGolem>of((e,l) -> new EntityClayGolem(e,l, EntityClayGolem.Tier.MARVELOUS), MobCategory.MISC).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "clay_golem_marvelous").toString()));
    public static final RegistryObject<EntityType<? extends EntityClayGolem>> CLAY_GOLEM_MYSTIC = ENTITIES.register("clay_golem_mystic", () -> EntityType.Builder.<EntityClayGolem>of((e,l) -> new EntityClayGolem(e,l, EntityClayGolem.Tier.MYSTIC), MobCategory.MISC).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "clay_golem_mystic").toString()));
    public static final RegistryObject<EntityType<? extends EntityClayGolem>> CLAY_GOLEM_ARCANE = ENTITIES.register("clay_golem_arcane", () -> EntityType.Builder.<EntityClayGolem>of((e,l) -> new EntityClayGolem(e,l, EntityClayGolem.Tier.ARCANE), MobCategory.MISC).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "clay_golem_arcane").toString()));

    public static final RegistryObject<EntityType<? extends EntityTornado>> TORNADO = ENTITIES.register("tornado", () -> EntityType.Builder.<EntityTornado>of(EntityTornado::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(20).build(new ResourceLocation(ArsOmega.MOD_ID, "tornado").toString()));

    public static final RegistryObject<EntityType<? extends EntityWhirlpool>> WHIRLPOOL = ENTITIES.register("whirlpool", () -> EntityType.Builder.<EntityWhirlpool>of(EntityWhirlpool::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(20).build(new ResourceLocation(ArsOmega.MOD_ID, "whirlpool").toString()));

    public static final RegistryObject<EntityType<? extends EntityEarthquake>> EARTHQUAKE = ENTITIES.register("earthquake", () -> EntityType.Builder.<EntityEarthquake>of(EntityEarthquake::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(20).build(new ResourceLocation(ArsOmega.MOD_ID, "earthquake").toString()));


    public static final RegistryObject<EntityType<? extends EntityDivineSmite>> DIVINE_SMITE = ENTITIES.register("divine_smite", () -> EntityType.Builder.<EntityDivineSmite>of(EntityDivineSmite::new, MobCategory.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build(new ResourceLocation(ArsOmega.MOD_ID, "divine_smite").toString()));

    public static final RegistryObject<EntityType<EntityWitherBound>> WITHER_BOUND = ENTITIES.register("bound_wither", () -> EntityType.Builder.<EntityWitherBound>of(EntityWitherBound::new, MobCategory.MISC).sized(0.9F, 3.0F).build(new ResourceLocation(ArsOmega.MOD_ID, "bound_wither").toString()));



    public static final RegistryObject<Item> DEMON_SPAWN_EGG = ITEMS.register("basic_demon_spawn_egg", () -> new ModSpawnEggItem(BASIC_DEMON,0x000000,0x000000,EGG_PROPERTIES));
    public static final RegistryObject<Item> DEMON_STRONG_SPAWN_EGG = ITEMS.register("strong_demon_spawn_egg", () -> new ModSpawnEggItem(STRONG_DEMON,0x000000,0x000000,EGG_PROPERTIES));
    public static final RegistryObject<Item> DEMON_RAPTOR_SPAWN_EGG = ITEMS.register("demon_raptor_spawn_egg", () -> new ModSpawnEggItem(RAPTOR_DEMON,0x000000,0x000000,EGG_PROPERTIES));



    public static void RegisterMobSpawns(){
        SpawnPlacements.register(BASIC_DEMON.get(),SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, EntityDemonBasic::canSpawn);
        SpawnPlacements.register(STRONG_DEMON.get(),SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, EntityDemonBasic::canSpawn);
        SpawnPlacements.register(BOSS_DEMON_KING.get(),SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, EntityDemonBasic::canSpawn);
        SpawnPlacements.register(RAPTOR_DEMON.get(),SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, EntityDemonBasic::canSpawn);
    }
}