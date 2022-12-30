package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;

public class BasicItem extends Item implements INamedItem {
    boolean showEnch = false;
    String myName;
    public BasicItem(Properties properties) {
        super(properties);
    }
    public BasicItem(Properties properties, String name)
    {
        super(properties);
        setRegistryName(name);
    }
    public BasicItem(Properties properties, String name, boolean showEnch)
    {
        super(properties);
        setRegistryName(name);
        this.showEnch = showEnch;
    }
    @Override
    public INamedItem setRegistryName(String name){
        myName = name;
        return this;
    }

    @Override
    public String getNameForReg(){
        return myName;
    }

    @Override
    public Item getItem() {
        return this;
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
