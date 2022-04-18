package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class DemonStaff extends BasicItem {
    public DemonStaff(Properties properties, String name) {
        super(properties);
        this.setRegistryName(ArsOmega.MOD_ID, name);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(worldIn instanceof ServerLevel){
            Entity ent = RegistryHandler.BOSS_DEMON_KING.get().spawn((ServerLevel)worldIn,stack,playerIn,playerIn.blockPosition(), MobSpawnType.MOB_SUMMONED,true,false);
            worldIn.addFreshEntity(ent);
        }
        stack.shrink(1);
        return new InteractionResultHolder<>(InteractionResult.CONSUME,stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(new TextComponent("Summons the Demon King on use"));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
