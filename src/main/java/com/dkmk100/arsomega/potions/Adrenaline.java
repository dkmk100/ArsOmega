package com.dkmk100.arsomega.potions;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.List;

public class Adrenaline extends MobEffect {
    protected Adrenaline() {
        super(MobEffectCategory.NEUTRAL, 2039587);
        this.setRegistryName("arsomega", "adrenaline");
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList();
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        try {
            if (entity instanceof Player) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 500, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 500));
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 1));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
