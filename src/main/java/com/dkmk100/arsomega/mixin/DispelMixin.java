package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDispel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collection;

@Mixin(EffectDispel.class)
public class DispelMixin {

    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/spell/effect/EffectDispel;onResolveEntity(Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)V", cancellable = true, remap = false)
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, CallbackInfo ci) {
        if (rayTraceResult.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity)rayTraceResult.getEntity();
            if(entity.hasEffect(ModPotions.DISPELLANT.get())) {
                ci.cancel();
            }
        }

    }
}
