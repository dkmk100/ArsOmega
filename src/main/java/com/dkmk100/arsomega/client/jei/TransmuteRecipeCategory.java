package com.dkmk100.arsomega.client.jei;

import com.dkmk100.arsomega.crafting.TransmuteRecipe;
import com.dkmk100.arsomega.glyphs.AdvancedAmplify;
import com.dkmk100.arsomega.glyphs.TransmuteGlyph;
import com.dkmk100.arsomega.util.RegistryHandler;
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
  
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipeCategory implements IRecipeCategory<TransmuteRecipe>{
    //public static final ResourceLocation UID = new ResourceLocation("arsomega", "transmute");
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack input;
    public ItemStack output;
    public static final RecipeType<TransmuteRecipe> type = RecipeType.create("arsomega", "transmute", TransmuteRecipe.class);
    public int minAmp;

    public TransmuteRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(TransmuteGlyph.INSTANCE)));
    }

    @Override
    public RecipeType<TransmuteRecipe> getRecipeType() {
        return type;
    }

    public Component getTitle() {
        return Component.translatable("arsomega.transmute_recipe");
    }

    public IDrawable getBackground() {
        return this.helper.createBlankDrawable(150, 32);
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TransmuteRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = new ArrayList();
        List<ItemStack> input = new ArrayList<>();
        input.add(recipe.input);
        inputs.add(input);

        List<ItemStack> validFoci = new ArrayList<>();
        if(recipe.minAmp>TransmuteGlyph.normalMax){
            if(recipe.minAmp<=TransmuteGlyph.focusMax){
                validFoci.add(new ItemStack(RegistryHandler.FOCUS_OF_ALCHEMY.get()));
            }
            validFoci.add(new ItemStack(RegistryHandler.FOCUS_OF_ADVANCED_ALCHEMY.get()));
        }
        else{
            validFoci.add(ItemStack.EMPTY);
        }
        inputs.add(validFoci);

        int index = 0;
        this.output = recipe.output;
        this.input = recipe.input;
        this.minAmp = recipe.minAmp;

        //position 0 will always be the list of actual inputs, position 1 is the foci
        builder.addSlot(RecipeIngredientRole.INPUT,6, 2).addItemStacks(inputs.get(0));

        //before foci I'm gonna add the amps real quick
        if(minAmp > 0) {
            ItemStack amps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AugmentAmplify.INSTANCE), minAmp);
            ItemStack advAmps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AdvancedAmplify.INSTANCE), Math.round(minAmp / 2f + 0.3f));
            List<ItemStack> ampsList = new ArrayList<>();
            ampsList.add(amps);
            ampsList.add(advAmps);

            //render amps needed
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY,35, 14).addItemStacks(ampsList);
        }

        //render foci
        builder.addSlot(RecipeIngredientRole.CATALYST,65, 14).addItemStacks(inputs.get(1));

        //output
        builder.addSlot(RecipeIngredientRole.OUTPUT,100, 2).addItemStack(output);


        /*
        for(int i = 0; i < output.size(); ++i) {
            recipeLayout.getItemStacks().init(index, false, 100 + 10*i, 2);
            recipeLayout.getItemStacks().set(index, (List)inputs.get(i));
            ++index;
        }
         */

    }
}


