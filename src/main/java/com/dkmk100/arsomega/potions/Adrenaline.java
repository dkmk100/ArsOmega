package com.dkmk100.arsomega.potions;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;

import java.util.ArrayList;
import java.util.List;

public class Adrenaline extends Effect {
    protected Adrenaline() {
        super(EffectType.NEUTRAL, 2039587);
        this.setRegistryName("arsomega", "adrenaline");
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList();
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        try {
            if (entity instanceof PlayerEntity) {
                entity.addEffect(new EffectInstance(Effects.WEAKNESS, 500, 1));
                entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 500));
                entity.addEffect(new EffectInstance(Effects.CONFUSION, 120, 1));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
