package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ItemsRegistry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.List;

public class BindEffect extends Effect {
    public BindEffect(String name) {
        super(EffectType.NEUTRAL, 2039587);
        this.setRegistryName("arsomega", name);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0dee8a21-f182-42c8-8361-1ad6186cac30", -1.0D,AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
    public BindEffect(String name,int color) {
        super(EffectType.NEUTRAL, color);
        this.setRegistryName("arsomega", name);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0dee8a21-f182-42c8-8361-1ad6186cac30", -1.0D,AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(ItemsRegistry.CLEANSING_GEM));
        return ret;
    }
}
