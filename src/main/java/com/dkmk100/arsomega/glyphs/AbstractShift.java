package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.LevelUtil;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBlink;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public abstract class AbstractShift extends TierFourEffect {

    public AbstractShift(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(shooter instanceof Player player && isNotFakePlayer(player)) {
            super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);

            boolean longRange = allowLongRange() ? spellStats.hasBuff(AugmentSensitive.INSTANCE) : false;

            ArsOmega.LOGGER.info("is long range: "+longRange);

            BlockPos pos;

            if (rayTraceResult instanceof BlockHitResult blockHit) {
                pos = blockHit.isInside() ? blockHit.getBlockPos() : blockHit.getBlockPos().relative(blockHit.getDirection());
            } else if (rayTraceResult instanceof EntityHitResult entityHit) {
                pos = entityHit.getEntity().blockPosition();
            } else {
                return;
            }

            int light = LevelUtil.getAdjustedLightValue(pos, world);

            double maxDistance = getRange(spellStats, spellContext, longRange);

            Vec3 vec = rayTraceResult.getLocation();
            double dist = vec.distanceTo(shooter.position());

            boolean lightValid = validLightLevel(light, spellStats, spellContext, longRange, false);
            boolean casterLightValid = true;

            if(longRange){
                int casterLight = LevelUtil.getAdjustedLightValue(shooter.blockPosition(),world);
                casterLightValid = validLightLevel(casterLight, spellStats, spellContext, true, true);
                ArsOmega.LOGGER.info("caster light valid: "+casterLightValid);
            }

            if (EffectBlink.isValidTeleport(world, pos)) {
                if (dist > maxDistance) {
                    ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, shooter.position().x, shooter.getEyeY(), shooter.position().z,
                            4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                    PortUtil.sendMessageNoSpam(player, Component.literal("Location out of range"));
                }else if(!casterLightValid){
                    ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, shooter.position().x, shooter.getY(), shooter.position().z,
                            4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                    PortUtil.sendMessageNoSpam(player, Component.literal("invalid light level at your position"));
                }
                else if (!lightValid) {
                    ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, vec.x, vec.y, vec.z,
                            4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                    PortUtil.sendMessageNoSpam(player, Component.literal("invalid light level at target position"));
                } else {
                    LevelUtil.anchoringImmuneWarp(shooter, new BlockPos(vec));
                }
            }
        }

    }

    @Override
    protected void buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        super.buildAugmentLimitsConfig(builder, defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(), 6);
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 6);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return this.setOf(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE);
    }

    protected abstract boolean validLightLevel(int light, SpellStats stats, SpellContext context, boolean isLongRange, boolean isCasterLocation);

    protected abstract double getRange(SpellStats stats, SpellContext context, boolean isLongRange);

    protected boolean allowLongRange(){
        return true;
    }
}
