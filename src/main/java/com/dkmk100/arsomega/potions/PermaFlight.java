package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.ArrayList;
import java.util.List;

public class PermaFlight extends MobEffect {

    protected PermaFlight() {
        super(MobEffectCategory.BENEFICIAL, 2039587);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(RegistryHandler.CLEANSING_GEM.get()));
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
        if (entity instanceof Player) {
            if(!((Player)entity).hasEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.GRAVITY_EFFECT.get())) {
                ((Player) entity).getAbilities().mayfly = true;
            }
        }

    }
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap p_111187_2_, int p_111187_3_) {
        super.removeAttributeModifiers(entity, p_111187_2_, p_111187_3_);
        if (entity instanceof ServerPlayer) {
            ((Player) entity).getAbilities().mayfly = false;
            ((Player) entity).getAbilities().flying = false;
            Networking.sendToPlayerClient(new PacketUpdateFlight(false, false), (ServerPlayer) entity);
        }
    }
}
