package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.armors.BasicArmorMaterial;
import com.dkmk100.arsomega.base_blocks.BasicBlock;
import com.dkmk100.arsomega.base_blocks.BlockPropertiesCreator;
import com.dkmk100.arsomega.blocks.*;
import com.dkmk100.arsomega.crafting.CustomRecipeType;
import com.dkmk100.arsomega.crafting.EnchantRecipe;
import com.dkmk100.arsomega.crafting.TransmuteRecipe;
import com.dkmk100.arsomega.entities.*;
import com.dkmk100.arsomega.glyphs.*;
import com.dkmk100.arsomega.items.*;
import com.dkmk100.arsomega.rituals.*;
import com.dkmk100.arsomega.tools.BasicItemTier;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.RegistryHelper;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
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
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

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

    public static final String FIRE_FOCUS_DAMAGE = "hellflare";

    public static void RegisterRecipeTypes(){
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("arsomega", "transmute"), TRANSMUTE_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("arsomega", "enchant"), ENCHANT_TYPE);

    }
    public static void RegisterRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event){
        event.getRegistry().register(TRANSMUTE_SERIALIZER.setRegistryName(new ResourceLocation("arsomega", "transmute")));
        event.getRegistry().register(ENCHANT_SERIALIZER.setRegistryName(new ResourceLocation("arsomega", "enchant")));

    }

    public static void init (){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        TILE_ENTITIES.register(bus);
        ENTITIES.register(bus);
        //StructureInit.RegisterStructures(bus);
        //ExperimentalStructureInit.RegisterStructures(bus);
    }

    public static void registerBlocks(final RegistryEvent.Register<Block> event){
        event.getRegistry().register(new InfinityCrystal(UNBREAKABLE_BLOCK_PROPERTIES,"infinity_crystal"));
    }

    static final List<ConfigurableGlyph> configurableGlyphs = new ArrayList<>();

    public static void registerGlyphConfig(){
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve("arsomega"), "arsomega");
        for(ConfigurableGlyph glyph : configurableGlyphs) {
            ForgeConfigSpec.Builder spellBuilder = new ForgeConfigSpec.Builder();
            glyph.buildExtraConfig(spellBuilder);
            ForgeConfigSpec spec = spellBuilder.build();
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, "arsomega/" + glyph.getId() + ".toml");
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
    }



    static final ItemPropertiesCreator itemPropertiesCreator = new ItemPropertiesCreator();
    static final Item.Properties ITEM_PROPERTIES = itemPropertiesCreator.create(ArsOmega.itemGroup,64);

    static final Item.Properties EGG_PROPERTIES = itemPropertiesCreator.create(CreativeModeTab.TAB_MISC,64);

    static final Item.Properties ITEM_PROPERTIES_FIRE = itemPropertiesCreator.create(ArsOmega.itemGroup,64).fireResistant();
    static final Item.Properties UNSTACKABLE_FIRE = itemPropertiesCreator.create(ArsOmega.itemGroup,1).fireResistant();


    public static final List<Item> ITEMS = new ArrayList<>();
    public static void RegisterItems(RegistryEvent.Register<Item> event){
        final Item GREATER_MANA_AMULET = new MagicCurio("greater_mana_amulet",500,1);
        final Item GREATER_REGEN_AMULET = new MagicCurio("greater_regen_amulet",10,25);
        final Item FOCUS_OF_MANA = new MagicCurio("focus_of_mana",3000,-35);
        final Item FOCUS_OF_ALCHEMY = new MagicCurio("focus_of_alchemy",-50,-5);
        final Item FOCUS_OF_ALCHEMY_2 = new MagicCurio("focus_of_advanced_alchemy",-200,-12);
        final Item FOCUS_OF_REGEN = new MagicCurio("focus_of_regen",-250,100);
        final Item RING_REGEN = new MagicCurio("ring_regen",0,5,5);
        final Item RING_BOOST = new MagicCurio("ring_boost",100,0,5);
        final Item RING_ARCANE_DISCOUNT = new MagicCurio("ring_arcane_discount",20,1,50);

        final Item STAFF = new Staff("staff", BasicItemTier.Staff,2,-2.4f,2, AugmentAmplify.INSTANCE,2);
        final Item STAFF_2 = new Staff("archmage_staff", BasicItemTier.Staff2,2,-2.4f,3, AdvancedAmplify.INSTANCE,2);
        final Item STAFF_3 = new Staff("arcane_staff", BasicItemTier.Staff3,2,-2.4f,3, AdvancedAmplify.INSTANCE,3,true);
        final Item SPELLBOOK_FOUR = new SpellBook(TierFourEffect.FOUR).setRegistryName("arcane_book");
        final Item POISON_FLOWER_ITEM = new BlockItem(POISON_FLOWER.get(),ITEM_PROPERTIES).setRegistryName("poison_flower");
        final Item DEMONIC_STONE_ITEM = new BlockItem(DEMONIC_STONE.get(),ITEM_PROPERTIES).setRegistryName("demonic_stone");
        final Item DEMONIC_ORE_ITEM = new BlockItem(DEMONIC_ORE.get(),ITEM_PROPERTIES).setRegistryName("demonic_ore");
        final Item DEMON_GEM = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("demonic_gem");
        final Item CLEANSING_GEM = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("cleansing_gem");
        final Item DEMON_CRYSTAL = new DescribedItem("demonic_crystal",ITEM_PROPERTIES_FIRE,"Drops from the Demon King boss, who can be summond with a demonic staff.");
        final Item ARCANE_BLOOM_CROP = new BlockItem(ARCANE_BLOOM.get(),ITEM_PROPERTIES).setRegistryName("arcane_bloom_crop");
        final Item ENCHANTERS_WOOL_ITEM = new BlockItem(ENCHANTERS_WOOL.get(),ITEM_PROPERTIES).setRegistryName("enchanters_wool");
        final Item GORGON_GEM = new DescribedItem("gorgon_gem",ITEM_PROPERTIES_FIRE,"Found in gorgon caves");

        final Item ARCANE_BLOOM = new Item(ITEM_PROPERTIES).setRegistryName("arcane_bloom");
        final Item ARCANE_FIBER = new Item(ITEM_PROPERTIES).setRegistryName("arcane_fiber");
        final Item ARCANE_CLOTH = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("arcane_cloth");
        final Item MAGIC_CLAY = new Item(ITEM_PROPERTIES).setRegistryName("magic_clay");
        final Item MARVELOUS_CLAY = new Item(ITEM_PROPERTIES).setRegistryName("marvelous_clay");
        final Item MYSTIC_CLAY = new Item(ITEM_PROPERTIES).setRegistryName("mystic_clay");
        final Item ARCANE_CLAY = new Item(ITEM_PROPERTIES).setRegistryName("arcane_clay");


        final Item INFUSED_DIAMOND = new BasicItem(ITEM_PROPERTIES,"infused_diamond");
        final Item ENCHANTED_DIAMOND = new BasicItem(ITEM_PROPERTIES,"enchanted_diamond",true);
        final Item ARCANE_DIAMOND = new BasicItem(ITEM_PROPERTIES_FIRE,"arcane_diamond",true);
        final Item ENCHANTED_DEMONIC_GEM = new BasicItem(ITEM_PROPERTIES_FIRE,"enchanted_demonic_gem",true);
        final Item SEARING_FLESH = new DescribedItem("searing_flesh",ITEM_PROPERTIES_FIRE,"Dropped by demons from the demon realm");
        final Item DEMONIC_TOOTH = new DescribedItem("demonic_tooth",ITEM_PROPERTIES,"Dropped by demons from the demon realm");
        final Item DEMONIC_GEM_BLOCK_ITEM = new BlockItem(DEMONIC_GEM_BLOCK.get(),ITEM_PROPERTIES_FIRE).setRegistryName("demonic_gem_block");

        final Item INFUSED_DIAMOND_BLOCK_ITEM = new BlockItem(INFUSED_DIAMOND_BLOCK.get(),ITEM_PROPERTIES).setRegistryName("infused_diamond_block");
        final Item ENCHANTED_DIAMOND_BLOCK_ITEM =  new BlockItem(ENCHANTED_DIAMOND_BLOCK.get(),ITEM_PROPERTIES).setRegistryName("enchanted_diamond_block");
        final Item ARCANE_DIAMOND_BLOCK_ITEM = new BlockItem(ARCANE_DIAMOND_BLOCK.get(),ITEM_PROPERTIES).setRegistryName("arcane_diamond_block");

        final Item ROPE = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("rope");
        final Item MAGIC_ROPE = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("magic_rope");
        final Item ENCHANTED_ROPE = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("enchanted_rope");

        final Item ESSENCE_ARCANE = new EssenceItem(ITEM_PROPERTIES,"arcane_essence");
        final Item ESSENCE_LIFE = new EssenceItem(ITEM_PROPERTIES,"life_essence");
        final Item ESSENCE_ALCHEMY = new EssenceItem(ITEM_PROPERTIES,"alchemy_essence");
        final Item ESSENCE_NATURE = new EssenceItem(ITEM_PROPERTIES,"nature_essence");
        final Item ESSENCE_DEMONIC = new EssenceItem(ITEM_PROPERTIES,"demonic_essence");
        final Item ESSENCE_ELEMENTAL_BASIC = new EssenceItem(ITEM_PROPERTIES,"basic_elemental_essence");
        final Item ESSENCE_ELEMENTAL_ADVANCED = new EssenceItem(ITEM_PROPERTIES,"advanced_elemental_essence");

        final Item INFINITY_CRYSTAL_BLOCK = new BlockItem(ItemsRegistry.INFINITY_JAR,ITEM_PROPERTIES).setRegistryName("infinity_crystal");
        final Item FLESH_BLOCK_ITEM = new BlockItem(FLESH_BLOCK.get(),ITEM_PROPERTIES).setRegistryName("flesh_block");
        final Item DEMONIC_RIFT_ITEM = new BlockItem(DEMONIC_RIFT.get(),ITEM_PROPERTIES).setRegistryName("demonic_rift");


        final Item MAGIC_CLAY_BLOCK_ITEM = new BlockItem(MAGIC_CLAY_BLOCK.get(),ITEM_PROPERTIES).setRegistryName("magic_clay_block");
        final Item MAGIC_CLAY_CARVED_ITEM = new BlockItem(MAGIC_CLAY_CARVED.get(),ITEM_PROPERTIES).setRegistryName("magic_clay_carved");
        final Item GORGON_STONE_ITEM = new BlockItem(GORGON_STONE.get(),ITEM_PROPERTIES).setRegistryName("gorgon_stone");
        final Item DEMONIC_GLOWSTONE_ITEM = new BlockItem(DEMONIC_GLOWSTONE.get(),ITEM_PROPERTIES).setRegistryName("demonic_glowstone");
        final Item DEMONIC_GLOWSTONE_DUST = new Item(ITEM_PROPERTIES).setRegistryName("demonic_glowstone_dust");

        final Item HEXED_BOOK = new HexedItem(UNSTACKABLE_FIRE,"hexed_book",false);
        final Item HEXED_WRITABLE_BOOK = new HexedItem(UNSTACKABLE_FIRE,"hexed_writable_book",false);
        final Item HEXED_WRITTEN_BOOK = new HexedItem(UNSTACKABLE_FIRE,"hexed_written_book",true);
        final Item POTION_EXTENDER_ITEM = new BlockItem(POTION_EXTENDER.get(),ITEM_PROPERTIES).setRegistryName("potion_extender");
        final Item POTION_AMPLIFIER_ITEM = new BlockItem(POTION_AMPLIFIER.get(),ITEM_PROPERTIES).setRegistryName("potion_amplifier");
        final Item POTION_RELAY_ITEM = new BlockItem(POTION_RELAY.get(),ITEM_PROPERTIES).setRegistryName("potion_relay");

        //final Item CHALK_LINE_ITEM = new BlockItem(CHALK_LINE_1.get(),ITEM_PROPERTIES).setRegistryName("chalk_line");

        final Item DEMON_STAFF = new DemonStaff(UNSTACKABLE_FIRE,"demon_staff");

        final Item ARCANE_HELMET = new EnchantedArmor("arcane_helmet",EquipmentSlot.HEAD,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);
        final Item ARCANE_CHEST = new EnchantedArmor("arcane_chestplate",EquipmentSlot.CHEST,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);
        final Item ARCANE_LEGGINGS = new EnchantedArmor("arcane_leggings",EquipmentSlot.LEGS,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);
        final Item ARCANE_BOOTS = new EnchantedArmor("arcane_boots",EquipmentSlot.FEET,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);

        final Item DEFENSE_HELMET = new EnchantedArmor("defense_helmet",EquipmentSlot.HEAD,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);
        final Item DEFENSE_CHEST = new EnchantedArmor("defense_chestplate",EquipmentSlot.CHEST,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);
        final Item DEFENSE_LEGGINGS = new EnchantedArmor("defense_leggings",EquipmentSlot.LEGS,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);
        final Item DEFENSE_BOOTS = new EnchantedArmor("defense_boots",EquipmentSlot.FEET,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);

        final Item DEMON_SPAWN_EGG = new ModSpawnEggItem(BASIC_DEMON,0x000000,0x000000,EGG_PROPERTIES).setRegistryName("basic_demon_spawn_egg");
        final Item DEMON_STRONG_SPAWN_EGG = new ModSpawnEggItem(STRONG_DEMON,0x000000,0x000000,EGG_PROPERTIES).setRegistryName("strong_demon_spawn_egg");
        final Item DEMON_RAPTOR_SPAWN_EGG = new ModSpawnEggItem(RAPTOR_DEMON,0x000000,0x000000,EGG_PROPERTIES).setRegistryName("demon_raptor_spawn_egg");


        final Item BIOME_CRYSTAL = new BiomeCrystal("biome_crystal",ITEM_PROPERTIES);


        ITEMS.add(GREATER_MANA_AMULET);
        ITEMS.add(GREATER_REGEN_AMULET);
        ITEMS.add(FOCUS_OF_MANA);
        ITEMS.add(FOCUS_OF_ALCHEMY);
        ITEMS.add(FOCUS_OF_ALCHEMY_2);
        ITEMS.add(FOCUS_OF_REGEN);
        ITEMS.add(RING_REGEN);
        ITEMS.add(RING_BOOST);
        ITEMS.add(RING_ARCANE_DISCOUNT);
        ITEMS.add(STAFF);
        ITEMS.add(STAFF_2);
        ITEMS.add(STAFF_3);
        ITEMS.add(SPELLBOOK_FOUR);

        ITEMS.add(POISON_FLOWER_ITEM);
        ITEMS.add(DEMONIC_STONE_ITEM);
        ITEMS.add(DEMONIC_ORE_ITEM);
        ITEMS.add(DEMON_GEM);
        ITEMS.add(MAGIC_CLAY);
        ITEMS.add(MARVELOUS_CLAY);
        ITEMS.add(MYSTIC_CLAY);
        ITEMS.add(ARCANE_CLAY);
        ITEMS.add(CLEANSING_GEM);
        ITEMS.add(GORGON_GEM);
        ITEMS.add(DEMON_CRYSTAL);

        ITEMS.add(INFUSED_DIAMOND);
        ITEMS.add(ENCHANTED_DIAMOND);
        ITEMS.add(ARCANE_DIAMOND);
        ITEMS.add(INFUSED_DIAMOND_BLOCK_ITEM);
        ITEMS.add(ENCHANTED_DIAMOND_BLOCK_ITEM);
        ITEMS.add(ARCANE_DIAMOND_BLOCK_ITEM);
        ITEMS.add(ENCHANTED_DEMONIC_GEM);
        ITEMS.add(SEARING_FLESH);
        ITEMS.add(DEMONIC_TOOTH);
        ITEMS.add(DEMONIC_GEM_BLOCK_ITEM);

        ITEMS.add(ESSENCE_NATURE);
        ITEMS.add(ESSENCE_ARCANE);
        ITEMS.add(ESSENCE_DEMONIC);
        ITEMS.add(ESSENCE_LIFE);
        ITEMS.add(ESSENCE_ALCHEMY);
        ITEMS.add(ESSENCE_ELEMENTAL_ADVANCED);
        ITEMS.add(ESSENCE_ELEMENTAL_BASIC);

        ITEMS.add(ARCANE_BLOOM_CROP);
        ITEMS.add(ENCHANTERS_WOOL_ITEM);
        ITEMS.add(ARCANE_BLOOM);
        ITEMS.add(ARCANE_FIBER);
        ITEMS.add(ARCANE_CLOTH);
        ITEMS.add(ROPE);
        ITEMS.add(MAGIC_ROPE);
        ITEMS.add(ENCHANTED_ROPE);

        ITEMS.add(MAGIC_CLAY_BLOCK_ITEM);
        ITEMS.add(MAGIC_CLAY_CARVED_ITEM);
        ITEMS.add(GORGON_STONE_ITEM);
        ITEMS.add(DEMONIC_GLOWSTONE_ITEM);
        ITEMS.add(DEMONIC_GLOWSTONE_DUST);
        ITEMS.add(HEXED_BOOK);
        ITEMS.add(HEXED_WRITABLE_BOOK);
        ITEMS.add(HEXED_WRITTEN_BOOK);
        ITEMS.add(POTION_EXTENDER_ITEM);
        ITEMS.add(POTION_AMPLIFIER_ITEM);
        ITEMS.add(POTION_RELAY_ITEM);

        //ITEMS.add(CHALK_LINE_ITEM);

        ITEMS.add(DEMON_STAFF);

        ITEMS.add(ARCANE_HELMET);
        ITEMS.add(ARCANE_CHEST);
        ITEMS.add(ARCANE_LEGGINGS);
        ITEMS.add(ARCANE_BOOTS);

        ITEMS.add(DEFENSE_HELMET);
        ITEMS.add(DEFENSE_CHEST);
        ITEMS.add(DEFENSE_LEGGINGS);
        ITEMS.add(DEFENSE_BOOTS);

        ITEMS.add(INFINITY_CRYSTAL_BLOCK);
        ITEMS.add(FLESH_BLOCK_ITEM);
        ITEMS.add(DEMONIC_RIFT_ITEM);

        ITEMS.add(DEMON_SPAWN_EGG);
        ITEMS.add(DEMON_STRONG_SPAWN_EGG);
        ITEMS.add(DEMON_RAPTOR_SPAWN_EGG);
        ITEMS.add(BIOME_CRYSTAL);

        //magic chalk items
        ITEMS.add(new MagicChalk("magic_chalk",() -> CHALK_LINE_1.get(),16));
        ITEMS.add(new MagicChalk("marvelous_chalk",() -> CHALK_LINE_2.get(),32));
        ITEMS.add(new MagicChalk("mystic_chalk",() -> CHALK_LINE_3.get(),128));
        ITEMS.add(new MagicChalk("arcane_chalk",() -> CHALK_LINE_4.get(),1024));

        //chalk for crafting stuff
        ITEMS.add(new BasicChalkItem("chalk",() -> CHALK_BLOCK.get(), ITEM_PROPERTIES,"A crafting ingredient used for crafting magic chalk. \nWill gain its own functionality in another update..."));


        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
    }


    //Block Properties
    static final BlockPropertiesCreator blockPropertiesCreator = new BlockPropertiesCreator();
    static final Block.Properties STONE_PROPERTIES = blockPropertiesCreator.create(Material.STONE,3.5f,2f, SoundType.STONE, true);

    static final Block.Properties CHALK_PROPERTIES = blockPropertiesCreator.create(Material.CLAY,0.4f,7f, SoundType.CALCITE, false).noCollission().noOcclusion();;

    static final Block.Properties FLOWER_PROPERTIES = blockPropertiesCreator.create(Material.PLANT,0.01f,0f, SoundType.CROP, false).noCollission().noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES = blockPropertiesCreator.create(Material.PLANT, 2f, 0.5f,  SoundType.HARD_CROP, false).noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES_2 = blockPropertiesCreator.create(Material.PLANT, 4f, 1f,  SoundType.HARD_CROP, false).noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES_3 = blockPropertiesCreator.create(Material.PLANT, 6f, 2f,  SoundType.HARD_CROP, true).noOcclusion();
    static final BlockBehaviour.Properties BRAMBLE_PROPERTIES_4 = blockPropertiesCreator.create(Material.PLANT, 10f, 4f,  SoundType.HARD_CROP, true).noOcclusion();
    static final BlockBehaviour.Properties CLAY_PROPERTIES = blockPropertiesCreator.create(Material.CLAY, 3f, 1f,  SoundType.GRAVEL, false);
    static final BlockBehaviour.Properties WOOL_PROPERTIES = blockPropertiesCreator.create(Material.WOOL, 8f, 800f,  SoundType.WOOL, false);
    static final BlockBehaviour.Properties FIRE_PROPERTIES = blockPropertiesCreator.create(Material.FIRE, 0, 0, SoundType.SAND, false).noOcclusion().lightLevel((BlockState state) -> 15);
    static final Block.Properties GLOW_PROPERTIES = blockPropertiesCreator.create(Material.STONE,1.5f,1f, SoundType.STONE, false).lightLevel((BlockState state) -> 15);

    private static Boolean never(BlockState p_235427_0_, BlockGetter p_235427_1_, BlockPos p_235427_2_, EntityType<?> p_235427_3_) {
        return (boolean)false;
    }
    private static boolean never(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }
    static final Block.Properties UNBREAKABLE_BLOCK_PROPERTIES = blockPropertiesCreator.create(Material.STONE,-1,1200f, SoundType.STONE, true).noDrops().noOcclusion().isValidSpawn(RegistryHandler::never).isRedstoneConductor(RegistryHandler::never).isSuffocating(RegistryHandler::never).isViewBlocking(RegistryHandler::never);


    public static final RegistryObject<Block> DEMONIC_STONE = BLOCKS.register("demonic_stone",() -> new Block(STONE_PROPERTIES));
    public static final RegistryObject<Block> DEMONIC_ORE = BLOCKS.register("demonic_ore",() -> new OreBlock(STONE_PROPERTIES));

    public static final RegistryObject<Block> DEMONIC_RIFT = BLOCKS.register("demonic_rift",() -> new DemonicRift(STONE_PROPERTIES));
    public static final RegistryObject<Block> GORGON_STONE = BLOCKS.register("gorgon_stone",() -> new Block(STONE_PROPERTIES));
    public static final RegistryObject<Block> GORGON_FIRE = BLOCKS.register("gorgon_fire",() -> new GorgonFire(FIRE_PROPERTIES));
    public static final RegistryObject<Block> DEMONIC_GLOWSTONE = BLOCKS.register("demonic_glowstone",() -> new Block(GLOW_PROPERTIES));
    public static final RegistryObject<Block> FLESH_BLOCK = BLOCKS.register("flesh_block",() -> new Block(STONE_PROPERTIES));

    public static final RegistryObject<Block> POTION_EXTENDER = BLOCKS.register("potion_extender",() -> new PotionExtender(STONE_PROPERTIES));
    public static final RegistryObject<Block> POTION_AMPLIFIER = BLOCKS.register("potion_amplifier",() -> new PotionAmplifier(STONE_PROPERTIES));

    static EntityType<? extends Entity> getClayGolem(){
        return CLAY_GOLEM.get();
    }
    public static final RegistryObject<Block> MAGIC_CLAY_BLOCK = BLOCKS.register("magic_clay_block",() -> new Block(CLAY_PROPERTIES));
    public static final RegistryObject<Block> MAGIC_CLAY_CARVED = BLOCKS.register("magic_clay_carved",() -> new CarvedClay(CLAY_PROPERTIES,false,MAGIC_CLAY_BLOCK.get(), RegistryHandler::getClayGolem));

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

    //IDK about the balancing on the charges and costs of these.
    //the colors look pretty good though
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_1 = BLOCKS.register("magic_chalk_line",() ->
            new ChalkLineBlock(CHALK_PROPERTIES,3,1.0f,new ChalkColor(0.3f,0.2f,0.3f,0.5f,0.3f,0.5f)));
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_2 = BLOCKS.register("marvelous_chalk_line",() ->
            new ChalkLineBlock(CHALK_PROPERTIES,6,0.95f,new ChalkColor(0.3f,0.3f,0.15f,0.6f,0.6f,0.3f)));
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_3 = BLOCKS.register("mystic_chalk_line",() ->
            new ChalkLineBlock(CHALK_PROPERTIES,10,0.9f,new ChalkColor(0.1f,0.4f,0.2f,0.1f,0.6f,0.3f)));
    public static final RegistryObject<ChalkLineBlock> CHALK_LINE_4 = BLOCKS.register("arcane_chalk_line",() ->
            new ChalkLineBlock(CHALK_PROPERTIES,16,0.8f,new ChalkColor(0.3f,0.03f,0.3f,0.55f,0.05f,0.6f)));

    public static final RegistryObject<BasicChalk> CHALK_BLOCK = BLOCKS.register("chalk",() ->
            new BasicChalk(CHALK_PROPERTIES,new ChalkColor(1.0f,1.0f,1.0f,0f,0f,0f)));

    public static final RegistryObject<Block> POTION_RELAY = BLOCKS.register("potion_relay",() -> new PotionRelay());

    public static RegistryObject<BlockEntityType<PotionExtenderTile>> PotionExtenderType = TILE_ENTITIES.register("potion_extender_tile",() -> BlockEntityType.Builder.of(PotionExtenderTile::new,POTION_EXTENDER.get()).build(null));
    public static RegistryObject<BlockEntityType<PotionAmplifierTile>> PotionAmplifierType = TILE_ENTITIES.register("potion_amplifier_tile",() -> BlockEntityType.Builder.of(PotionAmplifierTile::new,POTION_AMPLIFIER.get()).build(null));
    public static RegistryObject<BlockEntityType<InfinityCrystalTile>> InfinityCrystalType = TILE_ENTITIES.register("infinity_crystal_tile",() -> BlockEntityType.Builder.of(InfinityCrystalTile::new, ItemsRegistry.INFINITY_JAR).build(null));

    public static RegistryObject<BlockEntityType<PotionRelayTile>> PotionRelayType = TILE_ENTITIES.register("potion_relay_tile",() -> BlockEntityType.Builder.of(PotionRelayTile::new, POTION_RELAY.get()).build(null));

    public static RegistryObject<BlockEntityType<ChalkTile>> ChalkTileType = TILE_ENTITIES.register("chalk_tile",() -> BlockEntityType.Builder.of(ChalkTile::new, CHALK_LINE_1.get(),CHALK_LINE_2.get(),CHALK_LINE_3.get(),CHALK_LINE_4.get()).build(null));

    public static final RegistryObject<EntityType<? extends Mob>> BASIC_DEMON = ENTITIES.register("demon_basic", () -> EntityType.Builder.of(EntityDemonBasic::new, MobCategory.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_basic").toString()));
    public static final RegistryObject<EntityType<? extends Mob>> STRONG_DEMON = ENTITIES.register("demon_strong", () -> EntityType.Builder.of(EntityDemonStrong::new, MobCategory.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_strong").toString()));
    public static final RegistryObject<EntityType<? extends Mob>> BOSS_DEMON_KING = ENTITIES.register("boss_demon_king", () -> EntityType.Builder.of(EntityBossDemonKing::new, MobCategory.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "boss_demon_king").toString()));

    public static final RegistryObject<EntityType<? extends Mob>> RAPTOR_DEMON = ENTITIES.register("demon_raptor", () -> EntityType.Builder.of(EntityDemonRaptor::new, MobCategory.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_raptor").toString()));

    public static final RegistryObject<EntityType<? extends Mob>> CLAY_GOLEM = ENTITIES.register("clay_golem", () -> EntityType.Builder.of(EntityClayGolem::new, MobCategory.MISC).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "clay_golem").toString()));

    public static final RegistryObject<EntityType<? extends EntityTornado>> TORNADO = ENTITIES.register("tornado", () -> EntityType.Builder.<EntityTornado>of(EntityTornado::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(20).build(new ResourceLocation(ArsOmega.MOD_ID, "tornado").toString()));

    public static final RegistryObject<EntityType<? extends EntityWhirlpool>> WHIRLPOOL = ENTITIES.register("whirlpool", () -> EntityType.Builder.<EntityWhirlpool>of(EntityWhirlpool::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(20).build(new ResourceLocation(ArsOmega.MOD_ID, "whirlpool").toString()));

    public static final RegistryObject<EntityType<? extends EntityEarthquake>> EARTHQUAKE = ENTITIES.register("earthquake", () -> EntityType.Builder.<EntityEarthquake>of(EntityEarthquake::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(20).build(new ResourceLocation(ArsOmega.MOD_ID, "earthquake").toString()));


    public static final RegistryObject<EntityType<? extends EntityDivineSmite>> DIVINE_SMITE = ENTITIES.register("divine_smite", () -> EntityType.Builder.<EntityDivineSmite>of(EntityDivineSmite::new, MobCategory.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build(new ResourceLocation(ArsOmega.MOD_ID, "divine_smite").toString()));

    public static final RegistryObject<EntityType<EntityWitherBound>> WITHER_BOUND = ENTITIES.register("bound_wither", () -> EntityType.Builder.<EntityWitherBound>of(EntityWitherBound::new, MobCategory.MISC).sized(0.9F, 3.0F).build(new ResourceLocation(ArsOmega.MOD_ID, "bound_wither").toString()));
}
