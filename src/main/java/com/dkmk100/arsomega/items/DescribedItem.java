package com.dkmk100.arsomega.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class DescribedItem extends BasicItem {
    String description;
    public DescribedItem(Properties properties, String description) {
        super(properties);
        this.description = description;
    }
    public DescribedItem(String name, Properties properties, String description) {
        super(properties,name);
        this.description = description;
    }
    public DescribedItem(String name, Properties properties, String description, boolean showEnch) {
        super(properties,name,showEnch);
        this.description = description;
    }

    public DescribedItem(Properties properties, String description, boolean showEnch) {
        super(properties,showEnch);
        this.description = description;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(new TextComponent(description));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
