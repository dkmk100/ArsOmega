package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.renderLayer.PetrificationLayer;
import com.dkmk100.arsomega.enchants.ProactiveSpellcaster;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.spell.Spell;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onTooltip(final ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        if(stack.hasTag()) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(RegistryHandler.PROACTIVE_ENCHANT.get(), stack);
            Spell spell = new ProactiveSpellcaster(stack).getSpell();
            if (level > 0 && spell.isValid()) {
                event.getToolTip().add(  Component.literal("Proactive spell: "));
                event.getToolTip().add(  Component.literal(spell.getDisplayString()));
            }
        }
    }
}
