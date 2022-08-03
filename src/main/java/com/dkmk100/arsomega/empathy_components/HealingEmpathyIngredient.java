package com.dkmk100.arsomega.empathy_components;

import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyIngredientInstance;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public class HealingEmpathyIngredient extends GenericEmpathyIngredient {
    int amount = 0;

    //NOTE: duration multiplied by the amount of the ingredient...
    final HarmingEmpathyIngredient.EffectInterface effect;

    public HealingEmpathyIngredient(Supplier<Item> item, int amount) {
        this(item,amount,1,null);
    }

    public HealingEmpathyIngredient(Supplier<Item> item, int amount, int maxAmount) {
        this(item,amount,maxAmount,null);
    }

    public HealingEmpathyIngredient(Supplier<Item> item, int amount, HarmingEmpathyIngredient.EffectInterface instance) {
        this(item,amount,1,instance);
    }

    public HealingEmpathyIngredient(Supplier<Item> item, int amount, int maxAmount, HarmingEmpathyIngredient.EffectInterface instance) {
        super(item,maxAmount);
        this.amount = amount;
        this.effect = instance;
    }

    @Override
    public void onAdd(EmpathySpell currentSpell, @NotNull EmpathyIngredientInstance currentInstance) {
        currentSpell.healing += amount;
    }

    @Override
    public void onCastEntity(LivingEntity target, EmpathySpell spell, EmpathyIngredientInstance instance, float percentage, boolean affected) {
        if(effect!=null && affected) {
            target.addEffect(new MobEffectInstance(effect.apply(instance.getAmount(),percentage)), spell.getCaster());
        }
    }
}
