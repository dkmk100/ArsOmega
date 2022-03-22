package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.armor.Materials;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;

public class EnchantedArmor extends MagicArmor {
    int boostBonus;
    int regenBonus;
    public EnchantedArmor(String name,EquipmentSlotType slot, int boost, int regen, IArmorMaterial material)
    {
        super(material, slot, ItemsRegistry.defaultItemProperties());
        boostBonus = boost;
        regenBonus = regen;
        this.setRegistryName(ArsOmega.MOD_ID,name);
    }
    public EnchantedArmor(String name,EquipmentSlotType slot, int boost, int regen, IArmorMaterial material, Item.Properties properties)
    {
        super(material, slot, properties);
        boostBonus = boost;
        regenBonus = regen;
        this.setRegistryName(ArsOmega.MOD_ID,name);
    }

    public int getMaxManaBoost() {
        return boostBonus;
    }

    public int getManaRegenBonus() {
        return regenBonus;
    }


}
