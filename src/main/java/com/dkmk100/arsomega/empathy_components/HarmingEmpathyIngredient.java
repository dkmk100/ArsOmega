package com.dkmk100.arsomega.empathy_components;

import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyIngredientInstance;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public class HarmingEmpathyIngredient extends GenericEmpathyIngredient {
    int amount = 0;

    @FunctionalInterface
    public interface EffectInterface{
        MobEffectInstance apply(int amount, float mult);
    }
    @FunctionalInterface
    public interface AdvancedEffectInterface{
        MobEffectInstance apply(int amount, float mult, EmpathySpell spell);
    }
    AdvancedEffectInterface effect;
    public HarmingEmpathyIngredient(Supplier<Item> item, int amount) {
        this(item,amount,1, (EffectInterface) null);
    }

    public HarmingEmpathyIngredient(Supplier<Item> item, int amount, int maxAmount) {
        this(item,amount,maxAmount, (EffectInterface) null);
    }

    public HarmingEmpathyIngredient(Supplier<Item> item, int amount, EffectInterface instance) {
        this(item,amount,1,instance);
    }

    public HarmingEmpathyIngredient(Supplier<Item> item, int amount, int maxAmount, EffectInterface instance) {
        this(item,amount,maxAmount, instance == null ? null : (a, m, s) -> instance.apply(a,m));
    }

    public HarmingEmpathyIngredient(Supplier<Item> item, int amount, int maxAmount, AdvancedEffectInterface instance) {
        super(item,maxAmount);
        this.amount = amount;
        this.effect = instance;
    }

    public void setEffect(AdvancedEffectInterface effect){
        this.effect = effect;
    }

    @Override
    public void onAdd(EmpathySpell currentSpell, @NotNull EmpathyIngredientInstance currentInstance) {
        currentSpell.damage += amount;
    }

    @Override
    public void onCastEntity(LivingEntity target, EmpathySpell spell, EmpathyIngredientInstance instance, float percentage, boolean affected) {
        if(effect!=null && affected) {
            MobEffectInstance effect2 = effect.apply(instance.getAmount(),percentage,spell);
            if(effect2!=null) {
                target.addEffect(new MobEffectInstance(effect2), spell.getCaster());
            }
        }
    }
}
