package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityEarthquake;
import com.dkmk100.arsomega.entities.EntityTornado;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Earthquake  extends TierFourEffect{


    public static Earthquake INSTANCE = new Earthquake("earthquake","Earthquake");

    public Earthquake(String tag, String description) {
        super(tag, description);
    }


    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        EntityEarthquake quake = new EntityEarthquake(world,shooter);
        Vec3 pos = rayTraceResult.getLocation();
        quake.setColor(spellContext.getColors());
        quake.setPos(pos.x,pos.y + 0.5,pos.z);
        int ticks = 50 + (int)Math.round(25 * spellStats.getDurationMultiplier());
        quake.setDuration(ticks);
        quake.setAccelerate((int) spellStats.getAccMultiplier());
        quake.setAoe((float) spellStats.getAoeMultiplier());
        quake.setAmp((int) Math.round(spellStats.getAmpMultiplier()));
        world.addFreshEntity(quake);
    }

    @Override
    public int getDefaultManaCost() {
        return 1000;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAccelerate.INSTANCE, AugmentAmplify.INSTANCE});
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_EARTH});
    }
}
