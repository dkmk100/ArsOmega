package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnchantRecipe implements Recipe<Container> {
    public final ItemStack input;

    public final ItemStack output;

    public final int minLevel;
    public final ResourceLocation id;

    public static final String RECIPE_ID = "enchant";

    public EnchantRecipe(ResourceLocation id, ItemStack input, ItemStack output, int minLevel) {
        this.input = input;
        this.output = output;
        this.minLevel = minLevel;
        this.id = id;
    }

    public EnchantRecipe(String id, ItemStack input, ItemStack output, int minAmp) {
        this(new ResourceLocation("arsomega", "enchant_" + id), input, output, minAmp);
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
        return RegistryHandler.ENCHANT_SERIALIZER;
    }

    public RecipeType<?> getType() {
        return (RecipeType) Registry.RECIPE_TYPE.get(new ResourceLocation("arsomega", "enchant"));
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "arsomega:enchant");
        jsonobject.add("input", stackToJson(input));
        jsonobject.add("output", stackToJson(input));
        jsonobject.addProperty("minLevel", minLevel);
        return jsonobject;
    }

    public static JsonElement stackToJson(ItemStack stack){
        JsonObject element = new JsonObject();
        element.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
        element.addProperty("count", stack.getCount());
        return element;
    }
    public static ItemStack stackFromJson(JsonObject obj){
        int count = obj.has("count") ? GsonHelper.getAsInt(obj, "count") : 1;
        String itemId = GsonHelper.getAsString(obj, "item");
        ItemStack stack = new ItemStack(Registry.ITEM.get(new ResourceLocation(itemId)), count);
        return stack;
    }


    public static class Serializer implements RecipeSerializer<EnchantRecipe> {
        public Serializer() {
        }

        public EnchantRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack input = stackFromJson(json.getAsJsonObject("input"));
            ItemStack output = stackFromJson(json.getAsJsonObject("output"));
            int minLevel = json.has("minLevel") ? GsonHelper.getAsInt(json, "minLevel") : 1;
            return new EnchantRecipe(recipeId, input, output, minLevel);
        }

        public void toNetwork(FriendlyByteBuf buf, EnchantRecipe recipe) {
            buf.writeItem(recipe.input);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.minLevel);
        }

        @Nullable
        public EnchantRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack input = buffer.readItem();
            ItemStack output = buffer.readItem();
            int minLevel = buffer.readInt();
            return new EnchantRecipe(recipeId, input, output, minLevel);
        }
    }
}