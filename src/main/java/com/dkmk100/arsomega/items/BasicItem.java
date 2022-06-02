package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;

public class BasicItem extends Item {
    boolean showEnch = false;
    public BasicItem(Properties properties) {
        super(properties);
    }
    public BasicItem(Properties properties, String name)
    {
        super(properties);
        setRegistryName(name);
    }
    public BasicItem(Properties properties, String name,boolean showEnch)
    {
        super(properties);
        setRegistryName(name);
        this.showEnch = showEnch;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if(showEnch){
            return true;
        }
        else{
            return super.isFoil(stack);
        }
    }


}
