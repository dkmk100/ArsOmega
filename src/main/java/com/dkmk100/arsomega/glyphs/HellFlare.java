package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class HellFlare extends TierFourEffect implements  IDamageEffect {

    public static HellFlare INSTANCE = new HellFlare("hell_flare", "hell_flare");

    public HellFlare(String tag, String description) {
        super(tag, description);
    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        EntityDamageSource damageSource = new EntityDamageSource("hell_flare", (Entity)(shooter == null ? ANFakePlayer.getPlayer((ServerLevel)world) : shooter));
        damageSource.setMagic().setIsFire();
        return damageSource;
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            Vec3 vec = this.safelyGetHitPos(rayTraceResult);
            float damage = (float)(8 + 4 * spellStats.getAmpMultiplier());
            int range = 4 + (int)Math.round(1.5f * spellStats.getAoeMultiplier());
            int fireSec = (int)(5.0D + 1.2D * spellStats.getDurationMultiplier());
            DamageSource source = buildDamageSource(world, shooter);
            if (livingEntity.isOnFire()) {
                ((ServerLevel)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5D, vec.z, 50, ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), 0.3D);
                Iterator var13 = world.getEntities(shooter, new AABB(livingEntity.blockPosition().north(range).east(range).above(range), livingEntity.blockPosition().south(range).west(range).below(range))).iterator();
                boolean soul = livingEntity.hasEffect(ModPotions.SOUL_FIRE);
                while(var13.hasNext()) {
                    Entity e = (Entity)var13.next();
                    if (!e.equals(livingEntity) && e instanceof LivingEntity) {
                        this.dealDamage(world, shooter, damage, spellStats, e, source);
                        e.setSecondsOnFire(fireSec);
                        if(soul) {
                            MobEffectInstance effect = livingEntity.getEffect(ModPotions.SOUL_FIRE);
                            ((LivingEntity) e).addEffect(new MobEffectInstance(ModPotions.SOUL_FIRE, effect.getDuration(), effect.getAmplifier(),true,true));
                        }
                        vec = e.position();
                        ((ServerLevel)world).sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5D, vec.z, 50, ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), ParticleUtil.inRange(-0.1D, 0.1D), 0.3D);
                    }
                }
                this.dealDamage(world, shooter, damage, spellStats, livingEntity, source);
            }

        }
    }

    @Override
    public int getDefaultManaCost() {
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
