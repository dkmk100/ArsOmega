package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLight;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class DemonicLight extends TierFourEffect {
    public static DemonicLight INSTANCE = new DemonicLight("demonic_light","Demonic Light");

    public DemonicLight(String name, String description) {
        super(name,description);
    }

    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (rayTraceResult.getEntity() instanceof ILightable) {
            ((ILightable)rayTraceResult.getEntity()).onLight(rayTraceResult, world, shooter, spellStats, spellContext);
        }

        if (rayTraceResult.getEntity() instanceof LivingEntity) {
            if(!spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
                this.applyConfigPotion((LivingEntity) rayTraceResult.getEntity(), Effects.GLOWING, spellStats);
            }
            this.applyConfigPotion((LivingEntity)rayTraceResult.getEntity(), Effects.NIGHT_VISION, spellStats,false);
        }
    }

    //to change later
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        if (BlockUtil.destroyRespectsClaim(this.getPlayer(shooter, (ServerWorld)world), world, pos)) {
            if (world.getBlockEntity(rayTraceResult.getBlockPos()) instanceof ILightable) {
                ((ILightable)world.getBlockEntity(rayTraceResult.getBlockPos())).onLight(rayTraceResult, world, shooter, spellStats, spellContext);
            } else {
                if (world.getBlockState(pos).getMaterial().isReplaceable() && world.isUnobstructed(BlockRegistry.LIGHT_BLOCK.defaultBlockState(), pos, ISelectionContext.of(ANFakePlayer.getPlayer((ServerWorld)world)))) {
                    world.setBlockAndUpdate(pos, (BlockState)BlockRegistry.LIGHT_BLOCK.defaultBlockState().setValue(SconceBlock.LIGHT_LEVEL, Math.max(0, Math.min(15, 14 + (int)spellStats.getAmpMultiplier()))));
                    LightTile tile = (LightTile)world.getBlockEntity(pos);
                    if (tile != null) {
                        tile.red = spellContext.colors.r;
                        tile.green = spellContext.colors.g;
                        tile.blue = spellContext.colors.b;
                    }
                }

            }
        }
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addPotionConfig(builder, 45);
    }

    public int getManaCost() {
        return 250;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDurationDown.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE, AugmentSensitive.INSTANCE});
    }

    public String getBookDescription() {
        return "If cast on a block, a permanent light source is created. May be amplified up to Glowstone brightness, or Dampened for a lower light level. When cast on yourself, you will receive night vision. When cast on other entities, they will receive Night Vision and Glowing.";
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.CONJURATION});
    }
}
