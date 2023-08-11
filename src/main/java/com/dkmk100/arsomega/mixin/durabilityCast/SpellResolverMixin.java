package com.dkmk100.arsomega.mixin.durabilityCast;

import com.dkmk100.arsomega.IReactiveFlag;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpellResolver.class)
public class SpellResolverMixin implements IReactiveFlag {
    boolean isReactive = false;
    ItemStack trueItem = ItemStack.EMPTY;

    @Override
    public void setReactive(boolean val) {
        isReactive = val;
    }

    @Override
    public boolean getValue() {
        return isReactive;
    }

    @Override
    public ItemStack getTrueItem() {
        return trueItem;
    }

    @Override
    public void setTrueItem(ItemStack trueItem) {
        this.trueItem = trueItem;
    }

    @ModifyVariable(at = @At("STORE"), remap = false, ordinal = 0, method = "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;enoughMana(Lnet/minecraft/world/entity/LivingEntity;)Z")
    int canCastCostModifier(int val){

        if(getValue()){
            ItemStack stack = getTrueItem();
            int enchantLevel = stack.getEnchantmentLevel(RegistryHandler.DURABILITY_CAST_ENCHANT.get());
            if(enchantLevel == 0){
                return val;
            }
            int durability = val - getCastCost(val, stack, enchantLevel);

            return Math.max(0, val - durability);
        }
        else{
            return val;
        }
    }

    int getCastCost(int val, ItemStack stack, int enchantLevel){
        int durability = stack.getMaxDamage() - stack.getDamageValue();

        int newCost = Math.max(0, val - durability);

        int durabilityCost = val - newCost;

        float multiplierDownPerLevel = 0.2f;

        if (durabilityCost > 0) {

            durabilityCost = Math.round(durabilityCost * (1f - multiplierDownPerLevel * (enchantLevel - 1)));

            //if there was originally a durability cost, always have a durability cost of at least 1
            durabilityCost = Math.max(durabilityCost, 1);
        }

        return durabilityCost;
    }

    @ModifyVariable(at = @At("STORE"), remap = false, ordinal = 0, method = "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;expendMana()V")
    int expendManaModifyCost(int val){
        LogManager.getLogger().info("expend mana");
        if(getValue()) {

            ItemStack stack = getTrueItem();
            int enchantLevel = stack.getEnchantmentLevel(RegistryHandler.DURABILITY_CAST_ENCHANT.get());
            if(enchantLevel == 0){
                return val;//don't provide discount
            }

            if(!stack.isDamageableItem() || stack.getDamageValue() >= stack.getMaxDamage()){
                return val;
            }

            int durability = stack.getMaxDamage() - stack.getDamageValue();

            //only remove a max of val mana from the cost obv
            int newCost = Math.max(0, val - durability);

            LivingEntity caster = ((SpellResolver) (Object) this).spellContext.getUnwrappedCaster();
            if (caster instanceof Player player && player.getAbilities().instabuild) {
                return newCost;//in creative mode just cast for free
            }

            int durabilityCost = getCastCost(val,stack,enchantLevel);

            //damage item by the amount of mana used from it
            stack.hurtAndBreak(durabilityCost, ((SpellResolver) (Object) this).spellContext.getUnwrappedCaster(), (t) -> {});

            //return the cost to finisht the method
            return newCost;

        }
        else{
            return val;
        }
    }

}
