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

public class HarmingEmpathyIngredient extends AbstractEmpathyIngredient {
    int amount = 0;
    final int maxAmount;

    @FunctionalInterface
    public interface EffectInterface{
        MobEffectInstance apply(int amount, float mult);
    }
    final EffectInterface effect;
    public HarmingEmpathyIngredient(Item item, int amount) {
        this(item,amount,1,null);
    }

    public HarmingEmpathyIngredient(Item item, int amount, int maxAmount) {
        this(item,amount,maxAmount,null);
    }

    public HarmingEmpathyIngredient(Item item, int amount, EffectInterface instance) {
        this(item,amount,1,instance);
    }

    public HarmingEmpathyIngredient(Item item, int amount, int maxAmount, EffectInterface instance) {
        super(item);
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.effect = instance;
    }

    @Override
    public boolean canAdd(EmpathySpell currentSpell, @Nullable EmpathyIngredientInstance currentInstance) {
        return currentInstance == null || currentInstance.getAmount() < maxAmount;
    }

    @Override
    public void onAdd(EmpathySpell currentSpell, @NotNull EmpathyIngredientInstance currentInstance) {
        currentSpell.damage += amount;
    }

    @Override
    public void onCastEntity(LivingEntity target, EmpathySpell spell, EmpathyIngredientInstance instance, float percentage, boolean affected) {
        if(effect!=null && affected) {
            target.addEffect(new MobEffectInstance(effect.apply(instance.getAmount(),percentage)), spell.getCaster());
        }
    }
}
