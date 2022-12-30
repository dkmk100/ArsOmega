package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;

public class BasicBlockItem extends BlockItem implements INamedItem {

    String myName;
    public BasicBlockItem(Block block,Properties properties) {
        super(block,properties);
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
}
