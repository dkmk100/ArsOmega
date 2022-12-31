package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;

public class BasicItem extends Item {
    boolean showEnch = false;
    String myName;
    public BasicItem(Properties properties) {
        super(properties);
    }
    public BasicItem(Properties properties, boolean showEnch)
    {
        super(properties);
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
