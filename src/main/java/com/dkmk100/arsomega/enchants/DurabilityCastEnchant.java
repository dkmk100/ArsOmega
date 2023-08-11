package com.dkmk100.arsomega.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class DurabilityCastEnchant extends Enchantment{
    public DurabilityCastEnchant() {
        super(Enchantment.Rarity.VERY_RARE, CustomEnchantCategory.ALL, EquipmentSlot.values());
    }

    public int getMinCost(int enchantmentLevel) {
        return 0;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 0;
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    public boolean canEnchant(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    public boolean isAllowedOnBooks() {
        return false;
    }
}
