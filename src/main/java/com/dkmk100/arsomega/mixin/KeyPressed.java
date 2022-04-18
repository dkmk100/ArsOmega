package com.dkmk100.arsomega.mixin;

import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.keybindings.KeyHandler;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyHandler.class)
public class KeyPressed {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    //@Overwrite
    /*
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/client/keybindings/KeyHandler;checkKeysPressed(I)V", cancellable = true, remap = false)
    private static void checkKeysPressed(int key, CallbackInfo ci) {
        ItemStack stack = StackUtil.getHeldSpellbook(MINECRAFT.player);
        CompoundTag tag;
        int newMode;
        if (key == ModKeyBindings.NEXT_SLOT.getKey().getValue() && stack.getItem() instanceof SpellBook) {
            if (stack.hasTag()) {
                tag = stack.getTag();
                newMode = SpellBook.getMode(tag) + 1;
                if (newMode > 10) {
                    newMode = 0;
                }

                KeyHandler.sendUpdatePacket(tag, newMode);
            }
        } else if (key == ModKeyBindings.PREVIOUS__SLOT.getKey().getValue() && stack.getItem() instanceof SpellBook) {
            if (stack.hasTag()) {
                tag = stack.getTag();
                newMode = SpellBook.getMode(tag) - 1;
                if (newMode < 0) {
                    newMode = 10;
                }

                KeyHandler.sendUpdatePacket(tag, newMode);
            }
        } else {
            if (key == ModKeyBindings.OPEN_SPELL_SELECTION.getKey().getValue()) {
                if (MINECRAFT.screen instanceof GuiRadialMenu) {
                    MINECRAFT.player.closeContainer();
                    return;
                }

                if (stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.screen == null) {
                    MINECRAFT.setScreen(new GuiRadialMenu(stack.getTag()));
                }
            }

            if (key == ModKeyBindings.OPEN_BOOK.getKey().getValue()) {
                if (MINECRAFT.screen instanceof GuiSpellBook && !((GuiSpellBook)MINECRAFT.screen).spell_name.isFocused()) {
                    MINECRAFT.player.closeContainer();
                    return;
                }

                if (stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.screen == null) {
                    if(stack.getItem() instanceof CustomSpellBook){
                        GuiSpellBook.open(stack.getTag(), ((CustomSpellBook) stack.getItem()).getTrueTier(), SpellBook.getUnlockedSpellString(stack.getTag()));
                    }
                    else {
                        GuiSpellBook.open(stack.getTag(), ((SpellBook) stack.getItem()).getTier().ordinal(), SpellBook.getUnlockedSpellString(stack.getTag()));
                    }
                }
            }

        }
        ci.cancel();
    }

     */
}
