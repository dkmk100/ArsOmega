package com.dkmk100.arsomega.client.jei;

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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConjuringRecipeCategory  implements IRecipeCategory<ConjuringRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("arsomega", "conjuring");
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack output;

    public ConjuringRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get("conjuring")));
    }

    public ResourceLocation getUid() {
        return UID;
    }

    public Class getRecipeClass() {
        return ConjuringRecipe.class;
    }

    public Component getTitle() {
        return new TranslatableComponent("arsomega.conjuring_recipe");
    }

    public IDrawable getBackground() {
        return this.helper.createBlankDrawable(120, 30);
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(ConjuringRecipe o, IIngredients iIngredients) {
        List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(o.output.getItem(),1));


        iIngredients.setInputs(VanillaTypes.ITEM, input);

        List<ItemStack> output = new ArrayList<>();
        output.add(new ItemStack(o.output.getItem(),3));
        iIngredients.setOutputs(VanillaTypes.ITEM, output);
    }

    public void setRecipe(IRecipeLayout recipeLayout, ConjuringRecipe o, IIngredients ingredients) {
        int index = 0;
        this.output = o.output;

        recipeLayout.getItemStacks().init(index, true, 10, 5);
        recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(VanillaTypes.ITEM).get(0));
        ++index;

        for(int i = 0; i < ingredients.getOutputs(VanillaTypes.ITEM).size(); ++i) {
            recipeLayout.getItemStacks().init(index, false, 70 + 10*i, 5);
            recipeLayout.getItemStacks().set(index, (List)ingredients.getOutputs(VanillaTypes.ITEM).get(i));
            ++index;
        }

    }
}

