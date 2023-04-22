package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.blocks.ChalkLineBlock;
import com.dkmk100.arsomega.blocks.ChalkTile;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
 
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public class MagicChalk extends BasicItem{
    Supplier<ChalkLineBlock> chalkBlock;

    public MagicChalk(Supplier<ChalkLineBlock> chalkSupplier,int durability) {
        super(new Properties().tab(ArsOmega.itemGroup).durability(durability));
        chalkBlock = chalkSupplier;
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockPlaceContext blockContext = new BlockPlaceContext(context);
        if (world.isClientSide) {
            return super.useOn(context);
        } else {
            if(world.getBlockState(pos).canBeReplaced(blockContext)){
                pos = pos.below();
            }
            if (world.getBlockState(pos.above()).getMaterial() == Material.AIR) {


                world.setBlockAndUpdate(pos.above(), chalkBlock.get().getStateForPlacement(blockContext));
                ChalkTile rune = (ChalkTile)world.getBlockEntity(pos.above());
                rune.data.owner = context.getPlayer().getUUID();//so the player who places it owns it and others can't edit it
                chalkBlock.get().RequestAdjacentUpdates(world,pos.above());


                context.getItemInHand().hurtAndBreak(1, context.getPlayer(), (t) -> {
                });
            }

            return InteractionResult.SUCCESS;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(  Component.literal("Used to draw lines of magic chalk on the ground. " +
                "\nThese can then be inscribed with a touch spell, which will affect the entire line. " +
                "\nCan take multiple charges of a spell, and is good for defense in a pinch. "));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

}
