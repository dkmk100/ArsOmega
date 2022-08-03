package com.dkmk100.arsomega.empathy_components;

import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyIngredientInstance;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


public class MultiplierIngredient extends GenericEmpathyIngredient {
    float amount = 0;

    final boolean affectHarm;
    final boolean affectHeal;

    public MultiplierIngredient(Supplier<Item> item, float amount) {
        this(item,amount,1,true,true);
    }

    public MultiplierIngredient(Supplier<Item> item, float amount, int maxAmount) {
        this(item,amount,maxAmount,true,true);
    }

    public MultiplierIngredient(Supplier<Item> item, float amount, boolean affectHeal, boolean affectHarm) {
        this(item,amount,1, affectHarm, affectHeal);
    }

    public MultiplierIngredient(Supplier<Item> item, float amount, int maxAmount, boolean affectHarm, boolean affectHeal) {
        super(item,maxAmount);
        this.amount = amount;
        this.affectHarm = affectHarm;
        this.affectHeal = affectHeal;
    }

    @Override
    public void onAdd(EmpathySpell currentSpell, @NotNull EmpathyIngredientInstance currentInstance) {
        if(affectHarm) {
            currentSpell.damageMult += amount;
        }
        if(affectHeal){
            currentSpell.healingMult += amount;
        }
    }

    @Override
    public void onCastEntity(LivingEntity target, EmpathySpell spell, EmpathyIngredientInstance instance, float percentage, boolean affected) {
        //nothing to do here lol
    }
}
