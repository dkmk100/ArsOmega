package com.dkmk100.arsomega.items;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class EssenceItem extends ModItem implements INamedItem {

    String myName;
    public EssenceItem(Properties properties) {
        super(properties);
        this.withTooltip(Component.translatable("tooltip.essences"));
    }

    public EssenceItem(Properties properties, String registryName) {
        super(properties);
        this.withTooltip(Component.translatable("tooltip.essences"));
        this.setRegistryName(registryName);
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
