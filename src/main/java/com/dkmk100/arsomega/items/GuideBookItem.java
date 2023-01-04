package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class GuideBookItem extends DescribedItem {
    public GuideBookItem (String description) {
        super((new Item.Properties()).tab(ArsOmega.itemGroup).stacksTo(1), description);
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (playerIn instanceof ServerPlayer) {
            PatchouliAPI.get().openBookGUI((ServerPlayer)playerIn, Registry.ITEM.getKey(this));
        }

        return new InteractionResultHolder(InteractionResult.PASS, stack);
    }

}
