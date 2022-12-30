package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.blocks.BasicChalk;
import com.dkmk100.arsomega.blocks.ChalkLineBlock;
import com.dkmk100.arsomega.blocks.ChalkTile;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
 
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BasicChalkItem extends DescribedItem{
    Supplier<BasicChalk> chalkBlock;

    public BasicChalkItem(String name,Supplier<BasicChalk> chalkSupplier,int durability,String description) {
        super(name, new Item.Properties().tab(ArsOmega.itemGroup).durability(durability), description);
        chalkBlock = chalkSupplier;
    }

    public BasicChalkItem(String name, Supplier<BasicChalk> chalkSupplier, Properties properties, String description) {
        super(name, properties, description);
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

                context.getItemInHand().hurtAndBreak(1, context.getPlayer(), (t) -> {
                });
            }

            return InteractionResult.SUCCESS;
        }
    }
}
