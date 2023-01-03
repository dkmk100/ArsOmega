package com.dkmk100.arsomega;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ObjectHolder;

//todo: get rid of this entire space and actually use deferred registrer
@ObjectHolder("arsomega")
public class ItemsRegistry {
    @ObjectHolder("staff")
    public static Item STAFF;
    @ObjectHolder("arcane_bloom_crop")
    public static Item ARCANE_BLOOM_CROP;
    @ObjectHolder("cleansing_gem")
    public static Item CLEANSING_GEM;
    @ObjectHolder("demonic_crystal")
    public static Item DEMON_CRYSTAL;

    @ObjectHolder("magic_clay")
    public static Item MAGIC_CLAY;
    @ObjectHolder("marvelous_clay")
    public static Item MARVELOUS_CLAY;
    @ObjectHolder("mystic_clay")
    public static Item MYSTIC_CLAY;
    @ObjectHolder("arcane_clay")
    public static Item ARCANE_CLAY;
    @ObjectHolder("infinity_crystal")
    public static Block INFINITY_JAR;

    @ObjectHolder("greater_mana_amulet")
    public static Item GREATER_MANA_AMULET;
    @ObjectHolder("focus_of_alchemy")
    public static Item ALCHEMY_FOCUS;
    @ObjectHolder("focus_of_advanced_alchemy")
    public static Item ALCHEMY_FOCUS_ADVANCED;

    @ObjectHolder("alchemy_essence")
    public static Item ALCHEMY_ESSENCE;

    @ObjectHolder("life_essence")
    public static Item LIFE_ESSENCE;

    @ObjectHolder("nature_essence")
    public static Item NATURE_ESSENCE;

    @ObjectHolder("arcane_essence")
    public static Item ARCANE_ESSENCE;

    @ObjectHolder("biome_crystal")
    public static Item BIOME_CRYSTAL;

    @ObjectHolder("dimension_crystal")
    public static Item DIMENSION_CRYSTAL;

    @ObjectHolder("demonic_tooth")
    public static Item DEMONIC_TOOTH;

    @ObjectHolder("demonic_gem")
    public static Item DEMONIC_GEM;

    @ObjectHolder("gorgon_gem")
    public static Item GORGON_GEM;

    @ObjectHolder("poison_flower")
    public static Item POISON_FLOWER;

    @ObjectHolder("enchanted_mirror_shard")
    public static Item ENCHANTED_MIRROR_SHARD;

    @ObjectHolder("enchanters_cloak")
    public static Item ENCHANTERS_CLOAK;
    @ObjectHolder("stability_cloak")
    public static Item STABILITY_CLOAK;

    @ObjectHolder("splash_bottle")
    public static Item SPLASH_BOTTLE;
    @ObjectHolder("lingering_bottle")
    public static Item LINGERING_BOTTLE;

    @ObjectHolder("sigil_binding_active")
    public static Item SIGIL_BINDING_ACTIVE;

    @ObjectHolder("salt")
    public static Item SALT;

    @ObjectHolder("ancient_mirror_shard")
    public static Item ANCIENT_SHARD;

    @ObjectHolder("celestial_staff")
    public static Item CELESTIAL_STAFF;

    @ObjectHolder("arcane_staff")
    public static Item ARCANE_STAFF;

    @ObjectHolder("demonic_stone")
    public static Block DEMONIC_STONE;

    @ObjectHolder("infused_diamond")
    public static Item INFUSED_DIAMOND;

    @ObjectHolder("enchanters_wool")
    public static Item ENCHANTERS_WOOL_ITEM;

    @ObjectHolder("arcane_cloth")
    public static Item ARCANE_CLOTH;

    @ObjectHolder("arcane_apple")
    public static Item ARCANE_APPLE;
}
