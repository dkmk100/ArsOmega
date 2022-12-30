package com.dkmk100.arsomega.enchants;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class ProactiveEnchant extends Enchantment {

    public ProactiveEnchant() {
        super(Rarity.VERY_RARE, CustomEnchantCategory.ALL, EquipmentSlot.values());
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
        return false;
    }

    public boolean canEnchant(ItemStack stack) {
        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    public boolean isAllowedOnBooks() {
        return false;
    }
}
