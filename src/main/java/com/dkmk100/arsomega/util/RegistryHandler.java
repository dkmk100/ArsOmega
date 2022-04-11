package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.armors.BasicArmorMaterial;
import com.dkmk100.arsomega.base_blocks.BlockPropertiesCreator;
import com.dkmk100.arsomega.blocks.*;
import com.dkmk100.arsomega.books.CustomSpellBook;
import com.dkmk100.arsomega.entities.EntityBossDemonKing;
import com.dkmk100.arsomega.entities.EntityDemonBasic;
import com.dkmk100.arsomega.entities.EntityDemonStrong;
import com.dkmk100.arsomega.glyphs.*;
import com.dkmk100.arsomega.init.ExperimentalStructureInit;
import com.dkmk100.arsomega.init.StructureInit;
import com.dkmk100.arsomega.items.*;
import com.dkmk100.arsomega.rituals.*;
import com.dkmk100.arsomega.tools.BasicItemTier;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBookRenderer;
import com.hollingsworth.arsnouveau.common.block.CreativeManaJar;
import com.hollingsworth.arsnouveau.common.block.ManaJar;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class RegistryHandler{
    public static final ResourceLocation DIMTYPE = new ResourceLocation(ArsOmega.MOD_ID, "demon_realm");

    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();
    public static List<AbstractRitual> registeredRituals = new ArrayList<>();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Block.class, ArsOmega.MOD_ID);

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES,ArsOmega.MOD_ID);

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ArsOmega.MOD_ID);


    public static final ITag.INamedTag<Block> GORGON_FIRE_BURNABLES = BlockTags.bind("arsomega:gorgon_fire_burnables");

    public static void init (){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        TILE_ENTITIES.register(bus);
        ENTITIES.register(bus);
        //StructureInit.RegisterStructures(bus);
        ExperimentalStructureInit.RegisterStructures(bus);
    }

    public static void registerBlocks(final RegistryEvent.Register<Block> event){
        event.getRegistry().register(new InfinityCrystal(UNBREAKABLE_BLOCK_PROPERTIES,"infinity_crystal"));
    }

    public static void registerGlyphs(){
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
    }

    public static void register(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart.tag, spellPart);
        registeredSpells.add(spellPart);
    }
    public static void register(AbstractRitual ritual) {
        ArsNouveauAPI.getInstance().registerRitual(ritual.getID(),ritual);
        registeredRituals.add(ritual);
    }

    static final ItemPropertiesCreator itemPropertiesCreator = new ItemPropertiesCreator();
    static final Item.Properties ITEM_PROPERTIES = itemPropertiesCreator.create(ItemGroup.TAB_MISC,64,null,0);
    static final Item.Properties ITEM_PROPERTIES_FIRE = itemPropertiesCreator.create(ItemGroup.TAB_MISC,64,null,0).fireResistant();
    static final Item.Properties UNSTACKABLE_FIRE = itemPropertiesCreator.create(ItemGroup.TAB_COMBAT,1,null,0).fireResistant();


    public static final List<Item> ITEMS = new ArrayList<>();
    public static void RegisterItems(RegistryEvent.Register<Item> event){
        final Item GREATER_MANA_AMULET = new MagicCurio("greater_mana_amulet",500,1);
        final Item GREATER_REGEN_AMULET = new MagicCurio("greater_regen_amulet",10,25);
        final Item FOCUS_OF_MANA = new MagicCurio("focus_of_mana",3000,-40);
        final Item FOCUS_OF_ALCHEMY = new MagicCurio("focus_of_alchemy",-50,-5);
        final Item FOCUS_OF_ALCHEMY_2 = new MagicCurio("focus_of_advanced_alchemy",-200,-12);
        final Item FOCUS_OF_REGEN = new MagicCurio("focus_of_regen",-250,100);
        final Item RING_REGEN = new MagicCurio("ring_regen",0,5);
        final Item RING_BOOST = new MagicCurio("ring_boost",100,0);
        final Item STAFF = new Staff("staff", BasicItemTier.Staff,2,-2.5f,2, AugmentAmplify.INSTANCE,2);
        final Item STAFF_2 = new Staff("archmage_staff", BasicItemTier.Staff2,2,-2.5f,3, AdvancedAmplify.INSTANCE,2);
        final Item STAFF_3 = new Staff("arcane_staff", BasicItemTier.Staff3,2,-2.5f,3, AdvancedAmplify.INSTANCE,3,true);
        final Item SPELLBOOK_FOUR = new CustomSpellBook("arcane_book",(new Item.Properties()).stacksTo(1).tab(ArsNouveau.itemGroup).setISTER(() -> {
            return SpellBookRenderer::new;
        }).fireResistant(),4,10);
        final Item POISON_FLOWER_ITEM = new BlockItem(POISON_FLOWER.get(),ITEM_PROPERTIES).setRegistryName("poison_flower");
        final Item DEMONIC_STONE_ITEM = new BlockItem(DEMONIC_STONE.get(),ITEM_PROPERTIES).setRegistryName("demonic_stone");
        final Item DEMONIC_ORE_ITEM = new BlockItem(DEMONIC_ORE.get(),ITEM_PROPERTIES).setRegistryName("demonic_ore");
        final Item DEMON_GEM = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("demonic_gem");
        final Item CLEANSING_GEM = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("cleansing_gem");
        final Item DEMON_CRYSTAL = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("demonic_crystal");
        final Item ARCANE_BLOOM_CROP = new BlockItem(ARCANE_BLOOM.get(),ITEM_PROPERTIES).setRegistryName("arcane_bloom_crop");
        final Item ENCHANTERS_WOOL_ITEM = new BlockItem(ENCHANTERS_WOOL.get(),ITEM_PROPERTIES).setRegistryName("enchanters_wool");
        final Item GORGON_GEM = new DescribedItem("gorgon_gem",ITEM_PROPERTIES_FIRE,"Found in gorgon caves");

        final Item ARCANE_BLOOM = new Item(ITEM_PROPERTIES).setRegistryName("arcane_bloom");
        final Item ARCANE_FIBER = new Item(ITEM_PROPERTIES).setRegistryName("arcane_fiber");
        final Item ARCANE_CLOTH = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("arcane_cloth");

        final Item ROPE = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("rope");
        final Item MAGIC_ROPE = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("magic_rope");
        final Item ENCHANTED_ROPE = new Item(ITEM_PROPERTIES_FIRE).setRegistryName("enchanted_rope");

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
        final Item DEMON_STAFF = new DemonStaff(UNSTACKABLE_FIRE,"demon_staff");

        final Item ARCANE_HELMET = new EnchantedArmor("arcane_helmet",EquipmentSlotType.HEAD,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);
        final Item ARCANE_CHEST = new EnchantedArmor("arcane_chestplate",EquipmentSlotType.CHEST,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);
        final Item ARCANE_LEGGINGS = new EnchantedArmor("arcane_leggings",EquipmentSlotType.LEGS,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);
        final Item ARCANE_BOOTS = new EnchantedArmor("arcane_boots",EquipmentSlotType.FEET,350,12,BasicArmorMaterial.Arcane,UNSTACKABLE_FIRE);

        final Item DEFENSE_HELMET = new EnchantedArmor("defense_helmet",EquipmentSlotType.HEAD,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);
        final Item DEFENSE_CHEST = new EnchantedArmor("defense_chestplate",EquipmentSlotType.CHEST,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);
        final Item DEFENSE_LEGGINGS = new EnchantedArmor("defense_leggings",EquipmentSlotType.LEGS,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);
        final Item DEFENSE_BOOTS = new EnchantedArmor("defense_boots",EquipmentSlotType.FEET,80,6,BasicArmorMaterial.Defensive,UNSTACKABLE_FIRE);

        final Item DEMON_SPAWN_EGG = new ModSpawnEggItem(BASIC_DEMON,0x000000,0x000000,ITEM_PROPERTIES).setRegistryName("basic_demon_spawn_egg");

        ITEMS.add(GREATER_MANA_AMULET);
        ITEMS.add(GREATER_REGEN_AMULET);
        ITEMS.add(FOCUS_OF_MANA);
        ITEMS.add(FOCUS_OF_ALCHEMY);
        ITEMS.add(FOCUS_OF_ALCHEMY_2);
        ITEMS.add(FOCUS_OF_REGEN);
        ITEMS.add(RING_REGEN);
        ITEMS.add(RING_BOOST);
        ITEMS.add(STAFF);
        ITEMS.add(STAFF_2);
        ITEMS.add(STAFF_3);
        ITEMS.add(SPELLBOOK_FOUR);

        ITEMS.add(POISON_FLOWER_ITEM);
        ITEMS.add(DEMONIC_STONE_ITEM);
        ITEMS.add(DEMONIC_ORE_ITEM);
        ITEMS.add(DEMON_GEM);
        ITEMS.add(CLEANSING_GEM);
        ITEMS.add(GORGON_GEM);
        ITEMS.add(DEMON_CRYSTAL);

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

        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
    }


    //Block Properties
    static final BlockPropertiesCreator blockPropertiesCreator = new BlockPropertiesCreator();
    static final Block.Properties STONE_PROPERTIES = blockPropertiesCreator.create(Material.STONE,3.5f,2f,0, SoundType.STONE, ToolType.PICKAXE);
    static final Block.Properties FLOWER_PROPERTIES = blockPropertiesCreator.create(Material.PLANT,0.01f,0f,0, SoundType.CROP, null).noCollission().noOcclusion();
    static final AbstractBlock.Properties BRAMBLE_PROPERTIES = blockPropertiesCreator.create(Material.PLANT, 2f, 0.5f, 0, SoundType.HARD_CROP, ToolType.AXE).noOcclusion();
    static final AbstractBlock.Properties BRAMBLE_PROPERTIES_2 = blockPropertiesCreator.create(Material.PLANT, 4f, 1f, 0, SoundType.HARD_CROP, ToolType.AXE).noOcclusion();
    static final AbstractBlock.Properties BRAMBLE_PROPERTIES_3 = blockPropertiesCreator.create(Material.PLANT, 6f, 2f, 1, SoundType.HARD_CROP, ToolType.AXE).noOcclusion();
    static final AbstractBlock.Properties BRAMBLE_PROPERTIES_4 = blockPropertiesCreator.create(Material.PLANT, 10f, 4f, 2, SoundType.HARD_CROP, ToolType.AXE).noOcclusion();
    static final AbstractBlock.Properties CLAY_PROPERTIES = blockPropertiesCreator.create(Material.CLAY, 3f, 1f, 2, SoundType.GRAVEL, ToolType.SHOVEL);
    static final AbstractBlock.Properties WOOL_PROPERTIES = blockPropertiesCreator.create(Material.WOOL, 4f, 8f, 2, SoundType.WOOL, ToolType.AXE);
    static final AbstractBlock.Properties FIRE_PROPERTIES = blockPropertiesCreator.create(Material.FIRE, 0, 0, 0, SoundType.SAND, null).noOcclusion().lightLevel((BlockState state) -> 15);
    static final Block.Properties GLOW_PROPERTIES = blockPropertiesCreator.create(Material.STONE,1.5f,1f,0, SoundType.STONE, ToolType.PICKAXE).lightLevel((BlockState state) -> 15);

    private static Boolean never(BlockState p_235427_0_, IBlockReader p_235427_1_, BlockPos p_235427_2_, EntityType<?> p_235427_3_) {
        return (boolean)false;
    }
    private static boolean never(BlockState p_235436_0_, IBlockReader p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }
    static final Block.Properties UNBREAKABLE_BLOCK_PROPERTIES = blockPropertiesCreator.create(Material.STONE,-1,1200f,0, SoundType.STONE, ToolType.PICKAXE).noDrops().noOcclusion().isValidSpawn(RegistryHandler::never).isRedstoneConductor(RegistryHandler::never).isSuffocating(RegistryHandler::never).isViewBlocking(RegistryHandler::never);


    public static final RegistryObject<Block> DEMONIC_STONE = BLOCKS.register("demonic_stone",() -> new Block(STONE_PROPERTIES));
    public static final RegistryObject<Block> DEMONIC_ORE = BLOCKS.register("demonic_ore",() -> new OreBlock(STONE_PROPERTIES));

    public static final RegistryObject<Block> DEMONIC_RIFT = BLOCKS.register("demonic_rift",() -> new DemonicRift(STONE_PROPERTIES));
    public static final RegistryObject<Block> GORGON_STONE = BLOCKS.register("gorgon_stone",() -> new Block(STONE_PROPERTIES));
    public static final RegistryObject<Block> GORGON_FIRE = BLOCKS.register("gorgon_fire",() -> new GorgonFire(FIRE_PROPERTIES));
    public static final RegistryObject<Block> DEMONIC_GLOWSTONE = BLOCKS.register("demonic_glowstone",() -> new Block(GLOW_PROPERTIES));
    public static final RegistryObject<Block> FLESH_BLOCK = BLOCKS.register("flesh_block",() -> new Block(STONE_PROPERTIES));

    public static final RegistryObject<Block> POTION_EXTENDER = BLOCKS.register("potion_extender",() -> new PotionExtender(STONE_PROPERTIES));
    public static final RegistryObject<Block> POTION_AMPLIFIER = BLOCKS.register("potion_amplifier",() -> new PotionAmplifier(STONE_PROPERTIES));

    public static final RegistryObject<Block> MAGIC_CLAY_BLOCK = BLOCKS.register("magic_clay_block",() -> new Block(CLAY_PROPERTIES));
    public static final RegistryObject<Block> MAGIC_CLAY_CARVED = BLOCKS.register("magic_clay_carved",() -> new CarvedClay(CLAY_PROPERTIES,false,MAGIC_CLAY_BLOCK.get(), EntityType.IRON_GOLEM));

    public static final RegistryObject<Block> ENCHANTERS_WOOL = BLOCKS.register("enchanters_wool",() -> new Block(WOOL_PROPERTIES));
    public static final RegistryObject<Block> ARCANE_BLOOM = BLOCKS.register("arcane_bloom_crop",() -> new ArcaneBloomCrop());

    public static final RegistryObject<Block> POISON_FLOWER = BLOCKS.register("poison_flower",() -> new PoisonFlower(Effects.POISON,FLOWER_PROPERTIES));
    public static final RegistryObject<Block> BRAMBLE_1 = BLOCKS.register("bramble_1",() -> new BramblesBlock(BRAMBLE_PROPERTIES,1));
    public static final RegistryObject<Block> BRAMBLE_2 = BLOCKS.register("bramble_2",() -> new BramblesBlock(BRAMBLE_PROPERTIES_2,2));
    public static final RegistryObject<Block> BRAMBLE_3 = BLOCKS.register("bramble_3",() -> new BramblesBlock(BRAMBLE_PROPERTIES_3,3));
    public static final RegistryObject<Block> BRAMBLE_4 = BLOCKS.register("bramble_4",() -> new BramblesBlock(BRAMBLE_PROPERTIES_4,4));

    public static RegistryObject<TileEntityType<PotionExtenderTile>> PotionExtenderType = TILE_ENTITIES.register("potion_extender_tile",() -> TileEntityType.Builder.of(PotionExtenderTile::new,POTION_EXTENDER.get()).build(null));
    public static RegistryObject<TileEntityType<PotionAmplifierTile>> PotionAmplifierType = TILE_ENTITIES.register("potion_amplifier_tile",() -> TileEntityType.Builder.of(PotionAmplifierTile::new,POTION_AMPLIFIER.get()).build(null));
    public static RegistryObject<TileEntityType<InfinityCrystalTile>> InfinityCrystalType = TILE_ENTITIES.register("infinity_crystal_tile",() -> TileEntityType.Builder.of(InfinityCrystalTile::new, ItemsRegistry.INFINITY_JAR).build(null));


    public static final RegistryObject<EntityType<? extends MobEntity>> BASIC_DEMON = ENTITIES.register("demon_basic", () -> EntityType.Builder.of(EntityDemonBasic::new, EntityClassification.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_basic").toString()));
    public static final RegistryObject<EntityType<? extends MobEntity>> STRONG_DEMON = ENTITIES.register("demon_strong", () -> EntityType.Builder.of(EntityDemonStrong::new, EntityClassification.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "demon_strong").toString()));
    public static final RegistryObject<EntityType<? extends MobEntity>> BOSS_DEMON_KING = ENTITIES.register("boss_demon_king", () -> EntityType.Builder.of(EntityBossDemonKing::new, EntityClassification.MONSTER).sized(0.5F, 1.7F).build(new ResourceLocation(ArsOmega.MOD_ID, "boss_demon_king").toString()));
}
