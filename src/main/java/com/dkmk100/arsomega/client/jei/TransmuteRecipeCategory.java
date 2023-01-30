package com.dkmk100.arsomega.client.jei;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.crafting.TransmuteRecipe;
import com.dkmk100.arsomega.glyphs.AdvancedAmplify;
import com.dkmk100.arsomega.glyphs.TransmuteGlyph;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.client.jei.JEIConstants;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipeCategory implements IRecipeCategory<TransmuteRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("arsomega", "transmute");
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack input;
    public ItemStack output;
    public int minAmp;

    public TransmuteRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(TransmuteGlyph.INSTANCE)));
    }

    public ResourceLocation getUid() {
        return UID;
    }

    public Class getRecipeClass() {
        return TransmuteRecipe.class;
    }

    public Component getTitle() {
        return new TranslatableComponent("arsomega.transmute_recipe");
    }

    public IDrawable getBackground() {
        return this.helper.createBlankDrawable(150, 32);
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(TransmuteRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> inputs = new ArrayList();
        List<ItemStack> input = new ArrayList<>();
        input.add(o.input);
        inputs.add(input);


        List<ItemStack> validFoci = new ArrayList<>();
        if(o.minAmp>TransmuteGlyph.normalMax){
            if(o.minAmp<=TransmuteGlyph.focusMax){
                validFoci.add(new ItemStack(RegistryHandler.FOCUS_OF_ALCHEMY.get()));
            }
            validFoci.add(new ItemStack(RegistryHandler.FOCUS_OF_ADVANCED_ALCHEMY.get()));
        }
        else{
            validFoci.add(ItemStack.EMPTY);
        }
        inputs.add(validFoci);


        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);

        List<ItemStack> output = new ArrayList<>();
        output.add(o.output);
        iIngredients.setOutputs(VanillaTypes.ITEM, output);
    }

    public void setRecipe(IRecipeLayout recipeLayout, TransmuteRecipe o, IIngredients ingredients) {
        int index = 0;
        this.output = o.output;
        this.input = o.input;
        this.minAmp = o.minAmp;

        //position 0 will always be the list of actual inputs, position 1 is the foci
        recipeLayout.getItemStacks().init(index, true, 6, 2);
        recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(VanillaTypes.ITEM).get(0));
        ++index;

        //before foci I'm gonna add the amps real quick
        if(minAmp > 0) {
            ItemStack amps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AugmentAmplify.INSTANCE), minAmp);
            ItemStack advAmps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AdvancedAmplify.INSTANCE), Math.round(minAmp / 2f + 0.3f));
            List<ItemStack> ampsList = new ArrayList<>();
            ampsList.add(amps);
            ampsList.add(advAmps);

            recipeLayout.getItemStacks().init(index, true, 35, 14);
            recipeLayout.getItemStacks().set(index, ampsList);
            ++index;
        }

        //position 1 should be the foci...
        recipeLayout.getItemStacks().init(index, true, 65, 14);
        recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(VanillaTypes.ITEM).get(1));
        ++index;



        for(int i = 0; i < ingredients.getOutputs(VanillaTypes.ITEM).size(); ++i) {
            recipeLayout.getItemStacks().init(index, false, 100 + 10*i, 2);
            recipeLayout.getItemStacks().set(index, (List)ingredients.getOutputs(VanillaTypes.ITEM).get(i));
            ++index;
        }

    }
}

