package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class ArcaneBloomCrop extends CropBlock {
    public static final IntegerProperty AGE_0_4 = IntegerProperty.create("age", 0, 4);
    private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D)};

    public ArcaneBloomCrop() {
        super(Properties.of(Material.PLANT).randomTicks().strength(0.0F).noCollission().sound(SoundType.CROP).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPES[(Integer)state.getValue(this.getAgeProperty())];
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return RegistryHandler.ARCANE_BLOOM_CROP.get();
    }

}
