package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.enchants.ProactiveSpellcaster;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WriteProactiveRecipe extends EnchantingApparatusRecipe {
    public static final String RECIPE_ID = "write_proactive";

    public WriteProactiveRecipe(ResourceLocation id, List<Ingredient> pedestalItems, int cost) {
        this.pedestalItems = pedestalItems;
        this.id = id;
        this.sourceCost = cost;
    }

    public WriteProactiveRecipe(List<Ingredient> pedestalItems) {
        this.pedestalItems = pedestalItems;
        this.id = new ResourceLocation("arsomega", "write_proactive_default");
    }

    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        int level = (Integer) EnchantmentHelper.getEnchantments(reagent).getOrDefault(RegistryHandler.PROACTIVE_ENCHANT.get(), 0);
        ItemStack parchment = ReactiveEnchantmentRecipe.getParchment(pedestalItems);
        return !parchment.isEmpty() && !CasterUtil.getCaster(parchment).getSpell().isEmpty() && level > 0 && super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player);
    }



    public boolean doesReagentMatch(ItemStack reag) {
        return true;
    }

    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack parchment = ReactiveEnchantmentRecipe.getParchment(pedestalItems);
        ISpellCaster caster = CasterUtil.getCaster(parchment);
        ProactiveSpellcaster proCaster = new ProactiveSpellcaster(reagent);
        proCaster.setColor(caster.getColor());
        proCaster.setSpell(caster.getSpell());
        return proCaster.stack.copy();
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "arsomega:write_proactive");
        jsonobject.addProperty("sourceCost", this.getSourceCost());
        JsonArray pedestalArr = new JsonArray();
        Iterator var3 = this.pedestalItems.iterator();

        while(var3.hasNext()) {
            Ingredient i = (Ingredient)var3.next();
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }

        jsonobject.add("pedestalItems", pedestalArr);
        return jsonobject;
    }

    public RecipeType<?> getType() {
        return (RecipeType) Registry.RECIPE_TYPE.get(new ResourceLocation(ArsOmega.MOD_ID, RECIPE_ID));
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<WriteProactiveRecipe> {
        public Serializer() {
        }

        @Override
        public WriteProactiveRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int cost = json.has("sourceCost") ? GsonHelper.getAsInt(json, "sourceCost") : 0;
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = new ArrayList();

            Ingredient input;
            for(Iterator var9 = pedestalItems.iterator(); var9.hasNext(); stacks.add(input)) {
                JsonElement e = (JsonElement)var9.next();
                JsonObject obj = e.getAsJsonObject();
                input = null;
                if (GsonHelper.isArrayNode(obj, "item")) {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonArray(obj, "item"));
                } else {
                    input = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "item"));
                }
            }

            return new WriteProactiveRecipe(recipeId, stacks, cost);
        }

        @Nullable
        public WriteProactiveRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int length = buffer.readInt();
            List<Ingredient> stacks = new ArrayList();

            int cost;
            for(cost = 0; cost < length; ++cost) {
                try {
                    stacks.add(Ingredient.fromNetwork(buffer));
                } catch (Exception var7) {
                    var7.printStackTrace();
                    break;
                }
            }

            cost = buffer.readInt();
            return new WriteProactiveRecipe(recipeId, stacks, cost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WriteProactiveRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            Iterator var3 = recipe.pedestalItems.iterator();

            while(var3.hasNext()) {
                Ingredient i = (Ingredient)var3.next();
                i.toNetwork(buf);
            }

            buf.writeInt(recipe.sourceCost);
        }
    }
}
