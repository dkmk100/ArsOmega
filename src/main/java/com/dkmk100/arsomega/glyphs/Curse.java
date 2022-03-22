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

public class Curse extends AbstractEffect {

    public static Curse INSTANCE = new Curse("curse", "Curse");

    public Curse(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerWorld) {
            int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
            BlockPos pos = rayTraceResult.getBlockPos();
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, 0);

            Iterator var12 = posList.iterator();
            while (var12.hasNext()) {
                BlockPos pos1 = (BlockPos) var12.next();
                Block block = world.getBlockState(pos1).getBlock();
                if (block == Blocks.COBBLESTONE) {
                    world.setBlock(pos1, Blocks.INFESTED_COBBLESTONE.defaultBlockState(), 3);
                }
                else if (block == Blocks.STONE) {
                    world.setBlock(pos1, Blocks.INFESTED_STONE.defaultBlockState(), 3);
                }
                else if (block == Blocks.STONE_BRICKS) {
                    world.setBlock(pos1, Blocks.INFESTED_STONE_BRICKS.defaultBlockState(), 3);
                }
                else if (block == Blocks.CRACKED_STONE_BRICKS) {
                    world.setBlock(pos1, Blocks.INFESTED_CRACKED_STONE_BRICKS.defaultBlockState(), 3);
                }
                else if (block == Blocks.CHISELED_STONE_BRICKS) {
                    world.setBlock(pos1, Blocks.INFESTED_CHISELED_STONE_BRICKS.defaultBlockState(), 3);
                }
                else if (block == Blocks.MOSSY_STONE_BRICKS) {
                    world.setBlock(pos1, Blocks.INFESTED_MOSSY_STONE_BRICKS.defaultBlockState(), 3);
                }
                else if(block == Blocks.GRASS_BLOCK){
                    world.setBlock(pos1, Blocks.MYCELIUM.defaultBlockState(), 3);
                }
                else if(block == Blocks.SOUL_SOIL){
                    world.setBlock(pos1, Blocks.SOUL_SAND.defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            this.applyConfigPotion((LivingEntity)entity, Effects.UNLUCK, spellStats);
        }
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
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
