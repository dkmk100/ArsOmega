package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.CreativeModeTab;

public class ItemPropertiesCreator {
    public Properties create(CreativeModeTab group, int maxStackSize){
        Properties properties = new Properties().tab(group).stacksTo(maxStackSize);
        return properties;
    }
}
