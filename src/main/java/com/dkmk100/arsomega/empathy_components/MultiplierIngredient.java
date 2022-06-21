package com.dkmk100.arsomega.empathy_components;

import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyIngredientInstance;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MultiplierIngredient extends AbstractEmpathyIngredient {
    float amount = 0;
    final int maxAmount;

    final boolean affectHarm;
    final boolean affectHeal;

    public MultiplierIngredient(Item item, float amount) {
        this(item,amount,1,true,true);
    }

    public MultiplierIngredient(Item item, float amount, int maxAmount) {
        this(item,amount,maxAmount,true,true);
    }

    public MultiplierIngredient(Item item, float amount, boolean affectHeal, boolean affectHarm) {
        this(item,amount,1, affectHarm, affectHeal);
    }

    public MultiplierIngredient(Item item, float amount, int maxAmount, boolean affectHarm, boolean affectHeal) {
        super(item);
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.affectHarm = affectHarm;
        this.affectHeal = affectHeal;
    }

    @Override
    public boolean canAdd(EmpathySpell currentSpell, @Nullable EmpathyIngredientInstance currentInstance) {
        return currentInstance == null || currentInstance.getAmount() < maxAmount;
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
