package com.dkmk100.arsomega.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemPlayerStorage extends BasicItem {
    public ItemPlayerStorage(Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("target_name")) {
            tooltip2.add(new TextComponent("Player: " + stack.getTag().getString("target_name")));
        }
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    public static LivingEntity getTarget(ItemStack stack, Level world){
        if(stack.getTag().hasUUID("player")){
            UUID id = stack.getTag().getUUID("player");
            return world.getServer().getPlayerList().getPlayer(id);
        }
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        boolean changed = false;
        UUID id = player.getUUID();
        String name = player.getName().getContents();
        ItemStack stack = player.getItemInHand(hand);

        if (stack.hasTag() && stack.getTag().hasUUID("player") && stack.getTag().getUUID("player") == id) {
            changed = false;
        } else {
            changed = true;
            stack.getOrCreateTag().putUUID("player", id);
            stack.getTag().putString("target_name",name);
        }

        if (changed) {
            return InteractionResultHolder.success(stack);
        } else {
            return InteractionResultHolder.pass(stack);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity player) {
        boolean targetAny = false;//for testing, for now
        if(targetAny || target instanceof Player)
        {
            UUID id = target.getUUID();
            String name = target.getName().getContents();

            if (stack.hasTag() && stack.getTag().hasUUID("player") && stack.getTag().getUUID("player") == id) {
            } else {
                stack.getOrCreateTag().putUUID("player", id);
                stack.getTag().putString("target_name", name);
            }
        }
        return true;
    }
}
