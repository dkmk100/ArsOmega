package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class FormProjectileBurst extends AbstractCastMethod {
    public static FormProjectileBurst INSTANCE = new FormProjectileBurst();

    private FormProjectileBurst() {
        super("projectile_burst", "Projectile Burst");
    }

    public int getDefaultManaCost() {
        return 300;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    public void summonProjectiles(Level world, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver);
        projectiles.add(projectileSpell);
        int numProj = (int) Math.min(Math.pow(2, stats.getBuffCount(AugmentSplit.INSTANCE) + 1),16);
        for(int i = 1; i < numProj; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            int i4 = i % 4;
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            if(i4 % 2 == 1){
                if(i4 <= 1){
                    offset = Direction.DOWN;
                }
                else{
                    offset = Direction.UP;
                }
            }

            BlockPos projPos = BlockPos.ZERO.relative(offset, i);
            projPos = projPos.offset(0.0, 1, 0.0);
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
            Vec3 pos = shooter.position();
            spell.setPos(pos.x + projPos.getX() * 0.15,pos.y + projPos.getY() * 0.15,pos.z + projPos.getZ() * 0.15);
            projectiles.add(spell);
        }

        float velocity = 0.2F + 0.1f*(float)stats.getBuffCount(AugmentAccelerate.INSTANCE);
        Iterator var14 = projectiles.iterator();

        int i2 = 0;
        while(var14.hasNext()) {
            EntityProjectileSpell proj = (EntityProjectileSpell)var14.next();
            try {
                float offset = 0.003f;
                if(i2%2==0){
                    offset = offset * -1;
                }
                proj.shoot(shooter, ReflectionHandler.xRot.getFloat(shooter), ReflectionHandler.yRot.getFloat(shooter) + offset, 0.0F, velocity, 2.5F);
                i2 +=1;
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
            world.addFreshEntity(proj);
        }

    }

    public void summonProjectiles(Level world, BlockPos pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver);
        projectileSpell.setPos((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ());
        projectiles.add(projectileSpell);
        int numProj = (int) Math.min(Math.pow(2, stats.getBuffCount(AugmentSplit.INSTANCE) + 1),16);

        for(int i = 1; i < numProj; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            BlockPos projPos = pos.relative(offset, i);
            projPos = projPos.offset(0.0, 1.5, 0.0);
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
            spell.setPos((double)projPos.getX(), (double)projPos.getY(), (double)projPos.getZ());
            projectiles.add(spell);
        }

        Iterator var14 = projectiles.iterator();

        while(var14.hasNext()) {
            EntityProjectileSpell proj = (EntityProjectileSpell)var14.next();
            proj.setDeltaMovement(new Vec3(0.0, -0.1, 0.0));
            world.addFreshEntity(proj);
        }

    }

    public void onCast(ItemStack stack, LivingEntity shooter, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        this.summonProjectiles(world, shooter, spellStats, resolver);
        resolver.expendMana(shooter);
    }

    public void onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        Player shooter = context.getPlayer();
        this.summonProjectiles(world, shooter, spellStats, resolver);
        resolver.expendMana(shooter);
    }

    public void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        caster.lookAt(EntityAnchorArgument.Anchor.EYES, blockRayTraceResult.getLocation().add(0.0, 0.0, 0.0));
        this.summonProjectiles(caster.getCommandSenderWorld(), blockRayTraceResult.getBlockPos(), caster, spellStats, resolver);
        resolver.expendMana(caster);
    }

    public void onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        this.summonProjectiles(caster.getCommandSenderWorld(), caster, spellStats, resolver);
        resolver.expendMana(caster);
    }

    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    public boolean wouldCastOnBlockSuccessfully(UseOnContext context, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentSensitive.INSTANCE});
    }

    public String getBookDescription() {
        return "A spell you start with. Summons a projectile that applies spell effects when this projectile hits a target or block. Sensitive will allow Projectiles to break plants or other materials that do not block motion.";
    }
}
