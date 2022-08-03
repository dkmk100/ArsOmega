package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.ANExplosion;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class Fireball extends TierFourEffect implements IConfigurable{
    public static Fireball INSTANCE = new Fireball("fireball", "fireball");
    public ForgeConfigSpec.DoubleValue BASE;
    public ForgeConfigSpec.DoubleValue AOE_BONUS;
    public ForgeConfigSpec.DoubleValue AMP_DAMAGE;

    public Fireball(String tag, String description) {
        super(tag, description);
    }

    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Vector3d vec = this.safelyGetHitPos(rayTraceResult);
        double intensity;
        intensity = (Double) this.BASE.get() + (Double) this.AMP_VALUE.get() * spellStats.getAmpMultiplier() + (Double) this.AOE_BONUS.get() * (double) spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int dampen = spellStats.getBuffCount(AugmentDampen.INSTANCE);
        intensity -= 0.5D * (double)dampen;
        Explosion.Mode mode = dampen > 0 ? Explosion.Mode.NONE : Explosion.Mode.DESTROY;
        mode = spellStats.hasBuff(AugmentExtract.INSTANCE) ? Explosion.Mode.BREAK : mode;
        this.explode(world, shooter, (DamageSource)null, (ExplosionContext)null, vec.x, vec.y, vec.z, (float)intensity, true, mode, spellStats.getAmpMultiplier());
    }

    public Explosion explode(World world, @Nullable Entity e, @Nullable DamageSource source, @Nullable ExplosionContext context, double x, double y, double z, float radius, boolean p_230546_11_, Explosion.Mode p_230546_12_, double amp) {
        ANExplosion explosion = new ANExplosion(world, e, source, context, x, y, z, radius, p_230546_11_, p_230546_12_, amp);
        explosion.baseDamage = (Double)this.DAMAGE.get();
        explosion.ampDamageScalar = (Double)this.AMP_DAMAGE.get();
        if (ForgeEventFactory.onExplosionStart(world, explosion)) {
            return explosion;
        } else {
            explosion.explode();
            explosion.finalizeExplosion(false);
            if (p_230546_12_ == Explosion.Mode.NONE) {
                explosion.clearToBlow();
            }

            Iterator var17 = world.players().iterator();

            while(var17.hasNext()) {
                PlayerEntity serverplayerentity = (PlayerEntity)var17.next();
                if (serverplayerentity.distanceToSqr(x, y, z) < 4096.0D) {
                    ((ServerPlayerEntity)serverplayerentity).connection.send(new SExplosionPacket(x, y, z, radius, explosion.getToBlow(), (Vector3d)explosion.getHitPlayers().get(serverplayerentity)));
                }
            }

            return explosion;
        }
    }

    @Override
    public void buildExtraConfig(ForgeConfigSpec.Builder builder) {
        this.addAmpConfig(builder, 0.6D);
        this.BASE = builder.comment("Explosion base intensity").defineInRange("base", 1.2D, 0.0D, 100.0D);
        this.AOE_BONUS = builder.comment("AOE intensity bonus").defineInRange("aoe_bonus", 1.8D, 0.0D, 100.0D);
        this.addDamageConfig(builder, 6.0D);
        this.AMP_DAMAGE = builder.comment("Additional damage per amplify").defineInRange("amp_damage", 3D, 0.0D, 2.147483647E9D);
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
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE,AugmentAOE.INSTANCE,AugmentSensitive.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE});
    }
}
