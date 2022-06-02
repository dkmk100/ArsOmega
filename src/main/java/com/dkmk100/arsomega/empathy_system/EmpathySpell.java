package com.dkmk100.arsomega.empathy_system;

import joptsimple.util.KeyValuePair;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * A new instance of
 */
public class EmpathySpell {

    List<EmpathyIngredientInstance> ingredients;
    boolean finalized = false;

    boolean tryAddItem(Item item){
        for(AbstractEmpathyIngredient ingredient : EmpathyAPI.INSTANCE.getIngredientMap().values()){
            if(ingredient.item == item){
                return this.tryAdd(ingredient);
            }
        }
        return false;
    }

    boolean tryAdd(AbstractEmpathyIngredient ingredient){
        if(finalized)
            return false;

        EmpathyIngredientInstance instance = null;
        for(EmpathyIngredientInstance inst : ingredients){
            if(instance.getIngredient() == ingredient){
                instance = inst;
            }
        }
        if(ingredient.canAdd(this,instance)){
            if(instance == null){
                instance = new EmpathyIngredientInstance(ingredient);
            }
            else{
                instance.AddIngredient(ingredient,this);
            }
            return true;
        }
        return false;
    }

}
