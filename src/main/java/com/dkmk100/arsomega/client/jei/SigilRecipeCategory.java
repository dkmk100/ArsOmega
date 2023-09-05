package com.dkmk100.arsomega.client.jei;

import com.dkmk100.arsomega.crafting.SigilRecipe;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SigilRecipeCategory implements IRecipeCategory<SigilRecipe> {
    public IDrawable background;
    public IDrawable icon;

    public static final RecipeType<SigilRecipe> type = RecipeType.create("arsomega", "sigil_shaping", SigilRecipe.class);

    public SigilRecipeCategory(IGuiHelper helper){
        background = helper.createBlankDrawable(150, 125);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation("arsomega","ritual_shaping")).getDefaultInstance());
    }


    @Override
    public RecipeType<SigilRecipe> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("arsomega.sigil_shaping_recipe");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(SigilRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().fontFilterFishy;
        for(int i=0;i<recipe.pattern.sizeY;i++) {
            int x=0;
            for(char ch : recipe.pattern.getPatternRow(i).toCharArray()){
                font.draw(stack, new String(new char[]{ch}), 8+x, 20 + 8*i, 0);
                x+=5;
            }

        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SigilRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 75,0).addItemStack(recipe.output);
    }
}
