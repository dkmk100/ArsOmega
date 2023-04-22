package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityTornado;
import com.dkmk100.arsomega.entities.EntityWhirlpool;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class GlyphWhirlpool  extends TierFourEffect{

    public static GlyphWhirlpool INSTANCE = new GlyphWhirlpool("whirlpool","Whirlpool");

    public GlyphWhirlpool(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(!this.canSummon(shooter)){
            return;
        }
        EntityWhirlpool tornado = new EntityWhirlpool(world,shooter);
        Vec3 pos = rayTraceResult.getLocation();
        tornado.setColor(spellContext.getColors());
        tornado.setPos(pos.x,pos.y + 0.5,pos.z);
        int ticks = 250 + (int)Math.round(70 * spellStats.getDurationMultiplier());
        tornado.setDuration(ticks);
        tornado.setAccelerate(spellStats.getBuffCount(AugmentAccelerate.INSTANCE));
        tornado.setAoe((int)spellStats.getAoeMultiplier());
        world.addFreshEntity(tornado);

        if(shooter!=null) {
            shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS_EFFECT.get(),ticks));
        }
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public int getDefaultManaCost() {
        return 850;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAccelerate.INSTANCE});
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
    return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_WATER});
    }
}

