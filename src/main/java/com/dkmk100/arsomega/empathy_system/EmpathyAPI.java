package com.dkmk100.arsomega.empathy_system;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EmpathyAPI {
    public static final EmpathyAPI INSTANCE = new EmpathyAPI();
    private ConcurrentHashMap<String, AbstractEmpathyIngredient> ingredientMap =  new ConcurrentHashMap();
    public Map<String, AbstractEmpathyIngredient> getIngredientMap(){
        return this.ingredientMap;
    }

    public void RegisterIngredient(String id, AbstractEmpathyIngredient ingredient){
        Objects.requireNonNull(ingredient,"Ingredient was null");
        ingredientMap.put(id,ingredient);
    }

}
