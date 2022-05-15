package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class GlyphRaiseEarth extends TierFourEffect {

    public static GlyphRaiseEarth INSTANCE = new GlyphRaiseEarth("raise_earth","Raise Earth");
    final static int maxCheckUp = 4;

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        //raise actual earth
        BlockPos pos1 = rayTraceResult.getBlockPos();
        int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos1, rayTraceResult, aoeBuff, 0);
        int highestReached = 0;
        Iterator var12 = posList.iterator();
        while (var12.hasNext()) {
            BlockPos pos = (BlockPos) var12.next();
            if (world.getBlockState(pos).isAir()) {
                //allow going downards 1 time for hills and stuff
                pos = pos.below();
                if (world.getBlockState(pos).isAir()) {
                    continue;
                }
            }
            else {
                for (int i = 0; i < maxCheckUp; i++) {
                    if (!world.getBlockState(pos.above()).isAir()) {
                        pos = pos.above();
                    }
                }
            }
            int maxHeight = 5 + 3 * spellStats.getBuffCount(AugmentPierce.INSTANCE);

            int actualHeight = 0;
            for (int i = 0; i < maxHeight; i++) {
                if (world.getBlockState(pos.above(actualHeight + 1)).isAir()) {
                    actualHeight += 1;
                } else {
                    break;
                }
            }

            //used for damage hitbox size
            if(actualHeight > highestReached){
                highestReached = actualHeight;
            }

            for (int i = 0; i < actualHeight; i++) {
                BlockState targetState = world.getBlockState(pos.below(i));
                if (targetState.isAir()) {
                    targetState = Blocks.STONE.defaultBlockState();
                }
                world.setBlockAndUpdate(pos.above(actualHeight - i), targetState);
                world.setBlockAndUpdate(pos.below(i), Blocks.STONE.defaultBlockState());
            }
        }

        //deal damage
        boolean sensitive = false;
        int x = pos1.getX();
        int y = pos1.getY();
        int z = pos1.getZ();
        Predicate<? super Entity> test = (entity) -> {
            return (!sensitive || entity instanceof LivingEntity) && entity.isAlive();
        };
        List<Entity> list = world.getEntities(shooter, new AxisAlignedBB(x - 3.0D, y - 3.0D, z - 3.0D, x + 3.0D, y + highestReached + 3.0D, z + 3.0D).inflate(aoeBuff), test);
        for(Entity entity : list){
            entity.setPos(entity.position().x,entity.position().y + highestReached, entity.position().z);
            entity.hurt(DamageSource.FALL,4 + 2*(float)spellStats.getAmpMultiplier());
        }
    }

    public GlyphRaiseEarth(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getManaCost() {
        return 450;//kinda cheap since it's not meant as a big combat move but a medium-size blocking move
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE,AugmentPierce.INSTANCE});
    }
}
