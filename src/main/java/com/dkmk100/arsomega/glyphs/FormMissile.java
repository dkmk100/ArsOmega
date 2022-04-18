/*
package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityMissileSpell;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class FormMissile extends AbstractCastMethod {
    public static FormMissile INSTANCE = new FormMissile("missile","Missile");

    private FormMissile(String tag, String description) {
        super(tag,description);
    }

    public int getManaCost() {
        return 10;
    }

    public void summonProjectiles(Level world, LivingEntity shooter, List<AbstractAugment> augments, SpellResolver resolver) {
        final boolean activate = true;
        int duration = 50 + (10*getBuffCount(augments, AugmentExtendTime.class)) - (15*getBuffCount(augments, AugmentDurationDown.class));
        duration = Math.max(7,duration);
        ArrayList<EntityMissileSpell> projectiles = new ArrayList();
        EntityMissileSpell projectileSpell = new EntityMissileSpell(world, resolver,duration,activate,shooter);
        projectiles.add(projectileSpell);
        int numSplits = getBuffCount(augments, AugmentSplit.class);

        for(int i = 1; i < numSplits + 1; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            BlockPos projPos = shooter.blockPosition().relative(offset, i);
            projPos = projPos.offset(0.0D, 1.5D, 0.0D);
            EntityMissileSpell spell = new EntityMissileSpell(world, resolver,duration,activate,shooter);
            spell.setPos((double)projPos.getX(), (double)projPos.getY(), (double)projPos.getZ());
            projectiles.add(spell);
        }

        float velocity = 1.0F + (float)getBuffCount(augments, AugmentAccelerate.class);
        Iterator var14 = projectiles.iterator();

        while(var14.hasNext()) {
            EntityMissileSpell proj = (EntityMissileSpell)var14.next();
            proj.shoot(shooter, shooter.xRot, shooter.yRot, 0.0F, velocity, 0.8F);
            world.addFreshEntity(proj);
        }

    }

    public void summonProjectiles(Level world, BlockPos pos, LivingEntity shooter, List<AbstractAugment> augments, SpellResolver resolver) {
        final boolean activate = true;
        int duration = 50 + (10*getBuffCount(augments, AugmentExtendTime.class)) - (10*getBuffCount(augments, AugmentDurationDown.class));
        duration = Math.max(7,duration);
        ArrayList<EntityMissileSpell> projectiles = new ArrayList();
        EntityMissileSpell projectileSpell = new EntityMissileSpell(world, resolver,duration,activate,shooter);
        projectileSpell.setPos((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ());
        projectiles.add(projectileSpell);
        int numSplits = getBuffCount(augments, AugmentSplit.class);

        for(int i = 1; i < numSplits + 1; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            BlockPos projPos = pos.relative(offset, i);
            projPos = projPos.offset(0.0D, 1.5D, 0.0D);
            EntityMissileSpell spell = new EntityMissileSpell(world, resolver,duration,activate,shooter);
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

    public void onCast(ItemStack stack, LivingEntity shooter, Level world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver) {
        this.summonProjectiles(world, shooter, augments, resolver);
        resolver.expendMana(shooter);
    }

    public void onCastOnBlock(UseOnContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        Player shooter = context.getPlayer();
        this.summonProjectiles(world, shooter, augments, resolver);
        resolver.expendMana(shooter);
    }

    public void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        caster.lookAt(EntityAnchorArgument.Anchor.EYES, blockRayTraceResult.getLocation().add(0.0D, 0.0D, 0.0D));
        this.summonProjectiles(caster.getCommandSenderWorld(), blockRayTraceResult.getBlockPos(), caster, augments, resolver);
        resolver.expendMana(caster);
    }

    public void onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        this.summonProjectiles(caster.getCommandSenderWorld(), caster, augments, resolver);
        resolver.expendMana(caster);
    }

    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    public boolean wouldCastOnBlockSuccessfully(UseOnContext context, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentSensitive.INSTANCE, AugmentDurationDown.INSTANCE, AugmentExtendTime.INSTANCE});
    }

    public String getBookDescription() {
        return "A spell you start with. Summons a projectile that applies spell effects when this projectile hits a target or block. Sensitive will allow Projectiles to break plants or other materials that do not block motion.";
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    public Item getCraftingReagent() {
        return Items.BOW;
    }

    public boolean defaultedStarterGlyph() {
        return true;
    }
}

 */
