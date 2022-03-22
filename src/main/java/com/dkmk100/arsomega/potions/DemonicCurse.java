package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ItemsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class DemonicCurse extends Effect {

    public static final DamageSource DEMONIC_CURSE_DAMAGE = (new DamageSource("demonic_curse")).bypassArmor().bypassMagic();

    protected DemonicCurse() {
        super(EffectType.HARMFUL, 2039587);
        this.setRegistryName("arsomega", "demonic_curse");
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(ItemsRegistry.CLEANSING_GEM));
        return ret;
    }

    @Override
    public boolean isBeneficial() {
        return super.isBeneficial();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_) {
        entity.hurt(DEMONIC_CURSE_DAMAGE, 1.0F);
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        int i = 40 >> p_76397_2_;
        if (i > 0) {
            return p_76397_1_ % i == 0;
        } else {
            return true;
        }
    }
}
