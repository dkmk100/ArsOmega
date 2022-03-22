/*
package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.books.CustomSpellBook;
import com.dkmk100.arsomega.glyphs.ICustomTier;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.validation.BaseSpellValidationError;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(Glyph.class)
public class GlyphScribe {
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/items/Glyph;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", cancellable = true, remap = false)
    protected void use(World worldIn, PlayerEntity playerIn, Hand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        //playerIn.sendMessage(new StringTextComponent("inject start"), Util.NIL_UUID);
        if (!worldIn.isClientSide) {
            try {
                Field field = Glyph.class.getDeclaredField("spellPart");
                field.setAccessible(true);
                AbstractSpellPart part = (AbstractSpellPart) field.get(this);
                playerIn.inventory.items.forEach((itemStack) -> {
                    if (itemStack.getItem() instanceof CustomSpellBook) {
                        if (CustomSpellBook.getUnlockedSpells(itemStack.getTag()).contains(part)) {
                            playerIn.sendMessage(new StringTextComponent("You already know this spell!"), Util.NIL_UUID);
                            cir.setReturnValue(null);
                            return;
                        } else {
                            CustomSpellBook.unlockSpell(itemStack.getTag(), part.getTag());
                            playerIn.getItemInHand(handIn).shrink(1);
                            playerIn.sendMessage(new StringTextComponent("Unlocked " + part.getName()), Util.NIL_UUID);
                        }
                    }
                });
            } catch (Exception e) {
                playerIn.sendMessage(new StringTextComponent("error!"), Util.NIL_UUID);
            }
        }

    }
}
 */
