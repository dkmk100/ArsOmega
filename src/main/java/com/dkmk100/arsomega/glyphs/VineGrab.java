package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class VineGrab extends AbstractEffect {


    public static VineGrab INSTANCE = new VineGrab("vine_grab", "vinegrab");

    public VineGrab(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double amp = spellStats.getAmpMultiplier() + 2;
        int time = spellStats.getBuffCount(AugmentExtendTime.INSTANCE);

        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity living = (LivingEntity)rayTraceResult.getEntity();
            living.addEffect(new MobEffectInstance(ModPotions.VINE_BIND,50 + 20*time));
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 300;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.NATURE});
    }
}
