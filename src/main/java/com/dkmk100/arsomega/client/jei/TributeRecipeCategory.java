package com.dkmk100.arsomega.client.jei;


/*
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.crafting.ConjuringRecipe;
import com.dkmk100.arsomega.crafting.TransmuteRecipe;
import com.dkmk100.arsomega.glyphs.TransmuteGlyph;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.client.jei.JEIConstants;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
  
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class TributeRecipeCategory implements IRecipeCategory<ConjuringRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("arsomega", "tribute");
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack output;

    public TributeRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get("tribute")));
    }

    public ResourceLocation getUid() {
        return UID;
    }

    public Class getRecipeClass() {
        return ConjuringRecipe.class;
    }

    public Component getTitle() {
        return Component.translatable("arsomega.tribute_recipe");
    }

    public IDrawable getBackground() {
        return this.helper.createBlankDrawable(120, 30);
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(ConjuringRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> inputs = new ArrayList();
        List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(Items.GOLD_INGOT,20));
        input.add(new ItemStack(Items.DIAMOND,7));
        input.add(new ItemStack(Items.EMERALD,20));
        inputs.add(input);


        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);

        List<List<ItemStack>> outss = new ArrayList();
        List<ItemStack> output = new ArrayList<>();
        output.add(o.output);
        List<ItemStack> output2 = new ArrayList<>();
        output2.add(new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get("tribute")));

        outss.add((output2));
        outss.add((output));
        iIngredients.setOutputLists(VanillaTypes.ITEM, outss);
    }

    public void setRecipe(IRecipeLayout recipeLayout, ConjuringRecipe o, IIngredients ingredients) {
        int index = 0;
        this.output = o.output;

        //position 0 will always be the list of actual inputs, position 1 is the foci
        recipeLayout.getItemStacks().init(index, true, 6, 5);
        recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(VanillaTypes.ITEM).get(0));
        ++index;

        for(int i = 0; i < ingredients.getOutputs(VanillaTypes.ITEM).size(); ++i) {
            recipeLayout.getItemStacks().init(index, false, 65 + 22*i, 5);
            recipeLayout.getItemStacks().set(index, (List)ingredients.getOutputs(VanillaTypes.ITEM).get(i));
            ++index;
        }
    }
}
 */
