package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
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
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LightShift extends TierFourEffect{

    public LightShift INSTANCE = new LightShift("light_shift","Light Shift");

    public LightShift(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(shooter instanceof Player player && isNotFakePlayer(player)){
            Vec3 vec = rayTraceResult.getLocation();
            BlockPos pos = rayTraceResult.getBlockPos();

            if (isRealPlayer(shooter) && EffectBlink.isValidTeleport(world, (rayTraceResult).getBlockPos().relative((rayTraceResult).getDirection()))) {
                warpEntity(shooter, new BlockPos(vec));
            }
        }
    }



    //based on the one in Ars's blink effect
    //modified to not use the
    public static void warpEntity(Entity entity, BlockPos warpPos) {
        if (entity == null) return;
        Level world = entity.level;
        if (entity instanceof LivingEntity living){
            //on purpose not the ender entity event so that it isn't cancelled by demonic anchoring
            EntityTeleportEvent event = new EntityTeleportEvent(living, warpPos.getX(), warpPos.getY(), warpPos.getZ());
            if (event.isCanceled()) return;
        }
        ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY() + 1, entity.getZ(),
                4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);

        entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
        Networking.sendToNearby(world, entity, new PacketWarpPosition(entity.getId(), entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot()));
        entity.level.playSound(null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0f, 1.0f);
        ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, entity.blockPosition().getX() + 0.5, entity.blockPosition().getY() + 1.0, entity.blockPosition().getZ() + 0.5,
                4, (world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return this.setOf();
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.CELESTIAL,SpellSchools.MANIPULATION});
    }
}
