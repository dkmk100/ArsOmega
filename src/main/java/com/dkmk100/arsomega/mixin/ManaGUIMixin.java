package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.client.gui.GuiManaHUD;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiManaHUD.class)
public class ManaGUIMixin {
    private static final Minecraft minecraft = Minecraft.getInstance();

    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/client/gui/GuiManaHUD;shouldDisplayBar()Z", cancellable = true, remap = false)
    private static void shouldDisplayBar(CallbackInfoReturnable<Boolean> cir) {
        ItemStack mainHand = minecraft.player.getMainHandItem();
        ItemStack offHand = minecraft.player.getOffhandItem();
        boolean result = false;
        if(EnchantmentHelper.getEnchantmentLevel(RegistryHandler.PROACTIVE_ENCHANT.get(),minecraft.player)>0){
            cir.setReturnValue(true);
        }
    }

}
