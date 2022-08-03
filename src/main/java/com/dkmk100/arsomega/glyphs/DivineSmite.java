package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityDivineSmite;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class DivineSmite extends TierFourEffect implements IConfigurable{

    public static DivineSmite INSTANCE = new DivineSmite("divine_smite","Divine Smite");

    public DivineSmite(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Vector3d pos = this.safelyGetHitPos(rayTraceResult);
        EntityDivineSmite lightningBoltEntity = new EntityDivineSmite(RegistryHandler.DIVINE_SMITE.get(), world);
        lightningBoltEntity.setPos(pos.x(), pos.y(), pos.z());
        lightningBoltEntity.setCause(shooter instanceof ServerPlayerEntity ? (ServerPlayerEntity)shooter : null);
        lightningBoltEntity.setAoe(spellStats.getBuffCount(AugmentAOE.INSTANCE));
        lightningBoltEntity.setSensitive(spellStats.hasBuff(AugmentSensitive.INSTANCE));
        lightningBoltEntity.setDamage(this.DAMAGE.get().floatValue() + (float)((this.AMP_VALUE.get()) * spellStats.getAmpMultiplier()));
        world.addFreshEntity(lightningBoltEntity);
    }

    @Override
    public void buildExtraConfig(ForgeConfigSpec.Builder builder) {
        this.addDamageConfig(builder, 9.0);
        this.addAmpConfig(builder, 4.0);
    }

    @Override
    public int getManaCost() {
        return 1000;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentSensitive.INSTANCE,AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE});
    }
}
