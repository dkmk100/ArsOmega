package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(worldIn instanceof ServerWorld){
            Entity ent = RegistryHandler.BOSS_DEMON_KING.get().spawn((ServerWorld)worldIn,stack,playerIn,playerIn.blockPosition(), SpawnReason.MOB_SUMMONED,true,false);
            worldIn.addFreshEntity(ent);
        }
        stack.shrink(1);
        return new ActionResult<>(ActionResultType.CONSUME,stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        tooltip2.add(new StringTextComponent("Summons the Demon King on use"));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
