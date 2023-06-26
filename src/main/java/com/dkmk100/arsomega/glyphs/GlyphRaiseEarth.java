package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectKnockback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GlyphRaiseEarth  extends TierFourEffect implements IDamageEffect {

    public static GlyphRaiseEarth INSTANCE = new GlyphRaiseEarth("raise_earth","Raise Earth");
    final static int maxCheckUp = 4;

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        //raise actual earth
        BlockPos pos1 = rayTraceResult.getBlockPos();
        double aoeBuff = spellStats.getAoeMultiplier();

        BlockHitResult newHit = new BlockHitResult(rayTraceResult.getLocation(), Direction.DOWN, rayTraceResult.getBlockPos(), rayTraceResult.isInside());

        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos1, newHit, aoeBuff, 0);
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
                BlockPos targetPos = pos.below(i);
                BlockState targetState = world.getBlockState(targetPos);
                if (targetState.isAir()) {
                    targetState = Blocks.STONE.defaultBlockState();
                }
                //don't destroy bedrock
                if(targetState.getBlock().defaultDestroyTime() < 0 || targetState.getMaterial().getPushReaction() == PushReaction.BLOCK || world.getBlockEntity(targetPos) != null){
                    break;
                }
                world.setBlockAndUpdate(pos.above(actualHeight - i), targetState);
                world.setBlockAndUpdate(targetPos, Blocks.STONE.defaultBlockState());
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
        List<Entity> list = world.getEntities(shooter, new AABB(x - 3.0D, y - 3.0D, z - 3.0D, x + 3.0D, y + highestReached + 3.0D, z + 3.0D).inflate(aoeBuff), test);
        for(Entity entity : list){
            entity.setPos(entity.position().x,pos1.getY() + highestReached + 2, entity.position().z);
            if(shooter!=null) {
                double speed = 2 + (0.3f * spellStats.getAmpMultiplier()) + (0.5f * spellStats.getBuffCount(AugmentPierce.INSTANCE) + spellStats.getBuffCount(AugmentAccelerate.INSTANCE));
                entity.hurtMarked = true;
                entity.hasImpulse = true;
                EffectKnockback.INSTANCE.knockback(entity, shooter, (float)speed);
            }

            float damage = 4 + 2*(float)spellStats.getAmpMultiplier();

            this.attemptDamage(world,shooter,spellStats,spellContext,resolver, entity,  DamageSource.FALL, damage);
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    public GlyphRaiseEarth(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public int getDefaultManaCost() {
        return 350;//kinda cheap since it's not meant as a big combat move but a medium-size blocking move
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_EARTH});
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE,AugmentPierce.INSTANCE, AugmentAccelerate.INSTANCE});
    }
}
