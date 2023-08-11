package com.dkmk100.arsomega.items;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.common.items.curios.AbstractManaCurio;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MagicCurio extends BasicItem implements ICurioItem, IManaEquipment, IManaDiscountEquipment {
    int boost;
    int regen;

    int discount;

    public MagicCurio(Properties properties, int boost, int regen) {
        super(properties);
        this.boost = boost;
        this.regen = regen;
        this.discount = 0;
    }

    public MagicCurio(Properties properties, int boost, int regen, int discount) {
        super(properties);
        this.boost = boost;
        this.regen = regen;
        this.discount = discount;
    }



    @Override
    public int getManaRegenBonus(ItemStack stack) {
        return regen;
    }
    @Override
    public int getMaxManaBoost(ItemStack stack) {
        return boost;
    }

    @Override
    public int getManaDiscount(ItemStack stack) {
        return discount;
    }

    @Override
    public int getManaDiscount(ItemStack stack, Spell spell) {
        return getManaDiscount(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        if(getManaDiscount(stack) > 0) {
            tooltip2.add(Component.translatable("tooltip.discount_item", new Object[]{this.getManaDiscount(stack)}));
        }
    }
}
