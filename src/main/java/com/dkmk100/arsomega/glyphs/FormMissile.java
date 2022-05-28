package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityMissileSpell;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FormMissile extends AbstractCastMethod {
    public static FormMissile INSTANCE = new FormMissile("missile","Missile");

    private FormMissile(String tag, String description) {
        super(tag,description);
    }

    public int getDefaultManaCost() {
        return 20;
    }

    public void summonProjectiles(Level world, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        final boolean activate = true;
        int duration = 50 + (int)Math.round(15f * stats.getDurationMultiplier());
        duration = Math.max(7,duration);
        ArrayList<EntityMissileSpell> projectiles = new ArrayList();
        EntityMissileSpell projectileSpell = new EntityMissileSpell(world, resolver,duration,activate,stats.getBuffCount(AugmentAOE.INSTANCE),shooter);
        projectiles.add(projectileSpell);
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        for(int i = 1; i < numSplits + 1; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            BlockPos projPos = shooter.blockPosition().relative(offset, i);
            projPos = projPos.offset(0.0D, 1.5D, 0.0D);
            EntityMissileSpell spell = new EntityMissileSpell(world, resolver,duration,activate,stats.getBuffCount(AugmentAOE.INSTANCE),shooter);
            spell.setPos((double)projPos.getX(), (double)projPos.getY(), (double)projPos.getZ());
            projectiles.add(spell);
        }

        float velocity = 1.0F + (float)stats.getBuffCount(AugmentAccelerate.INSTANCE);
        Iterator var14 = projectiles.iterator();

        while(var14.hasNext()) {
            EntityMissileSpell proj = (EntityMissileSpell)var14.next();
            try {
                proj.shoot(shooter, ReflectionHandler.xRot.getFloat(shooter), ReflectionHandler.yRot.getFloat(shooter), 0.0F, velocity, 0.8F);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            world.addFreshEntity(proj);
        }

    }

    public void summonProjectiles(Level world, BlockPos pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        final boolean activate = true;
        int duration = 50 + (int)Math.round(15f * stats.getDurationMultiplier());
        duration = Math.max(7,duration);
        ArrayList<EntityMissileSpell> projectiles = new ArrayList();
        EntityMissileSpell projectileSpell = new EntityMissileSpell(world, resolver,duration,activate,stats.getBuffCount(AugmentAOE.INSTANCE),shooter);
        projectileSpell.setPos((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ());
        projectiles.add(projectileSpell);
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        for(int i = 1; i < numSplits + 1; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            BlockPos projPos = pos.relative(offset, i);
            projPos = projPos.offset(0.0D, 1.5D, 0.0D);
            EntityMissileSpell spell = new EntityMissileSpell(world, resolver,duration,activate,stats.getBuffCount(AugmentAOE.INSTANCE),shooter);
            spell.setPos((double)projPos.getX(), (double)projPos.getY(), (double)projPos.getZ());
            projectiles.add(spell);
        }

        Iterator var14 = projectiles.iterator();

        while(var14.hasNext()) {
            EntityMissileSpell proj = (EntityMissileSpell)var14.next();
            proj.setDeltaMovement(new Vec3(0.0D, -0.1D, 0.0D));
            world.addFreshEntity(proj);
        }

    }

    @Override
    public void onCast(ItemStack stack, LivingEntity shooter, Level world, SpellStats stats, SpellContext context, SpellResolver resolver) {
        this.summonProjectiles(world, shooter, stats, resolver);
        resolver.expendMana(shooter);
    }

    @Override
    public void onCastOnBlock(UseOnContext context, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        Player shooter = context.getPlayer();
        this.summonProjectiles(world, shooter, stats, resolver);
        resolver.expendMana(shooter);
    }

    @Override
    public void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster,  SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        caster.lookAt(EntityAnchorArgument.Anchor.EYES, blockRayTraceResult.getLocation().add(0.0D, 0.0D, 0.0D));
        this.summonProjectiles(caster.getCommandSenderWorld(), blockRayTraceResult.getBlockPos(), caster, stats, resolver);
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand,  SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        this.summonProjectiles(caster.getCommandSenderWorld(), caster, stats, resolver);
        resolver.expendMana(caster);
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack itemStack, LivingEntity livingEntity, Level level, SpellStats spellStats, SpellResolver spellResolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(UseOnContext useOnContext, SpellStats spellStats, SpellResolver spellResolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockHitResult, LivingEntity livingEntity, SpellStats spellStats, SpellResolver spellResolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack itemStack, LivingEntity livingEntity, Entity entity, InteractionHand interactionHand, SpellStats spellStats, SpellResolver spellResolver) {
        return false;
    }


    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentSensitive.INSTANCE, AugmentDurationDown.INSTANCE, AugmentExtendTime.INSTANCE});
    }

    public String getBookDescription() {
        return "A spell you start with. Summons a projectile that applies spell effects when this projectile hits a target or block. Sensitive will allow Projectiles to break plants or other materials that do not block motion.";
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.ELEMENTAL_AIR, SpellSchools.ELEMENTAL_FIRE);
    }
}


