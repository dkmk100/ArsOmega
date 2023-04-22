package com.dkmk100.arsomega.items;

import net.minecraft.world.item.Item.Properties;

public class SigilItem extends DescribedItem{
    public SigilItem(Properties properties) {
        super(properties, "Sigils are made with the ritual of shaping, by drawing shapes with chalk and then activating the ritual. \nMore info on crafting sigils can be found in the worn notebook, and JEI support is planned.");
    }
    public SigilItem(Properties properties, boolean showEnch) {
        super(properties, "Sigils are made with the ritual of shaping, by drawing shapes with chalk and then activating the ritual. \nMore info on crafting sigils can be found in the worn notebook, and JEI support is planned.", showEnch);
    }
    public SigilItem(Properties properties, boolean showEnch, boolean active) {
        super(properties, "Activated Sigils are made by crafting sigils with other components. Sigils are made with the ritual of shaping. \nFore more info, check the category on sigils in the worn notebook", showEnch);
    }
}
