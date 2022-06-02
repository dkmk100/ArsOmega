package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.glyphs.DilateTime;
import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientDelayEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDelay;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

@Mixin(EffectDelay.class)
public abstract class DelayMixin extends AbstractEffect {

    public DelayMixin(String tag, String description) {
        super(tag, description);
    }

    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/spell/effect/EffectDelay;sendPacket(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/HitResult;Lnet/minecraft/world/entity/LivingEntity;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/entity/Entity;)V", cancellable = true, remap = false)
    public void sendPacket(Level world, HitResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats spellStats, BlockHitResult blockResult, Entity hitEntity, CallbackInfo ci) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
            //add dilate time compat
            int duration = (Integer)this.GENERIC_INT.get() + (Integer)this.EXTEND_TIME.get() * (spellStats.getBuffCount(AugmentExtendTime.INSTANCE) + spellStats.getBuffCount(DilateTime.INSTANCE) * 2) * 20 - (Integer)this.EXTEND_TIME.get() / 2 * spellStats.getBuffCount(AugmentDurationDown.INSTANCE) * 20;
            EventQueue.getServerInstance().addEvent(new DelayedSpellEvent(duration, newSpell, rayTraceResult, world, shooter, newContext));
            Networking.sendToNearby(world, new BlockPos(this.safelyGetHitPos(rayTraceResult)), new PacketClientDelayEffect(duration, shooter, newSpell, newContext, blockResult, hitEntity));
        }
        ci.cancel();
    }
}
