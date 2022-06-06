package com.dkmk100.arsomega.mixin;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityProjectileSpell.class)
public class ProjectileMixin {
    //mixin used to debug the tornado freeze, will keep around for a few versions just in case
    //if you end up here again to fix that, I'm so sorry
    //oh and remember to re-enable this file in mixin.json
    //ok, bye!
    /*
    @Inject(at = @At("HEAD"), method = "Lcom/hollingsworth/arsnouveau/common/entity/EntityProjectileSpell;getHitResult()Lnet/minecraft/world/phys/HitResult;", cancellable = true, remap = false)
    public void getHitResult(CallbackInfoReturnable<HitResult> cir) {
        EntityProjectileSpell thisProj = ((EntityProjectileSpell)(Object)this);
        boolean log = !thisProj.level.isClientSide();
        if(log) {
            ArsOmega.LOGGER.info("Projectile Spell Mixin");
            ArsOmega.LOGGER.info("Spell pos: " + thisProj.position());
        }
        Vec3 movement = thisProj.getDeltaMovement();
        if(log) {
            ArsOmega.LOGGER.info("movement pre: " + movement);
        }
        final float minSpeed = 1.0f;
        if(movement.lengthSqr() < minSpeed){
            if(log) {
                ArsOmega.LOGGER.warn("speed guard activated");
            }
            ((EntityProjectileSpell)(Object)this).setDeltaMovement(movement.normalize().scale(minSpeed));
            if(log) {
                ArsOmega.LOGGER.info("movement post: " + thisProj.getDeltaMovement());
            }
            cir.setReturnValue(null);
        }
        else{
            if(log) {
                ArsOmega.LOGGER.warn("continuing with clip attempt");
                ArsOmega.LOGGER.info("movement post: " + thisProj.getDeltaMovement());
            }
            Vec3 thisPosition = thisProj.position();
            Vec3 nextPosition = thisProj.getNextHitPosition();
            cir.setReturnValue(thisProj.level.clip(new ClipContext(thisPosition, nextPosition, thisProj.numSensitive > 0 ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)));
        }
    }
     */
}
