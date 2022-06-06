package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectGrow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class AdvancedGrow extends AbstractEffect {
    public static AdvancedGrow INSTANCE = new AdvancedGrow();

    private AdvancedGrow() {
        super("advanced_grow", "Advanced Grow");
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Iterator var6 = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats).iterator();

        while(var6.hasNext()) {
            BlockPos blockpos = (BlockPos)var6.next();
            if (BlockUtil.destroyRespectsClaim(shooter, world, blockpos) && world instanceof ServerLevel) {
                GrowBlock(blockpos,(ServerLevel)world);
            }
        }

    }

    public static boolean GrowBlock(BlockPos pos, ServerLevel world){
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(block == Blocks.CACTUS || block == Blocks.SUGAR_CANE){
            //I know that you can just hit a higher block to keep growing them, I just don't care
            //seems fair enough to just let you do that as a player
            //turrets can't abuse it though, easier to just harvest at that point
            int maxExtraHeight = 3;
            int i = 1;
            while(i<maxExtraHeight) {
                if (pos.getY() + i >= world.getMaxBuildHeight()) {
                    return false;
                }
                if (world.getBlockState(pos.above(i)).isAir()) {
                    world.setBlockAndUpdate(pos.above(i), block.defaultBlockState());
                    return true;
                }
                i+=1;
            }
            return false;
        }
        else if(block == Blocks.NETHER_WART){
            int age = state.getValue(NetherWartBlock.AGE);
            if(age < NetherWartBlock.MAX_AGE){
                world.setBlockAndUpdate(pos,state.setValue(NetherWartBlock.AGE,age + 1));
                return true;
            }
            else{
                return false;
            }
        }
        else if(block == Blocks.CHORUS_FLOWER){
            GrowChorusFlower(state,pos,world);
            return true;
        }
        else if(block == Blocks.CHORUS_PLANT){
            int maxExtraHeight = 3;
            int i = 0;
            while(i<maxExtraHeight) {
                if (pos.getY() + i >= world.getMaxBuildHeight()) {
                    return false;
                }
                if (world.getBlockState(pos.above(i)).is(Blocks.CHORUS_FLOWER)) {
                    GrowChorusFlower(world.getBlockState(pos.above(i)),pos.above(i),world);
                }
                else if(world.getBlockState(pos.above(i)).is(Blocks.CHORUS_PLANT)){
                    for(Direction direction : Direction.Plane.HORIZONTAL) {
                        if (world.getBlockState(pos.above(i).relative(direction)).is(Blocks.CHORUS_FLOWER)) {
                            GrowChorusFlower(world.getBlockState(pos.above(i).relative(direction)),pos.above(i).relative(direction),world);
                            return true;
                        }
                    }
                }
                i+=1;
            }
            return false;
        }
        else if(state.is(BlockTags.SMALL_FLOWERS) && block instanceof IPlantable){
            for(Direction dir : Direction.values()){
                if(dir!=Direction.DOWN && dir!=Direction.UP){
                    BlockPos pos1 = pos.relative(dir);
                    if(world.getBlockState(pos1).isAir() && state.canSustainPlant(world,pos1.below(),Direction.UP,(IPlantable)block)){
                        world.setBlockAndUpdate(pos1,state);
                        return true;
                    }
                }
            }
            return false;
        }
        else{
            ItemStack stack = new ItemStack(Items.BONE_MEAL, 64);
            return BoneMealItem.applyBonemeal(stack, world, pos, FakePlayerFactory.getMinecraft((ServerLevel)world));
        }
    }

    public static void GrowChorusFlower(BlockState state, BlockPos pos, ServerLevel world){
        Random random = world.getRandom();
        BlockPos blockpos = pos.above();
        if (world.isEmptyBlock(blockpos) && blockpos.getY() < world.getMaxBuildHeight()) {
            int i = state.getValue(ChorusFlowerBlock.AGE);
            if (i < 5) {
                boolean flag = false;
                boolean flag1 = false;
                BlockState blockstate = world.getBlockState(pos.below());
                if (blockstate.is(Blocks.END_STONE)) {
                    flag = true;
                } else if (blockstate.getBlock() instanceof ChorusPlantBlock) {
                    int j = 1;

                    for(int k = 0; k < 4; ++k) {
                        BlockState blockstate1 = world.getBlockState(pos.below(j + 1));
                        if (!(blockstate1.getBlock() instanceof ChorusPlantBlock)) {
                            if (blockstate1.is(Blocks.END_STONE)) {
                                flag1 = true;
                            }
                            break;
                        }

                        ++j;
                    }

                    if (j < 2 || j <= random.nextInt(flag1 ? 5 : 4)) {
                        flag = true;
                    }
                } else if (blockstate.isAir()) {
                    flag = true;
                }

                if (flag && allNeighborsEmpty(world, blockpos, (Direction)null) && world.isEmptyBlock(pos.above(2))) {
                    world.setBlock(pos, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(world,pos), 2);
                    placeGrownFlower(world, blockpos, i);
                } else if (i < 4) {
                    int l = random.nextInt(4);
                    if (flag1) {
                        ++l;
                    }

                    boolean flag2 = false;

                    for(int i1 = 0; i1 < l; ++i1) {
                        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        BlockPos blockpos1 = pos.relative(direction);
                        if (world.isEmptyBlock(blockpos1) && world.isEmptyBlock(blockpos1.below()) && allNeighborsEmpty(world, blockpos1, direction.getOpposite())) {
                            placeGrownFlower(world, blockpos1, i + 1);
                            flag2 = true;
                        }
                    }

                    if (flag2) {
                        world.setBlock(pos, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(world,pos), 2);
                    } else {
                        placeDeadFlower(world,pos);
                    }
                } else {
                    placeDeadFlower(world, pos);
                }
            }
        }
    }

    private static void placeGrownFlower(Level p_51662_, BlockPos p_51663_, int p_51664_) {
        p_51662_.setBlock(p_51663_, Blocks.CHORUS_FLOWER.defaultBlockState().setValue(ChorusFlowerBlock.AGE, Integer.valueOf(p_51664_)), 2);
        p_51662_.levelEvent(1033, p_51663_, 0);
    }

    private static void placeDeadFlower(Level p_51659_, BlockPos p_51660_) {
        p_51659_.setBlock(p_51660_, Blocks.CHORUS_FLOWER.defaultBlockState().setValue(ChorusFlowerBlock.AGE, Integer.valueOf(5)), 2);
        p_51659_.levelEvent(1034, p_51660_, 0);
    }

    private static boolean allNeighborsEmpty(LevelReader p_51698_, BlockPos p_51699_, @Nullable Direction p_51700_) {
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction != p_51700_ && !p_51698_.isEmptyBlock(p_51699_.relative(direction))) {
                return false;
            }
        }

        return true;
    }

    public static boolean CanGrowBlock(BlockPos pos, Level world){
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(block == Blocks.CACTUS || block == Blocks.SUGAR_CANE){
            return true;
        }
        else if(block == Blocks.NETHER_WART){
            return state.getValue(NetherWartBlock.AGE) < NetherWartBlock.MAX_AGE;
        }
        else if(block == Blocks.CHORUS_FLOWER){
            return true;
        }
        else if(state.is(BlockTags.SMALL_FLOWERS)){
            return true;
        }
        else{
            return block instanceof BonemealableBlock && ((BonemealableBlock)block).isValidBonemealTarget(world, pos, world.getBlockState(pos), world.isClientSide);
        }
    }

    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (!(rayTraceResult instanceof BlockHitResult)) {
            return false;
        } else {
            BlockPos pos = ((BlockHitResult)rayTraceResult).getBlockPos();
            return CanGrowBlock(pos,world);
        }
    }

    public int getDefaultManaCost() {
        return 170;
    }

    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentPierce.INSTANCE});
    }

    public String getBookDescription() {
        return "Causes plants to accelerate in growth as if they were bonemealed.";
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE,SpellSchools.ELEMENTAL_EARTH});
    }
}

