package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item.Properties;

public class DimensionCrystal extends Item {
    public DimensionCrystal(String name, Properties p_i48487_1_) {
        super(p_i48487_1_);
        this.setRegistryName(ArsOmega.MOD_ID, name);
    }
}
