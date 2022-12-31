package com.dkmk100.arsomega.empathy_components;

import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyIngredientInstance;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient.AddResult;

public abstract class GenericEmpathyIngredient extends AbstractEmpathyIngredient {

    ConcurrentLinkedQueue<AbstractEmpathyIngredient> incompatibleIngredients = new ConcurrentLinkedQueue<>();
    private final int maxAmount;

    public GenericEmpathyIngredient(Supplier<Item> item, int maxCount) {
        super(item);
        this.maxAmount = maxCount;
    }

    public GenericEmpathyIngredient addBothIncompatable(GenericEmpathyIngredient ingredient){
        ingredient.setIncompatible(this);
        this.setIncompatible(ingredient);
        return this;//so we can chain them
    }

    private void setIncompatible(AbstractEmpathyIngredient ingredient){
        this.incompatibleIngredients.add(ingredient);
    }

    @Override
    public AddResult canAdd(EmpathySpell currentSpell, @Nullable EmpathyIngredientInstance currentInstance) {
        Iterator<AbstractEmpathyIngredient> ing = incompatibleIngredients.iterator();
        while(ing.hasNext()) {
            AbstractEmpathyIngredient ingredient = ing.next();
            for(EmpathyIngredientInstance inst : currentSpell.getIngredients()) {
                if (inst.getIngredient() == ingredient && inst.getAmount() > 0) {
                    return new AddResult(false, "incompatible with ingredient: " + ForgeRegistries.ITEMS.getKey(ingredient.GetItem()).toString());
                }
            }
        }
        if(currentInstance != null && currentInstance.getAmount() >= maxAmount){
            return new AddResult(false,"max amount reached");
        }
        return new AddResult(true,"added item");
    }
}
