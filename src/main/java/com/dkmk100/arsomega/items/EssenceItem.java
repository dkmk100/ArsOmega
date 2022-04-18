package com.dkmk100.arsomega.items;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.network.chat.TranslatableComponent;

public class EssenceItem extends ModItem {

    public EssenceItem(Properties properties) {
        super(properties);
        this.withTooltip(new TranslatableComponent("tooltip.essences"));
    }

    public EssenceItem(Properties properties, String registryName) {
        super(properties);
        this.withTooltip(new TranslatableComponent("tooltip.essences"));
        this.setRegistryName(registryName);
    }
}
