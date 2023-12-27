package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class CustomGlass extends GlassBlock {
    private final Supplier<BlockState> enderEyeState;
    private final Supplier<BlockState> reflectiveState;
    public CustomGlass(Properties properties, Supplier<BlockState> enderEyeState, Supplier<BlockState> reflectiveState) {
        super(properties);
        this.enderEyeState = enderEyeState;
        this.reflectiveState = reflectiveState;
    }




    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.is(Items.ENDER_EYE)){
            BlockState newState = enderEyeState.get();
            if(newState != null){
                world.setBlockAndUpdate(pos, newState);
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        else if(stack.is(RegistryHandler.REFLECTIVE_COATING.get())){
            BlockState newState = reflectiveState.get();
            if(newState != null){
                world.setBlockAndUpdate(pos, newState);
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }
}
