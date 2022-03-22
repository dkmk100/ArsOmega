package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Melt extends AbstractEffect {

    public static Melt INSTANCE = new Melt("melt", "Melt");

    public Melt(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(world instanceof ServerWorld){
            int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
            double amp = spellStats.getAmpMultiplier();
            int passes = (int)Math.round((amp+1)/3) + 1;
            BlockPos pos = rayTraceResult.getBlockPos();
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, 0);

            Iterator var12 = posList.iterator();
            while(var12.hasNext()) {
                BlockPos pos1 = (BlockPos) var12.next();
                Block block = world.getBlockState(pos1).getBlock();
                Block old = block;
                for(int i=0;i<passes;i++) {
                    if (block == Blocks.OBSIDIAN && passes>=6) {
                        block = Blocks.COBBLESTONE;
                        i+=5;
                    }
                    else if (block == Blocks.COBBLESTONE) {
                        block = Blocks.STONE;
                    }
                    else if (block == Blocks.STONE || Tags.Blocks.STONE.contains(block)) {
                        block = Blocks.BASALT;
                    }
                    else if (block == Blocks.BASALT) {
                        block = Blocks.MAGMA_BLOCK;
                    }
                    else if(block == Blocks.MAGMA_BLOCK){
                        block = Blocks.LAVA;
                    }
                }
                if(block!=old) {
                    world.setBlock(pos1, block.defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof MobEntity) {
            MobEntity living = (MobEntity) entity;
            float damage = 2.0f;
            Iterable<ItemStack> armor = living.getArmorSlots();
            for(ItemStack stack : armor){
                if(!stack.isEmpty()){
                    damage += 3;
                }
            }
            this.dealDamage(world,shooter,damage,spellStats,living,DamageSource.LAVA);
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
    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE,SpellSchools.ELEMENTAL_EARTH});
    }
}
