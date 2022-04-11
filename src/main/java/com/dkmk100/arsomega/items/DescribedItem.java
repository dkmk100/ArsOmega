package com.dkmk100.arsomega.items;

public class DescribedItem extends BasicItem {
    String description;
    public DescribedItem(Properties properties,String description) {
        super(properties);
        this.description = description;
    }
    public DescribedItem(String name, Properties properties, String description) {
        super(properties,name);
        this.description = description;
    }
}
