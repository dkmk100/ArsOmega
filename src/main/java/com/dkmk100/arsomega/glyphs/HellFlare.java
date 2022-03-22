package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class HellFlare extends TierFourEffect {

    public static HellFlare INSTANCE = new HellFlare("hell_flare", "hell_flare");

    public HellFlare(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            Vector3d vec = this.safelyGetHitPos(rayTraceResult);
            float damage = (float)(8 + 4 * spellStats.getAmpMultiplier());
            int range = 4 + Math.round(1.5f * spellStats.getBuffCount(AugmentAOE.INSTANCE));
            int fireSec = (int)(5.0D + 1.2D * spellStats.getDurationMultiplier());
            DamageSource source = this.buildDamageSource(world, shooter).setIsFire();
            if (livingEntity.isOnFire()) {
                ((ServerWorld)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5D, vec.z, 50, ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), 0.3D);
                Iterator var13 = world.getEntities(shooter, new AxisAlignedBB(livingEntity.blockPosition().north(range).east(range).above(range), livingEntity.blockPosition().south(range).west(range).below(range))).iterator();
                boolean soul = livingEntity.hasEffect(ModPotions.SOUL_FIRE);
                while(var13.hasNext()) {
                    Entity e = (Entity)var13.next();
                    if (!e.equals(livingEntity) && e instanceof LivingEntity) {
                        this.dealDamage(world, shooter, damage, spellStats, e, source);
                        e.setSecondsOnFire(fireSec);
                        if(soul) {
                            EffectInstance effect = livingEntity.getEffect(ModPotions.SOUL_FIRE);
                            ((LivingEntity) e).addEffect(new EffectInstance(ModPotions.SOUL_FIRE, effect.getDuration(), effect.getAmplifier(),true,true));
                        }
                        vec = e.position();
                        ((ServerWorld)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5D, vec.z, 50, ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), 0.3D);
                    }
                }
                this.dealDamage(world, shooter, damage, spellStats, livingEntity, source);
            }

        }
    }

    @Override
    public int getManaCost() {
        return 800;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE,AugmentAOE.INSTANCE,AugmentSensitive.INSTANCE, AugmentFortune.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE});
    }
}
