package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Heartstop extends AbstractEffect implements ConfigurableGlyph{

    public static final DamageSource HEARTSTOP_DAMAGE = new DamageSource("heartstop").bypassArmor().bypassMagic();

    public static Heartstop INSTANCE = new Heartstop("heartstop", "Heartstop");

    public ForgeConfigSpec.DoubleValue BASE;
    public ForgeConfigSpec.DoubleValue AMP_DAMAGE;

    public Heartstop(String tag, String description) {
            super(tag, description);
        }

    @Override
    public int getDefaultManaCost() {
        return 550;
    }
    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            float damage = (float)(this.DAMAGE.get() + this.AMP_DAMAGE.get() * spellStats.getAmpMultiplier());
            float mult = 0;
            float add = 0;
            if(living.hasEffect(ModPotions.DEMONIC_CURSE)){
                mult += 3;
                add += 6;
            }
            if(living.hasEffect(ModPotions.ADRENALINE)){
                mult -= 3;
                add -= 3;
            }
            if(living.hasEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.SHOCKED_EFFECT)){
                mult += 3;
                add += 1;
                int amp = living.getEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.SHOCKED_EFFECT).getAmplifier();
                mult += 1.5f * amp;
                add += 2 * amp;
            }
            if(living.hasEffect(com.hollingsworth.arsnouveau.common.potions.ModPotions.SNARE_EFFECT)){
                mult += 2f;
                add += 3;
            }

            //should go last always
            if(living.hasEffect(ModPotions.BLOOD_CLOT)){
                //lol, multiplying the multiplier so as to increase other effects, but not have the rest grow each other exponentially
                //yea this is kinda silly but oh well
                mult = mult*1.5f;
                mult += 8;
                add += 10;
            }

            //multiply some of the addition lol
            damage += add*0.15f;
            damage = damage * (1.0f + 0.1f*mult);
            damage += add*0.85f;

            this.dealDamage(world,shooter,damage,spellStats,living, HEARTSTOP_DAMAGE);
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120));
        }
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentFortune.INSTANCE});
    }

    @Override
    public void buildExtraConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addDamageConfig(builder, 2.0D);
        this.AMP_DAMAGE = builder.comment("Additional damage per amplify").defineInRange("amp_damage", 0.5D, 0.0D, 2.147483647E9D);
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.LIFE});
    }
}
