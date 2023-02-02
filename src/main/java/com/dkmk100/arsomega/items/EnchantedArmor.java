package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.armor.Materials;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EnchantedArmor extends MagicArmor {
    int boostBonus;
    int regenBonus;
    public EnchantedArmor(EquipmentSlot slot, int boost, int regen, ArmorMaterial material)
    {
        super(material, slot, ItemsRegistry.defaultItemProperties());
        boostBonus = boost;
        regenBonus = regen;
    }
    public EnchantedArmor(EquipmentSlot slot, int boost, int regen, ArmorMaterial material, Item.Properties properties)
    {
        super(material, slot, properties);
        boostBonus = boost;
        regenBonus = regen;
    }

    @Override
    public int getMaxManaBoost(ItemStack stack) {
        return boostBonus;
    }
    @Override

    public int getManaRegenBonus(ItemStack stack) {
        return regenBonus;
    }


}
