package com.dkmk100.arsomega.items;

public class SigilItem extends DescribedItem{
    public SigilItem(String name, Properties properties) {
        super(name, properties, "Sigils are made with the ritual of shaping, by drawing shapes with chalk and then activating the ritual. \nMore info on crafting sigils can be found in the worn notebook, and JEI support is planned.");
    }
    public SigilItem(String name, Properties properties, boolean showEnch) {
        super(name, properties, "Sigils are made with the ritual of shaping, by drawing shapes with chalk and then activating the ritual. \nMore info on crafting sigils can be found in the worn notebook, and JEI support is planned.", showEnch);
    }
    public SigilItem(String name, Properties properties, boolean showEnch, boolean active) {
        super(name, properties, "Activated Sigils are made by crafting sigils with other components. Sigils are made with the ritual of shaping. \nFore more info, check the category on sigils in the worn notebook", showEnch);
    }
}
