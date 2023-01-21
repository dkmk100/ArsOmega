package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityDivineSmite;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class DivineSmite  extends TierFourEffect {

    public static DivineSmite INSTANCE = new DivineSmite("divine_smite","Divine Smite");

    public DivineSmite(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Vec3 pos = this.safelyGetHitPos(rayTraceResult);
        EntityDivineSmite lightningBoltEntity = new EntityDivineSmite(RegistryHandler.DIVINE_SMITE.get(), world);
        lightningBoltEntity.setPos(pos.x(), pos.y(), pos.z());
        lightningBoltEntity.setCause(shooter instanceof ServerPlayer ? (ServerPlayer)shooter : null);
        lightningBoltEntity.setAoe((float)spellStats.getAoeMultiplier());
        lightningBoltEntity.setSensitive(spellStats.hasBuff(AugmentSensitive.INSTANCE));
        lightningBoltEntity.setDamage(this.DAMAGE.get().floatValue() + (float)((this.AMP_VALUE.get()) * spellStats.getAmpMultiplier()));
        world.addFreshEntity(lightningBoltEntity);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addDamageConfig(builder, 9.0);
        this.addAmpConfig(builder, 4.0);
    }

    @Override
    public int getDefaultManaCost() {
        return 1000;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentSensitive.INSTANCE,AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE});
    }
}

