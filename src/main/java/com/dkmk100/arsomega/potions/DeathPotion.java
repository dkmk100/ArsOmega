package com.dkmk100.arsomega.potions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.damagesource.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class DeathPotion extends MobEffect {
    public static final DamageSource DEATH = (new DamageSource("death")).bypassArmor().bypassInvul().bypassMagic();
    public DeathPotion(String name) {
        super(MobEffectCategory.NEUTRAL, 2039587);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList();
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        entity.setHealth(1);
        entity.hurt(DEATH,Float.MAX_VALUE);
        entity.kill();
    }
}
