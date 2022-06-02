package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.potions.ModPotions;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectBlink.class)
public class BlinkMixin {

    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/spell/effect/EffectBlink;warpEntity(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)V", cancellable = true, remap = false)
    private static void warpEntity(Entity entity, BlockPos warpPos, CallbackInfo cr) {
        if (entity != null) {
            if(entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(ModPotions.DEMONIC_ANCHORING)){
                cr.cancel();
            }
            else {
                Level world = entity.level;
                ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY() + 1.0, entity.getZ(), 4, (world.random.nextDouble() - 0.5) * 2.0, -world.random.nextDouble(), (world.random.nextDouble() - 0.5) * 2.0, 0.10000000149011612);
                entity.teleportTo((double) warpPos.getX() + 0.5, (double) warpPos.getY(), (double) warpPos.getZ() + 0.5);
                Networking.sendToNearby(world, entity, new PacketWarpPosition(entity.getId(), entity.getX(), entity.getY(), entity.getZ(), entity.getXRot(), entity.getYRot()));
                entity.level.playSound((Player) null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, (double) warpPos.getX() + 0.5, (double) warpPos.getY() + 1.0, (double) warpPos.getZ() + 0.5, 4, (world.random.nextDouble() - 0.5) * 2.0, -world.random.nextDouble(), (world.random.nextDouble() - 0.5) * 2.0, 0.10000000149011612);
            }
        }
    }
}
