package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.dkmk100.arsomega.util.SigilPattern;
import com.google.gson.JsonArray;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class SigilRecipe implements Recipe<Container> {
    public final ResourceLocation id;
    public final SigilPattern pattern;

    public final ItemStack output;


    public static final String RECIPE_ID = "sigil";

    public SigilRecipe(ResourceLocation id, SigilPattern pattern, ItemStack output){
        this.id = id;
        this.pattern = pattern;
        this.output = output;
    }

    public SigilRecipe(String name, SigilPattern pattern, ItemStack output){
        this(new ResourceLocation(ArsOmega.MOD_ID,"sigil_"+name),pattern,output);
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
        ItemStack stack = new ItemStack(Registry.ITEM.get(new ResourceLocation(itemId)), count);
        return stack;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SigilRecipe> {
        public Serializer() {
        }

        public SigilRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            boolean[][] recipe;
            JsonArray pattern = json.getAsJsonArray("pattern");

            recipe = new boolean[pattern.size()][];
            int tileY = pattern.size() / 2;
            int tileX = pattern.get(0).getAsString().length() / 2;

            int i2 = 0;
            for (JsonElement element : pattern) {
                String row = element.getAsString();
                recipe[i2] = new boolean[row.length()];
                for (int i = 0; i < row.length(); i++) {
                    char ch = row.charAt(i);
                    if (ch == 'x' || ch == 'X') {
                        recipe[i2][i] = true;
                    } else if (ch == ' ') {
                        recipe[i2][i] = false;
                    } else {
                        recipe[i2][i] = false;
                        tileX = i;
                        tileY = i2;
                    }
                }
                i2 += 1;
            }

            ItemStack output = stackFromJson(json.getAsJsonObject("output"));

            int sourceCost = json.has("sourceCost") ? json.get("sourceCost").getAsInt() : 0;

            return new SigilRecipe(recipeId, new SigilPattern(recipe,tileX,tileY,sourceCost), output);
        }


        public void toNetwork(FriendlyByteBuf buf, SigilRecipe recipe) {
            buf.writeItem(recipe.output);
            recipe.pattern.toNetwork(buf);
        }


        @Nullable
        public SigilRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack output = buffer.readItem();
            int sourceCost = buffer.readInt();
            int tileX = buffer.readInt();
            int tileY = buffer.readInt();
            int recipeSize = buffer.readInt();
            int size2 = buffer.readInt();

            boolean[][] recipe = new boolean[recipeSize][];
            for(int i=0;i<recipeSize;i++){
                recipe[i] = new boolean[size2];
                for(int i2=0;i2<size2;i2++) {
                    recipe[i][i2] = buffer.readBoolean();
                }
            }

            return new SigilRecipe(recipeId, new SigilPattern(recipe,tileX,tileY,sourceCost), output);
        }

    }

    @Override
    public boolean matches(Container p_44002_, Level p_44003_) {
        return true;
    }

    @Override
    public ItemStack assemble(Container p_44001_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegistryHandler.SIGIL_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return (RecipeType) Registry.RECIPE_TYPE.get(new ResourceLocation("arsomega", "sigil"));
    }
}
