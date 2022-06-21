package com.dkmk100.arsomega.empathy_api;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EmpathyIngredientInstance {
    final AbstractEmpathyIngredient ingredient;
    int amount;

    public CompoundTag toTag(){
        CompoundTag tag = new CompoundTag();
        tag.putString("name",ingredient.regName);
        tag.putInt("amount",amount);
        return tag;
    }
    public EmpathyIngredientInstance(CompoundTag tag){
        this.ingredient = EmpathyAPI.getIngredient(tag.getString("name"));
        this.amount = tag.getInt("amount");
    }

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
        amount+=1;
    }
}
