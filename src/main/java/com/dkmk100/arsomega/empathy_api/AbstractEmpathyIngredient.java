package com.dkmk100.arsomega.empathy_api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class AbstractEmpathyIngredient {
    protected Supplier<Item> item;
    public String regName;
    public AbstractEmpathyIngredient(Supplier<Item> item){
        this.item = item;
    }

    public Item GetItem(){
        return item.get();
    }

    /**
     * called when an ingredient is thrown into an empathy spell.
     * @param currentSpell
     * The current spell the ingredient is being added to
     * @param currentInstance
     * The current ingedient instance for this ingredient type in the spell. Can be null, representing that the spell does not contain the ingredient yet.
     * @return
     * Whether the ingredient should be added to the spell. The default implementation returns true when the ingredient hasn't been added yet.
     */
    public AddResult canAdd(EmpathySpell currentSpell, @Nullable EmpathyIngredientInstance currentInstance){
        if(currentInstance != null  && currentInstance.getAmount() > 0){
            return new AddResult(false,"max amount reached");
        }
        return new AddResult(true,"added item");
    }

    public static class AddResult{
        public String message;
        public boolean succeded;
        public AddResult(boolean suc, String mes){
            this.succeded = suc;
            this.message = mes;
        }
    }

    /**
     * called when the ingredient is added to the spell.
     * @param currentSpell
     * @param currentInstance
     */
    public abstract void onAdd(EmpathySpell currentSpell, @NotNull EmpathyIngredientInstance currentInstance);

    /**
     * Called when an empathy link is first ativated on all relevant entities. Will be called on both logical sides.
     * @param target
     * The entity hit with the empathy link.
     * @param spell
     * The spell the ingredient is being used in
     * @param instance
     * The ingredient instance
     * @param percentage
     * Determines the amount of the link affecting an entity. Should be a value between 0 and 1, with 1 representing 100%.
     * In some cases may add up to more than 100% on a spell instance, however should be treated as if not.
     * @param affected
     * Determines if the link should affect the target. By default will be true for percentages greater than 0, but some spell implementations may break this rule.
     */
    public abstract void onCastEntity(LivingEntity target, EmpathySpell spell, EmpathyIngredientInstance instance, float percentage, boolean affected);
}
