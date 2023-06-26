package com.dkmk100.arsomega.client.jei;

import com.dkmk100.arsomega.crafting.EnchantRecipe;
import com.dkmk100.arsomega.glyphs.AdvancedAmplify;
import com.dkmk100.arsomega.glyphs.EnchantGlyph;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
  
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantRecipeCategory implements IRecipeCategory<EnchantRecipe>{
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack input;
    public ItemStack output;
    public int minLevel;

    public static final RecipeType<EnchantRecipe> type = RecipeType.create("arsomega", "enchant", EnchantRecipe.class);



    public EnchantRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(EnchantGlyph.INSTANCE)));
    }


    @Override
    public RecipeType<EnchantRecipe> getRecipeType() {
        return type;
    }

    public Component getTitle() {
        return Component.translatable("arsomega.enchant_recipe");
    }

    public IDrawable getBackground() {
        return this.helper.createBlankDrawable(150, 32);
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = new ArrayList();
        List<ItemStack> input = new ArrayList<>();
        input.add(recipe.input);
        inputs.add(input);

        int index = 0;
        this.output = recipe.output;
        this.input = recipe.input;
        this.minLevel = recipe.minLevel;

        //position 0 will always be the list of actual inputs
        builder.addSlot(RecipeIngredientRole.INPUT,6,2).addItemStacks(inputs.get(0));

        //calc required amps
        if(minLevel > 0) {
            int minAmp = EnchantGlyph.INSTANCE.getAmp(minLevel);
            ItemStack amps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AugmentAmplify.INSTANCE), minAmp);
            ItemStack advAmps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AdvancedAmplify.INSTANCE), Math.round(minAmp / 2f + 0.3f));
            List<ItemStack> ampsList = new ArrayList<>();
            ampsList.add(amps);
            ampsList.add(advAmps);

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY,49,14).addItemStacks(ampsList);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT,100,2).addItemStack(output);

        /*
        for(int i = 0; i < ingredients.getOutputs(VanillaTypes.ITEM).size(); ++i) {
            recipeLayout.getItemStacks().init(index, false, 100 + 10*i, 2);
            recipeLayout.getItemStacks().set(index, (List)ingredients.getOutputs(VanillaTypes.ITEM).get(i));
            ++index;
        }
        */

    }
}

