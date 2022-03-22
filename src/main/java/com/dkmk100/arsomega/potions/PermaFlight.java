package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ItemsRegistry;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.List;

public class PermaFlight extends Effect {

    protected PermaFlight() {
        super(EffectType.BENEFICIAL, 2039587);
        this.setRegistryName("arsomega", "perma_flight");
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
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }
    @Override
    public void applyEffectTick(LivingEntity entity, int p_76394_2_) {
        super.applyEffectTick(entity, p_76394_2_);
        if (entity instanceof PlayerEntity) {
            if(!((PlayerEntity)entity).hasEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.GRAVITY_EFFECT)) {
                ((PlayerEntity) entity).abilities.mayfly = entity.getEffect(ModPotions.PERMA_FLIGHT).getDuration() > 2;
            }
        }

    }
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity)entity).abilities.mayfly = false;
            ((PlayerEntity)entity).abilities.flying = false;
            Networking.sendToPlayer(new PacketUpdateFlight(false, false), (PlayerEntity)entity);
        }

    }
}
