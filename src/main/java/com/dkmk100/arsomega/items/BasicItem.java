package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item.Properties;

public class BasicItem extends Item {
    public BasicItem(Properties properties) {
        super(properties);
    }
    public BasicItem(Properties properties, String name)
    {
        super(properties);
        setRegistryName(name);
    }
}
