package com.dkmk100.arsomega.empathy_api;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A new instance of
 */
public class EmpathySpell {

    List<EmpathyIngredientInstance> ingredients = new ArrayList<>();
    private LivingEntity caster = null;

    //stats, these are the numbers we wanna tweak:
    public float damage = 0;
    public float healing = 0;
    public float damageMult = 1;
    public float healingMult = 1;

    public double targetWeight = 1;
    public double casterWeight = 0;
    public boolean needsCaster = false;

    public boolean announce = true;


    boolean finalized = false;
    public EmpathySpell(){

    }
    public EmpathySpell(CompoundTag tag, Level world){
        damage = tag.getFloat("damage");
        healing = tag.getFloat("healing");
        damageMult = tag.getFloat("damageMult");
        healingMult = tag.getFloat("healingMult");
        targetWeight = tag.getFloat("targetWeight");
        casterWeight = tag.getFloat("casterWeight");
        finalized = tag.contains("finalized") ? tag.getBoolean("finalized") : false;
        needsCaster = tag.contains("needsCaster") ? tag.getBoolean("needsCaster") : false;
        caster = tag.hasUUID("caster") && world instanceof ServerLevel ? world.getServer().getPlayerList().getPlayer(tag.getUUID("caster")) : null;
        announce = tag.contains("announce") ? tag.getBoolean("announce") : true;
        int count = tag.getInt("itemCount");
        for(int i=0;i<count;i++){
            ingredients.add( new EmpathyIngredientInstance(tag.getCompound("item_"+i)));
        }
    }

    public void WriteTo(CompoundTag tag){
        tag.putFloat("damage",damage);
        tag.putFloat("healing",healing);
        tag.putFloat("damageMult",damageMult);
        tag.putFloat("healingMult",healingMult);
        tag.putDouble("targetWeight",targetWeight);
        tag.putDouble("casterWeight",casterWeight);
        tag.putInt("itemCount",ingredients.size());
        tag.putBoolean("finalized",finalized);
        tag.putBoolean("needsCaster",needsCaster);
        tag.putBoolean("announce",announce);
        if(caster!=null){
            tag.putUUID("caster",caster.getUUID());
        }
        int i = 0;
        for(EmpathyIngredientInstance instance : ingredients){
            tag.put("item_"+i,instance.toTag());
            i+=1;
        }
    }

    public List<EmpathyIngredientInstance> getIngredients(){
        return ingredients;
    }

    public void FinalizeSpell(LivingEntity caster){
        if(!finalized) {
            this.caster = caster;
            double totalWeight = casterWeight + targetWeight;
            if(totalWeight == 0){
                casterPercentage = 0;
                targetPercentage = 0;
            }
            else {
                double targetMult = targetWeight / totalWeight;
                double casterMult = casterWeight / totalWeight;
                casterPercentage = (float)(casterPercentage * casterMult);
                targetPercentage = (float)(targetPercentage * targetMult);
            }
            finalized = true;
        }
    }
    @Nullable
    public LivingEntity getCaster(){
        return caster;
    }

    public boolean isFinalized(){
        return finalized;
    }

    //sort of like casting, but the item does the actual casting later.
    public ItemStack applyToItem(ItemStack stack, float strength, float alignment){
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("hasEmpathy",true);
        CompoundTag spellTag = new CompoundTag();
        this.WriteTo(spellTag);
        tag.put("empathySpell",spellTag);
        tag.putFloat("empathyStrength",strength);
        tag.putFloat("empathyAlignment",alignment);
        return stack;
    }

