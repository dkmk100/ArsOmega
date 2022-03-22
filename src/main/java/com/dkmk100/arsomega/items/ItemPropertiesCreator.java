package com.dkmk100.arsomega.items;

import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;

public class ItemPropertiesCreator {
    public Properties create(ItemGroup group, int maxStackSize, ToolType toolType, int harverstLevel){
        Properties properties = new Properties().tab(group).stacksTo(maxStackSize).addToolType(toolType,harverstLevel);
        return properties;
    }
}
