package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Rot extends AbstractEffect {

    public static Rot INSTANCE = new Rot("rot", "Rot");

    public Rot(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(world instanceof ServerWorld){
            int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
            double amp = spellStats.getAmpMultiplier();
            BlockPos pos = rayTraceResult.getBlockPos();
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, 0);

            Iterator var12 = posList.iterator();
            while(var12.hasNext()) {
                BlockPos pos1 = (BlockPos) var12.next();
                Block block = world.getBlockState(pos1).getBlock();
                if (block instanceof IPlantable) {
                    BlockPos below = pos1.below();
                    if (world.isEmptyBlock(below)) {
                        world.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
                    } else {
                        Block block1 = world.getBlockState(below).getBlock();
                        if (block1 == Blocks.FARMLAND || block1 == Blocks.DIRT || block1 == Blocks.GRASS_BLOCK) {
                            Block dirt = Blocks.DIRT;
                            if (amp >= 7) {
                                dirt = Blocks.SAND;
                            } else if (amp >= 4) {
                                dirt = Blocks.SAND;
                            } else if (amp >= 1) {
                                dirt = Blocks.COARSE_DIRT;
                            }
                            world.setBlock(below, dirt.defaultBlockState(), 3);
                        }
                        world.setBlock(pos1, Blocks.DEAD_BUSH.defaultBlockState(), 3);
                    }
                }
                else if((block instanceof  IGrowable || block == Blocks.FARMLAND) && amp > 0){
                    Block dirt = Blocks.DIRT;
                    if(amp>=8){
                        dirt = Blocks.SAND;
                    }
                    else if(amp>=5){
                        dirt = Blocks.SAND;
                    }
                    else if(amp>=2) {
                        dirt = Blocks.COARSE_DIRT;
                    }
                    world.setBlock(pos1, dirt.defaultBlockState(),3);
                }
            }
        }
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            this.applyConfigPotion((LivingEntity)entity, Effects.HUNGER, spellStats);
        }
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Applies hunger to entities, and kills any plants it hits";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        Set potionAugments =  this.POTION_AUGMENTS;
        ArrayList<AbstractAugment> list = new ArrayList<AbstractAugment>(potionAugments);
        list.add(AugmentAOE.INSTANCE);
        return Collections.unmodifiableSet(new HashSet(list));
    }
}
