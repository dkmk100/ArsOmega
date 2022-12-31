package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.*;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

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

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS,ArsOmega.MOD_ID);


    public static void RegisterPotionRecipes(){
        ItemStack AWKWARD = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);

        ItemStack dispellantPot = PotionUtils.setPotion(new ItemStack(Items.POTION), DISPELLANT_POT.get());
        ItemStack dispellantLongPot = PotionUtils.setPotion(new ItemStack(Items.POTION), DISPELLANT_LONG_POT.get());
        ItemStack poison2Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_POISON);
        ItemStack poison3Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), POISON_3_POT.get());


        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(RegistryHandler.ESSENCE_ALCHEMY.get()),  dispellantPot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(dispellantPot), Ingredient.of(Items.REDSTONE),  dispellantLongPot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(poison2Pot), Ingredient.of(RegistryHandler.POISON_FLOWER_ITEM.get()),  poison3Pot));

        ItemStack health1Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), HEALTH_1_POT.get());
        ItemStack health2Pot = PotionUtils.setPotion(new ItemStack(Items.POTION), HEALTH_2_POT.get());

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(RegistryHandler.ESSENCE_LIFE.get()), health1Pot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(health1Pot), Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), health2Pot));

        ItemStack absPot = PotionUtils.setPotion(new ItemStack(Items.POTION), ABS_POT.get());
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(Items.GOLDEN_APPLE), absPot));


        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(new ItemStack(Items.GLASS_BOTTLE)), Ingredient.of(Items.GUNPOWDER), new ItemStack(RegistryHandler.SPLASH_BOTTLE.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(new ItemStack(RegistryHandler.SPLASH_BOTTLE.get())), Ingredient.of(Items.DRAGON_BREATH),new ItemStack(RegistryHandler.LINGERING_BOTTLE.get())));

    }


    public static RegistryObject<Potion>POISON_3_POT = POTIONS.register("poison_3_potion", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 3600,2)}));

        public static RegistryObject<Potion> DISPELLANT_POT = POTIONS.register("dispellant_potion", () ->new Potion(new MobEffectInstance[]{new MobEffectInstance(DISPELLANT, 3600,0)}));
    public static RegistryObject<Potion> DISPELLANT_LONG_POT = POTIONS.register("dispellant_extended_potion", () ->new Potion(new MobEffectInstance[]{new MobEffectInstance(DISPELLANT, 9600,0)}));

    public static RegistryObject<Potion> HEALTH_1_POT = POTIONS.register("health_1_potion", () ->new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.HEALTH_BOOST, 18000,0)}));
    public static RegistryObject<Potion> HEALTH_2_POT = POTIONS.register("health_2_potion", () ->new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.HEALTH_BOOST, 18000,1)}));

    public static RegistryObject<Potion> ABS_POT = POTIONS.register("absorption_potion", () ->new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.ABSORPTION, 9600,0)}));

    public static RegistryObject<Potion> BLENDED_POT = POTIONS.register("blended_potion", () ->new Potion(new MobEffectInstance[]{}));

}
