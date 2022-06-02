package com.dkmk100.arsomega.client.jei;

import com.dkmk100.arsomega.crafting.EnchantRecipe;
import com.dkmk100.arsomega.glyphs.AdvancedAmplify;
import com.dkmk100.arsomega.glyphs.EnchantGlyph;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantRecipeCategory implements IRecipeCategory<EnchantRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("arsomega", "enchant");
    IGuiHelper helper;
    public IDrawable background;
    public IDrawable icon;
    public ItemStack input;
    public ItemStack output;
    public int minLevel;

    public EnchantRecipeCategory(final IGuiHelper helper) {
        this.helper = helper;
        this.background = helper.createBlankDrawable(60, 50);
        this.icon = helper.createDrawableIngredient(new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(EnchantGlyph.INSTANCE)));
    }

    public ResourceLocation getUid() {
        return UID;
    }

    public Class getRecipeClass() {
        return EnchantRecipe.class;
    }

    public Component getTitle() {
        return new TranslatableComponent("arsomega.enchant_recipe");
    }

    public IDrawable getBackground() {
        return this.helper.createBlankDrawable(150, 32);
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(EnchantRecipe o, IIngredients iIngredients) {
        List<List<ItemStack>> inputs = new ArrayList();
        List<ItemStack> input = new ArrayList<>();
        input.add(o.input);
        inputs.add(input);


        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);

        List<ItemStack> output = new ArrayList<>();
        output.add(o.output);
        iIngredients.setOutputs(VanillaTypes.ITEM, output);
    }

    public void setRecipe(IRecipeLayout recipeLayout, EnchantRecipe o, IIngredients ingredients) {
        int index = 0;
        this.output = o.output;
        this.input = o.input;
        this.minLevel = o.minLevel;

        //position 0 will always be the list of actual inputs
        recipeLayout.getItemStacks().init(index, true, 6, 2);
        recipeLayout.getItemStacks().set(index, (List)ingredients.getInputs(VanillaTypes.ITEM).get(0));
        ++index;

        //calc required amps
        if(minLevel > 0) {
            int minAmp = EnchantGlyph.INSTANCE.getAmp(minLevel);
            ItemStack amps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AugmentAmplify.INSTANCE), minAmp);
            ItemStack advAmps = new ItemStack(ArsNouveauAPI.getInstance().getGlyphItem(AdvancedAmplify.INSTANCE), Math.round(minAmp / 2f + 0.3f));
            List<ItemStack> ampsList = new ArrayList<>();
            ampsList.add(amps);
            ampsList.add(advAmps);

            recipeLayout.getItemStacks().init(index, true, 49, 13);
            recipeLayout.getItemStacks().set(index, ampsList);
            ++index;
        }

        for(int i = 0; i < ingredients.getOutputs(VanillaTypes.ITEM).size(); ++i) {
            recipeLayout.getItemStacks().init(index, false, 100 + 10*i, 2);
            recipeLayout.getItemStacks().set(index, (List)ingredients.getOutputs(VanillaTypes.ITEM).get(i));
            ++index;
        }

    }
}

