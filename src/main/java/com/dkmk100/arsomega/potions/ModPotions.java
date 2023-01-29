package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.ObjectHolder;

public class ModPotions {

    public static DemonicCurse DEMONIC_CURSE = new DemonicCurse();
    public static PermaFlight PERMA_FLIGHT = new PermaFlight();
    public static Adrenaline ADRENALINE = new Adrenaline();
    public static MobEffect LEAD_SKIN = new GenericEffect(MobEffectCategory.BENEFICIAL,0,"lead_skin");
    public static MobEffect NO_BREAK = new GenericEffect(MobEffectCategory.NEUTRAL,0,"no_magic_break",false);
    public static MobEffect STONE_PETRIFICATION = new PetrificationEffect("stone_petrification");
    public static MobEffect VINE_BIND = new BindEffect("vine_bind",2743808);
    public static MobEffect BURNED = new GenericEffect(MobEffectCategory.HARMFUL,0,"burned",false);
    public static MobEffect SOUL_FIRE = new GenericEffect(MobEffectCategory.HARMFUL,0,"soulfire",false);

    public static MobEffect DISPELLANT = new GenericEffect(MobEffectCategory.NEUTRAL,0,"dispellant",true);

    public static MobEffect DEMONIC_ANCHORING = new GenericEffect(MobEffectCategory.NEUTRAL,0,"demonic_anchoring",false);

    public static MobEffect DEMONIC_CLEANSE = new GenericEffect(MobEffectCategory.NEUTRAL,0,"demonic_cleanse",false);

    public static MobEffect BLOOD_CLOT = new GenericEffect(MobEffectCategory.HARMFUL,0,"blood_clot",false);


    public static void RegisterPotionRecipes(){
        ItemStack AWKWARD = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);

        ItemStack dispellantPot = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionsRegistry.DISPELLANT_POT);
        ItemStack dispellantLongPot = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionsRegistry.DISPELLANT_LONG_POT);
        ItemStack poison2Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_POISON);
        ItemStack poison3Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionsRegistry.POISON_3_POT);


        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(RegistryHandler.ESSENCE_ALCHEMY.get()),  dispellantPot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(dispellantPot), Ingredient.of(Items.REDSTONE),  dispellantLongPot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(poison2Pot), Ingredient.of(RegistryHandler.POISON_FLOWER_ITEM.get()),  poison3Pot));

        ItemStack health1Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionsRegistry.HEALTH_1_POT);
        ItemStack health2Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionsRegistry.HEALTH_2_POT);

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(RegistryHandler.ESSENCE_LIFE.get()), health1Pot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(health1Pot), Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), health2Pot));

        ItemStack absPot = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionsRegistry.ABS_POT);
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(Items.GOLDEN_APPLE), absPot));


        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(new ItemStack(Items.GLASS_BOTTLE)), Ingredient.of(Items.GUNPOWDER), new ItemStack(RegistryHandler.SPLASH_BOTTLE.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(new ItemStack(RegistryHandler.SPLASH_BOTTLE.get())), Ingredient.of(Items.DRAGON_BREATH),new ItemStack(RegistryHandler.LINGERING_BOTTLE.get())));

    }

    @ObjectHolder(ArsOmega.MOD_ID)
    public class PotionsRegistry{
        @ObjectHolder("poison_3_potion")
        public static Potion POISON_3_POT;

        @ObjectHolder("dispellant_potion")
        public static Potion DISPELLANT_POT;
        @ObjectHolder("dispellant_extended_potion")
        public static Potion DISPELLANT_LONG_POT;

        @ObjectHolder("health_1_potion")
        public static Potion HEALTH_1_POT;
        @ObjectHolder("health_2_potion")
        public static Potion HEALTH_2_POT;

        @ObjectHolder("absorption_potion")
        public static Potion ABS_POT;

        @ObjectHolder("blended_potion")
        public static Potion BLENDED_POT;
    }

    @Mod.EventBusSubscriber(
            modid = ArsOmega.MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static class RegistrationHandler {
        public RegistrationHandler() {
        }

        @SubscribeEvent
        public static void registerEffects(RegistryEvent.Register<MobEffect> event) {
            IForgeRegistry<MobEffect> registry = event.getRegistry();
            registry.registerAll(new MobEffect[]{DEMONIC_CURSE,PERMA_FLIGHT,ADRENALINE, LEAD_SKIN,NO_BREAK,STONE_PETRIFICATION,VINE_BIND,BURNED,SOUL_FIRE,DISPELLANT,DEMONIC_ANCHORING,DEMONIC_CLEANSE,BLOOD_CLOT});
        }

        @SubscribeEvent
        public static void registerPotions(RegistryEvent.Register<Potion> event) {
            IForgeRegistry<Potion> registry = event.getRegistry();
            registry.register((new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 3600,2)})).setRegistryName("poison_3_potion"));
            registry.register((new Potion(new MobEffectInstance[]{new MobEffectInstance(DISPELLANT, 3600,0)})).setRegistryName("dispellant_potion"));
            registry.register((new Potion(new MobEffectInstance[]{new MobEffectInstance(DISPELLANT, 9600,0)})).setRegistryName("dispellant_extended_potion"));
            registry.register((new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.HEALTH_BOOST, 18000,0)})).setRegistryName("health_1_potion"));
            registry.register((new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.HEALTH_BOOST, 18000,1)})).setRegistryName("health_2_potion"));
            registry.register((new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.ABSORPTION, 9600,0)})).setRegistryName("absorption_potion"));
            registry.register((new Potion(new MobEffectInstance[]{})).setRegistryName("blended_potion"));
        }
    }
}
