package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TransmuteRecipe implements Recipe<Container> {
    public final ItemStack input;

    public final ItemStack output;

    public final boolean reversible;
    public final int minAmp;
    public final ResourceLocation id;

    public static final String RECIPE_ID = "transmute";

    public TransmuteRecipe(ResourceLocation id, ItemStack input, ItemStack output, boolean reversible, int minAmp) {
        this.input = input;
        this.output = output;
        this.reversible = reversible;
        this.minAmp = minAmp;
        this.id = id;
    }

    public TransmuteRecipe(String id, ItemStack input, ItemStack output, boolean reversible, int minAmp) {
        this(new ResourceLocation("arsomega", "transmute_" + id), input, output,reversible, minAmp);
    }

    public boolean matches(Container inventory, Level world) {
        return this.input.equals(inventory.getItem(0));
    }

    public boolean matches(ItemStack i, Level world) {
        return this.input.equals(i,false);
    }



    @Nonnull
    public ItemStack assemble(Container inventory) {
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
        return true;
    }

    @Nonnull
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return RegistryHandler.TRANSMUTE_SERIALIZER;
    }

    public RecipeType<?> getType() {
        return (RecipeType) Registry.RECIPE_TYPE.get(new ResourceLocation("arsomega", "transmute"));
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "arsomega:transmute");
        jsonobject.add("input", stackToJson(input));
        jsonobject.add("output", stackToJson(input));
        jsonobject.addProperty("reversible", reversible);
        jsonobject.addProperty("minAmp", minAmp);
        return jsonobject;
    }

    public static JsonElement stackToJson(ItemStack stack){
        JsonObject element = new JsonObject();
        element.addProperty("item", stack.getItem().getRegistryName().toString());
        element.addProperty("count", stack.getCount());
        return element;
    }
    public static ItemStack stackFromJson(JsonObject obj){
        int count = obj.has("count") ? GsonHelper.getAsInt(obj, "count") : 1;
        String itemId = GsonHelper.getAsString(obj, "item");
        ItemStack stack = new ItemStack((ItemLike)Registry.ITEM.get(new ResourceLocation(itemId)), count);
        return stack;
    }


    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<TransmuteRecipe> {
        public Serializer() {
        }

        public TransmuteRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack input = stackFromJson(json.getAsJsonObject("input"));
            ItemStack output = stackFromJson(json.getAsJsonObject("output"));
            boolean reversible = json.getAsJsonPrimitive("reversible").getAsBoolean();
            int minAmp = json.has("minAmp") ? GsonHelper.getAsInt(json, "minAmp") : 1;
            return new TransmuteRecipe(recipeId, input, output, reversible, minAmp);
        }

        public void toNetwork(FriendlyByteBuf buf, TransmuteRecipe recipe) {
            buf.writeItem(recipe.input);
            buf.writeItem(recipe.output);
            buf.writeBoolean(recipe.reversible);
            buf.writeInt(recipe.minAmp);
        }

        @Nullable
        public TransmuteRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack input = buffer.readItem();
            ItemStack output = buffer.readItem();
            boolean reversible = buffer.readBoolean();
            int minAmp = buffer.readInt();
            return new TransmuteRecipe(recipeId, input, output, reversible, minAmp);
        }
    }
}