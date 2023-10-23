package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.LevelUtil;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBlink;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DarkShift extends AbstractShift{

    public static DarkShift INSTANCE = new DarkShift("dark_shift","Dark Shift");

    public DarkShift(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(shooter instanceof Player player && isNotFakePlayer(player)){
            Vec3 vec = rayTraceResult.getLocation();

            int amp = (int) spellStats.getAmpMultiplier();
            double aoe = spellStats.getAoeMultiplier();

            BlockPos pos = rayTraceResult.isInside() ? rayTraceResult.getBlockPos() : rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());

            int light = LevelUtil.getAdjustedLightValue(pos, world);
            int maxLight = Math.min(8,3 + amp);

            double maxDistance = 10 + 2*aoe;

            double dist = vec.distanceTo(shooter.position());

            if (isRealPlayer(shooter) && EffectBlink.isValidTeleport(world, (rayTraceResult).getBlockPos().relative((rayTraceResult).getDirection()))) {
                if(dist > maxDistance){
                    ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, shooter.position().x, shooter.getEyeY(), shooter.position().z,
                            4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                }
                else if (light > maxLight) {
                    ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, vec.x, vec.y, vec.z,
                            4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                }
                else {
                    LevelUtil.anchoringImmuneWarp(shooter, new BlockPos(vec));
                }
            }
        }
    }

    @Override
    protected boolean validLightLevel(int light, SpellStats stats, SpellContext context, boolean isLongRange, boolean isCasterLocation) {
        int amp = (int) stats.getAmpMultiplier();
        int maxLight = Math.min(8,3 + amp);
        return light < maxLight;
    }

    @Override
    protected double getRange(SpellStats stats, SpellContext context, boolean isLongRange) {
        double aoe = stats.getAoeMultiplier();
        return isLongRange ? 30 + 3 * aoe : 10 + 2 * aoe;
    }


    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.CELESTIAL,SpellSchools.MANIPULATION});
    }
}

