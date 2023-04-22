package com.dkmk100.arsomega.items;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item.Properties;

public class EssenceItem extends ModItem {

    String myName;
    public EssenceItem(Properties properties) {
        super(properties);
        this.withTooltip(Component.translatable("tooltip.ars_nouveau.essences"));
    }

}
