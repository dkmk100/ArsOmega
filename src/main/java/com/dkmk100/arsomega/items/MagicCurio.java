package com.dkmk100.arsomega.items;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.common.items.curios.AbstractManaCurio;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MagicCurio extends ArsNouveauCurio implements IManaEquipment {
    int boost;
    int regen;

    int discount;
    public MagicCurio(String reg, int boost, int regen) {
        super();
        this.setRegistryName(reg);
        this.boost = boost;
        this.regen = regen;
        this.discount = 0;
    }

    public MagicCurio(String reg, int boost, int regen, int discount) {
        super();
        this.setRegistryName(reg);
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
    public void wearableTick(LivingEntity livingEntity) {

    }
}
