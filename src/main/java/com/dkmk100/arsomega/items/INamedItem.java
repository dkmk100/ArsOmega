package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item;

public interface INamedItem {

    public INamedItem setRegistryName(String name);
    public String getNameForReg();
    public Item getItem();
}
