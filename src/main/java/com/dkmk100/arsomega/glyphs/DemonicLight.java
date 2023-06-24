package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.blocks.DemonicLightTile;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class DemonicLight extends TierFourEffect {
    public static DemonicLight INSTANCE = new DemonicLight("demonic_light","Demonic Light");

    public DemonicLight(String name, String description) {
        super(RegistryHandler.getGlyphName(name),description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof ILightable) {
            ((ILightable)rayTraceResult.getEntity()).onLight(rayTraceResult, world, shooter, spellStats, spellContext);
        }

        if (rayTraceResult.getEntity() instanceof LivingEntity) {
                this.applyConfigPotion((LivingEntity) rayTraceResult.getEntity(), MobEffects.GLOWING, spellStats);
                this.applyConfigPotion((LivingEntity) rayTraceResult.getEntity(), MobEffects.NIGHT_VISION, spellStats, false);
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    //to change later
    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        if (BlockUtil.destroyRespectsClaim(this.getPlayer(shooter, (ServerLevel)world), world, pos)) {
            BlockPos rawPos = rayTraceResult.getBlockPos();
            Block block = world.getBlockState(rawPos).getBlock();
            if (world.getBlockEntity(rawPos) instanceof ILightable) {
                ((ILightable)world.getBlockEntity(rayTraceResult.getBlockPos())).onLight(rayTraceResult, world, shooter, spellStats, spellContext);
            }
            else if(block == Blocks.GLOWSTONE){
                world.setBlockAndUpdate(rawPos, Blocks.DEEPSLATE_COAL_ORE.defaultBlockState());
            }
            else if(block == RegistryHandler.DEMONIC_GLOWSTONE.get()){
                world.setBlockAndUpdate(rawPos, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState());
            }
            else {
                if (!spellStats.hasBuff(AugmentSensitive.INSTANCE) && world.getBlockState(pos).getMaterial().isReplaceable() && world.isUnobstructed(RegistryHandler.DEMONIC_LIGHT.get().defaultBlockState(), pos, CollisionContext.of(ANFakePlayer.getPlayer((ServerLevel)world)))) {
                    world.setBlockAndUpdate(pos, RegistryHandler.DEMONIC_LIGHT.get().defaultBlockState().setValue(SconceBlock.LIGHT_LEVEL, Math.max(0, Math.min(15, 14 + (int)spellStats.getAmpMultiplier()))));
                    DemonicLightTile tile = (DemonicLightTile)world.getBlockEntity(pos);
                    if (tile != null) {
                        tile.color = spellContext.getColors();
                    }
                }

            }
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addPotionConfig(builder, 45);
    }

    @Override
    public int getDefaultManaCost() {
        return 125;
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDurationDown.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE, AugmentSensitive.INSTANCE});
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.CONJURATION});
    }
}
