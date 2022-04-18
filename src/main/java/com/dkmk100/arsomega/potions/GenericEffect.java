package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ItemsRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericEffect extends MobEffect {

    boolean normalCure = true;
    public GenericEffect(MobEffectCategory p_i50391_1_, int p_i50391_2_, String name) {
        super(p_i50391_1_, p_i50391_2_);
        this.setRegistryName("arsomega", name);
        normalCure = true;
    }
    public GenericEffect(MobEffectCategory p_i50391_1_, int p_i50391_2_, String name, boolean milkCure) {
        super(p_i50391_1_, p_i50391_2_);
        this.setRegistryName("arsomega", name);
        normalCure = milkCure;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        if(normalCure){
            return super.getCurativeItems();
        }
        else{
            ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
            ret.add(new ItemStack(ItemsRegistry.CLEANSING_GEM));
            return ret;
        }
    }
}
