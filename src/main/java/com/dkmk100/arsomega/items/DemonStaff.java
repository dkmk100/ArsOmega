package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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
        if(worldIn instanceof ServerLevel) {
            if(!worldIn.dimension().equals(ResourceKey.create(Registry.DIMENSION_REGISTRY, RegistryHandler.DIMTYPE)) && !playerIn.getAbilities().instabuild){
                //not in demon realm
                PortUtil.sendMessage(playerIn,"Must be used in the demon realm!");
                return new InteractionResultHolder<>(InteractionResult.FAIL,stack);
            }
            Entity ent = RegistryHandler.BOSS_DEMON_KING.get().spawn((ServerLevel) worldIn, stack, playerIn, playerIn.blockPosition(), MobSpawnType.MOB_SUMMONED, true, false);
            worldIn.addFreshEntity(ent);
            //stack.shrink(1);
            playerIn.getCooldowns().addCooldown(this, 200);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS,stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(new TextComponent("Summons the Demon King on use. Can be found in a structure in the demon realm."));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
