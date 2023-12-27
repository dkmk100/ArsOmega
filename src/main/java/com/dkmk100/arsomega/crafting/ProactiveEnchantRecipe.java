package com.dkmk100.arsomega.crafting;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.enchants.ProactiveSpellcaster;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProactiveEnchantRecipe extends EnchantmentRecipe {
    public static final String RECIPE_ID = "proactive_enchant";

    public ProactiveEnchantRecipe(ResourceLocation id, List<Ingredient> pedestalItems, int cost) {
        super(pedestalItems,RegistryHandler.PROACTIVE_ENCHANT.get(),1,cost);
        this.pedestalItems = pedestalItems;
        this.id = id;
        this.sourceCost = cost;
    }

    public ProactiveEnchantRecipe(List<Ingredient> pedestalItems) {
        super(pedestalItems,RegistryHandler.PROACTIVE_ENCHANT.get(),1,0);
        this.pedestalItems = pedestalItems;
        this.id = new ResourceLocation("arsomega", "make_proactive_default");
    }

    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        ItemStack parchment = ReactiveEnchantmentRecipe.getParchment(pedestalItems);
        return super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player) && !parchment.isEmpty() && !CasterUtil.getCaster(parchment).getSpell().isEmpty();
    }



    public boolean doesReagentMatch(ItemStack reag) {
        return true;
    }

    public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile) {
        ItemStack resultStack = super.getResult(pedestalItems, reagent, enchantingApparatusTile);
        ItemStack parchment = ReactiveEnchantmentRecipe.getParchment(pedestalItems);
        ISpellCaster caster = CasterUtil.getCaster(parchment);
        ProactiveSpellcaster proCaster = new ProactiveSpellcaster(resultStack);
        proCaster.setColor(caster.getColor());
        proCaster.setSpell(caster.getSpell());
        return proCaster.stack.copy();
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "arsomega:proactive_enchant");
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

    public static class Serializer implements RecipeSerializer<ProactiveEnchantRecipe> {
        public Serializer() {
        }

        @Override
        public ProactiveEnchantRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int cost = json.has("sourceCost") ? GsonHelper.getAsInt(json, "sourceCost") : 0;
            JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
            List<Ingredient> stacks = new ArrayList();

            //ArsOmega.LOGGER.info("parsing recipe: "+recipeId.toString());

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
            //ArsOmega.LOGGER.info("pedestal items: "+pedestalItems);


            return new ProactiveEnchantRecipe(recipeId, stacks, cost);
        }

        @Nullable
        public ProactiveEnchantRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
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
            return new ProactiveEnchantRecipe(recipeId, stacks, cost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ProactiveEnchantRecipe recipe) {
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