package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.ANExplosion;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class Fireball extends TierFourEffect implements ConfigurableGlyph {
    public static Fireball INSTANCE = new Fireball("fireball", "fireball");
    public ForgeConfigSpec.DoubleValue BASE;
    public ForgeConfigSpec.DoubleValue AOE_BONUS;

    public ForgeConfigSpec.DoubleValue AMP_BONUS;
    public ForgeConfigSpec.DoubleValue AMP_DAMAGE;

    public ForgeConfigSpec.DoubleValue BASE_DAMAGE;

    public Fireball(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Vec3 vec = this.safelyGetHitPos(rayTraceResult);
        double intensity;
        intensity = this.BASE.get() + this.AMP_BONUS.get() * spellStats.getAmpMultiplier() + this.AOE_BONUS.get() * (double) spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int dampen = spellStats.getBuffCount(AugmentDampen.INSTANCE);
        intensity -= 0.5D * (double)dampen;
        Explosion.BlockInteraction mode = dampen > 0 ? Explosion.BlockInteraction.NONE : Explosion.BlockInteraction.DESTROY;
        mode = spellStats.hasBuff(AugmentExtract.INSTANCE) ? Explosion.BlockInteraction.BREAK : mode;
        this.explode(world, shooter, (DamageSource)null, (ExplosionDamageCalculator)null, vec.x, vec.y, vec.z, (float)intensity, true, mode, spellStats.getAmpMultiplier());
    }


    public Explosion explode(Level world, @Nullable Entity e, @Nullable DamageSource source, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float radius, boolean p_230546_11_, Explosion.BlockInteraction p_230546_12_, double amp) {
        ANExplosion explosion = new ANExplosion(world, e, source, context, x, y, z, radius, p_230546_11_, p_230546_12_, amp);
        explosion.baseDamage = BASE_DAMAGE.get();
        explosion.ampDamageScalar = AMP_DAMAGE.get();
        if (ForgeEventFactory.onExplosionStart(world, explosion)) {
            return explosion;
        } else {
            explosion.explode();
            explosion.finalizeExplosion(false);
            if (p_230546_12_ == Explosion.BlockInteraction.NONE) {
                explosion.clearToBlow();
            }

            Iterator var17 = world.players().iterator();

            while (var17.hasNext()) {
                Player serverplayerentity = (Player) var17.next();
                if (serverplayerentity.distanceToSqr(x, y, z) < 4096.0) {
                    ((ServerPlayer) serverplayerentity).connection.send(new ClientboundExplodePacket(x, y, z, radius, explosion.getToBlow(), (Vec3) explosion.getHitPlayers().get(serverplayerentity)));
                }
            }

            return explosion;
        }
    }

    @Override
    public void setConfig(ForgeConfigSpec spec) {
        this.CONFIG = spec;
    }
    @Override
    public void buildExtraConfig(ForgeConfigSpec.Builder builder) {
        this.BASE_DAMAGE = builder.comment("Base damage").defineInRange("base_damage", 6.0D, 0.0D, 100.0D);
        this.AMP_BONUS = builder.comment("AMP intensity bonus").defineInRange("amp_bonus", 0.6D, 0.0D, 100.0D);
        this.BASE = builder.comment("Base intensity").defineInRange("base", 1.2D, 0.0D, 100.0D);
        this.AOE_BONUS = builder.comment("AOE intensity bonus").defineInRange("aoe_bonus", 1.8D, 0.0D, 100.0D);
        this.AMP_DAMAGE = builder.comment("Additional damage per amplify").defineInRange("amp_damage", 3D, 0.0D, 100);
    }

    @Override
    public int getDefaultManaCost() {
        return 800;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE,AugmentAOE.INSTANCE,AugmentSensitive.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE});
    }
}
