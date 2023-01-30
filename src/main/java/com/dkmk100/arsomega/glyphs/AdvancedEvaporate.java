package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.rituals.RitualTribute;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectEvaporate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AdvancedEvaporate extends AbstractEffect {
    public static AdvancedEvaporate INSTANCE = new AdvancedEvaporate();

    private AdvancedEvaporate() {
        super("advanced_evaporate", "Advanced Evaporate");
    }



    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        Iterator var8 = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE)).iterator();

        HashMap<Item, Integer> toDrop = new HashMap<>();

        while(var8.hasNext()) {
            BlockPos p = (BlockPos)var8.next();
            this.evaporate(world, p, rayTraceResult, shooter, spellStats, toDrop);
            Direction[] var10 = Direction.values();
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                Direction d = var10[var12];
                this.evaporate(world, p.relative(d), rayTraceResult, shooter, spellStats, toDrop);
            }
        }

        for(var drop : toDrop.entrySet()){
            int left = drop.getValue();
            Item item = drop.getKey();
            while(left > 0) {
                spawnAtLocation(new ItemStack(item,Math.min(left,64)), world, pos);
                left -= 64;
            }
        }

    }

    public void evaporate(Level world, BlockPos p, BlockHitResult rayTraceResult, LivingEntity shooter, SpellStats stats, HashMap<Item, Integer> toDrop) {
        Block block = world.getBlockState(p).getBlock();
        if (!world.getFluidState(p).isEmpty() && block instanceof LiquidBlock) {
            if(world.getFluidState(p).isSource()){
                if(block == Blocks.WATER){
                    if(world.getBiome(p).is(Biomes.SWAMP)){
                        AddDrop(Items.SLIME_BALL, toDrop,world,0.05f + 0.05f * stats.getBuffCount(AugmentFortune.INSTANCE));

                    }
                    else{
                        AddDrop(RegistryHandler.SALT.get(), toDrop,world,0.05f + 0.05f * stats.getBuffCount(AugmentFortune.INSTANCE));
                    }
                }
                else if(block == Blocks.LAVA){
                    if(world.getBiome(p).is(BiomeTags.IS_NETHER)){
                        AddDrop(Items.MAGMA_CREAM, toDrop,world,0.5f);
                    }
                }
            }
            world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
        }


    }

    void AddDrop(Item item, HashMap<Item, Integer> toDrop, Level world, float chance) {
        if(world.random.nextFloat() < chance) {
            toDrop.put(item, toDrop.getOrDefault(item, 0) + 1);
        }
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nullable
    public ItemEntity spawnAtLocation(ItemStack p_70099_1_, Level world, BlockPos pos) {
        if (p_70099_1_.isEmpty()) {
            return null;
        } else if (world.isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(world, pos.getX(),pos.getY(),pos.getZ(), p_70099_1_);
            itementity.setDefaultPickUpDelay();
            world.addFreshEntity(itementity);
            return itementity;
        }
    }

    public int getDefaultManaCost() {
        return 250;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.setOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentFortune.INSTANCE});
    }
}

