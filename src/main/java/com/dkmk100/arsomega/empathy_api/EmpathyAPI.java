package com.dkmk100.arsomega.empathy_api;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EmpathyAPI {
    public static final EmpathyAPI INSTANCE = new EmpathyAPI();
    private ConcurrentHashMap<String, AbstractEmpathyIngredient> ingredientMap =  new ConcurrentHashMap();
    public Map<String, AbstractEmpathyIngredient> getIngredientMap(){
        return this.ingredientMap;
    }

    public void RegisterIngredient(String id, AbstractEmpathyIngredient ingredient){
        Objects.requireNonNull(ingredient,"Ingredient was null");
        ingredientMap.put(id,ingredient);
        ingredient.regName = id;
    }

    @Nullable
    public static AbstractEmpathyIngredient getIngredient(Item item){
        for(AbstractEmpathyIngredient ingredient : INSTANCE.ingredientMap.values()){
            ArsOmega.LOGGER.info("checking ingredient: " + ingredient.GetItem() + " with item: "+item);
            if(ingredient.GetItem() == item){
                return ingredient;
            }
        }
        return null;
    }

    @Nullable
    public static AbstractEmpathyIngredient getIngredient(String regName){
        return INSTANCE.ingredientMap.get(regName);
    }

}
