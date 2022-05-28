package com.dkmk100.arsomega.crafting;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class CustomRecipeType<T extends Recipe<?>> implements RecipeType<T> {
    public CustomRecipeType() {
    }

    public String toString() {
        return Registry.RECIPE_TYPE.getKey(this).toString();
    }
}