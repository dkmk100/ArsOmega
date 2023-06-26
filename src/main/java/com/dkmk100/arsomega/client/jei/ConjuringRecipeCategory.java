package com.dkmk100.arsomega.client.jei;



import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.crafting.ConjuringRecipe;
import com.dkmk100.arsomega.crafting.EnchantRecipe;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
  
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConjuringRecipeCategory implements IRecipeCategory<ConjuringRecipe> {
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack output;

    public static final RecipeType<ConjuringRecipe> type = RecipeType.create("arsomega", "conjuring", ConjuringRecipe.class);


    public ConjuringRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsOmega.MOD_ID,"ritual_conjuring"))));
    }

    @Override
    public RecipeType<ConjuringRecipe> getRecipeType() {
        return type;
    }

    public Component getTitle() {
        return Component.translatable("arsomega.conjuring_recipe");
    }

    public IDrawable getBackground() {
        return this.helper.createBlankDrawable(120, 30);
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ConjuringRecipe recipe, IFocusGroup focuses) {

        ItemStack input = recipe.output.copy();
        input.setCount(1);
        ItemStack output = recipe.output.copy();
        output.setCount(3);

        builder.addSlot(RecipeIngredientRole.INPUT,10,5).addItemStack(input);

        builder.addSlot(RecipeIngredientRole.OUTPUT,70,5).addItemStack(output);
    }
}

