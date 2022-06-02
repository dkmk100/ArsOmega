package com.dkmk100.arsomega.empathy_system;

import org.jetbrains.annotations.NotNull;

public class EmpathyIngredientInstance {
    AbstractEmpathyIngredient ingredient;
    int amount;

    /**
     * Note: does not add the ingredient automatically, the AddIngredient method must still be called.
     */
    public EmpathyIngredientInstance(@NotNull AbstractEmpathyIngredient ingredient){
        this.ingredient = ingredient;
        amount = 0;
    }
    public AbstractEmpathyIngredient getIngredient(){
        return ingredient;
    }
    public int getAmount(){
        return amount;
    }
    public void AddIngredient(AbstractEmpathyIngredient ingredient, EmpathySpell spell){
        ingredient.onAdd(spell,this);
    }
}
