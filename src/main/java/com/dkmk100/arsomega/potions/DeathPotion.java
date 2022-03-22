package com.dkmk100.arsomega.potions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class DeathPotion extends Effect {
    public static final DamageSource DEATH = (new DamageSource("death")).bypassArmor().bypassInvul().bypassMagic();
    public DeathPotion(String name) {
        super(EffectType.NEUTRAL, 2039587);
        this.setRegistryName("arsomega", name);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList();
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        entity.setHealth(1);
        entity.hurt(DEATH,Float.MAX_VALUE);
        entity.kill();
    }
}