    //negative alignment is evil, -1 is the curse altar
    public void CastSpell(@NotNull LivingEntity target, float strength, float alignment){
        if(needsCaster && caster == null){
            if(alignment < -0.1f) {
                PortUtil.sendMessage(target, "a curse cast on you failed");
            }
            else if(alignment > 0.1f){
                PortUtil.sendMessage(target, "a blessing cast on you failed");
            }
            else{
                PortUtil.sendMessage(target, "an empathy spell cast on you failed");
            }
            return;
        }
        if(finalized){
            if(caster!=null) {
                if (casterPercentage > 0) {
                    CastEffects(caster,casterPercentage * strength,true,alignment);
                } else {
                    CastEffects(caster,0,false,alignment);
                }
            }
            if (targetPercentage > 0) {
                CastEffects(target,targetPercentage * strength,true,alignment);
                if(announce){
                    if(alignment < -0.1f) {
                        PortUtil.sendMessage(target,"a curse has been cast on you!");
                    }
                    else if(alignment > 0.1f){
                        PortUtil.sendMessage(target,"a blessing has been cast on you!");
                    }
                    else{
                        PortUtil.sendMessage(target,"an empathy spell has been cast on you!");
                    }
                }
            } else {
                CastEffects(target,0,false,alignment);
            }
        }
        else{
            throw new IllegalStateException("");
        }
    }

    private void CastEffects(LivingEntity target, float strength,boolean affected, float alignment){
        //cast spell based on stats:
        if(affected && damage > 0) {
            float hurt = damage * damageMult * strength;

            //an alignemnt of -1 will double damage, and of 1 will remove it.
            hurt = Math.max(0,hurt * (1 + -1f*(alignment)));

            target.hurt(EntityDamageSource.indirectMagic(caster, caster), hurt);
        }
        if(affected && healing > 0){
            float heal = healing * healingMult * strength;

            //an alignemnt of 1 will double healing, and of -1 will remove it.
            heal = Math.max(0,heal * (1 + 1f*(alignment)));

            target.heal(heal);
        }

        //cast any other effects
        for(EmpathyIngredientInstance ingredient : ingredients){
            ingredient.getIngredient().onCastEntity(target,this,ingredient,strength,affected);
        }
    }

    protected float targetPercentage = 1.0f;
    protected float casterPercentage = 0.0f;



    public AbstractEmpathyIngredient.AddResult tryAddItem(Item item){
        AbstractEmpathyIngredient ingredient = EmpathyAPI.getIngredient(item);
        if(ingredient!=null) {
            return this.tryAdd(ingredient);
        }
        return new AbstractEmpathyIngredient.AddResult(false,"this item cannot be used as an ingredient");
    }

    public AbstractEmpathyIngredient.AddResult canAddIngredient(AbstractEmpathyIngredient ingredient){
        if(finalized)
            return new AbstractEmpathyIngredient.AddResult(false,"spell already finalized");
        EmpathyIngredientInstance instance = getIngredient(ingredient);

        return ingredient.canAdd(this,instance);
    }

    public EmpathyIngredientInstance getIngredient(AbstractEmpathyIngredient ingredient){
        EmpathyIngredientInstance instance = null;
        for(EmpathyIngredientInstance inst : ingredients){
            if(inst.getIngredient() == ingredient){
                instance = inst;
            }
        }
        return instance;
    }

    public int addIngredients(AbstractEmpathyIngredient ingredient, int count){
        for(int i=0;i<count;i++){
            if(!tryAdd(ingredient).succeded){
                return i;
            }
        }
        return count;
    }


    public AbstractEmpathyIngredient.AddResult tryAdd(AbstractEmpathyIngredient ingredient){
        if(finalized)
            return new AbstractEmpathyIngredient.AddResult(false,"spell already finalized");

        EmpathyIngredientInstance instance = null;
        for(EmpathyIngredientInstance inst : ingredients){
            if(inst.getIngredient() == ingredient){
                instance = inst;
            }
        }
        AbstractEmpathyIngredient.AddResult result = ingredient.canAdd(this,instance);
        if(result.succeded) {
            ArsOmega.LOGGER.info("success, adding...");
            if (instance == null) {
                instance = new EmpathyIngredientInstance(ingredient);
                this.ingredients.add(instance);
            }
            instance.AddIngredient(ingredient, this);
            ArsOmega.LOGGER.info("return true");
            return new AbstractEmpathyIngredient.AddResult(true,"added ingredient");
        }
        ArsOmega.LOGGER.info("no luck, nothignh added");
        return result;
    }
}
