package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.ArrayList;
import java.util.List;

public class BindEffect extends MobEffect {
    public BindEffect(String name) {
        super(MobEffectCategory.NEUTRAL, 2039587);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0dee8a21-f182-42c8-8361-1ad6186cac30", -1.0D,AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
    public BindEffect(String name,int color) {
        super(MobEffectCategory.NEUTRAL, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0dee8a21-f182-42c8-8361-1ad6186cac30", -1.0D,AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(RegistryHandler.CLEANSING_GEM.get()));
        return ret;
    }
}
