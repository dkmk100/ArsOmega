package com.dkmk100.arsomega.items;

import com.hollingsworth.arsnouveau.common.items.curios.AbstractManaCurio;
import net.minecraft.world.item.ItemStack;

public class MagicCurio extends AbstractManaCurio {
    int boost;
    int regen;
    public MagicCurio(String reg, int boost, int regen) {
        super(reg);
        this.boost = boost;
        this.regen = regen;
    }

    @Override
    public int getManaRegenBonus(ItemStack stack) {
        return regen;
    }
    @Override
    public int getMaxManaBoost(ItemStack stack) {
        return boost;
    }
}
