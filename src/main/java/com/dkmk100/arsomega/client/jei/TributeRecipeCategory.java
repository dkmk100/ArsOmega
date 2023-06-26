package com.dkmk100.arsomega.client.jei;




import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.crafting.ConjuringRecipe;
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
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class TributeRecipeCategory implements IRecipeCategory<ConjuringRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("arsomega", "tribute");
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack output;

    public static final RecipeType<ConjuringRecipe> type = RecipeType.create("arsomega", "tribute", ConjuringRecipe.class);


    public TributeRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsOmega.MOD_ID,"ritual_tribute"))));
    }


    @Override
    public RecipeType<ConjuringRecipe> getRecipeType() {
        return type;
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
    public void setRecipe(IRecipeLayoutBuilder builder, ConjuringRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(Items.GOLD_INGOT,20));
        input.add(new ItemStack(Items.DIAMOND,7));
        input.add(new ItemStack(Items.EMERALD,20));

        builder.addSlot(RecipeIngredientRole.INPUT,6,5).addItemStacks(input);

        builder.addSlot(RecipeIngredientRole.OUTPUT,65,5).addItemStack(recipe.output);
    }
}
