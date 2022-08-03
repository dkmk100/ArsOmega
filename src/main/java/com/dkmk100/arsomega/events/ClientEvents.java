package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.enchants.ProactiveSpellcaster;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onTooltip(final ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        if(stack.hasTag()) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(RegistryHandler.PROACTIVE_ENCHANT.get(), stack);
            Spell spell = new ProactiveSpellcaster(stack).getSpell();
            if (level > 0 && spell.isValid()) {
                event.getToolTip().add(new TextComponent("Proactive spell: "));
                event.getToolTip().add(new TextComponent(spell.getDisplayString()));
            }
        }
    }
}
