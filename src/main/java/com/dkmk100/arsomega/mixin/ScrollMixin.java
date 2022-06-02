package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WarpScroll.class)
public class ScrollMixin {

    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/items/WarpScroll;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", cancellable = true, remap = false)
    public void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if(player.hasEffect(ModPotions.DEMONIC_ANCHORING)){
            cir.setReturnValue(new InteractionResultHolder(InteractionResult.FAIL, player.getItemInHand(hand)));
        }
    }
}
