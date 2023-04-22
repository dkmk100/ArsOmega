package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityMissileSpell;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class PropagateMissile extends AbstractEffect {
    public static PropagateMissile INSTANCE = new PropagateMissile("propagate_missile","Propagate Missile");

    private PropagateMissile(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag),description);
    }

    public void summonProjectiles(Level world, Vec3 pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        final boolean activate = true;
        int duration = 30 + (int)Math.round(7f * stats.getDurationMultiplier());
        duration = Math.max(5,duration);
        ArrayList<EntityMissileSpell> projectiles = new ArrayList();
        EntityMissileSpell projectileSpell = new EntityMissileSpell(world, resolver,duration,activate, (float)stats.getAoeMultiplier(),shooter);
        projectileSpell.setPos(pos.x,pos.y + 1,pos.z);
        projectiles.add(projectileSpell);
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        for(int i = 1; i < numSplits + 1; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            BlockPos projPos = new BlockPos(pos.x,pos.y + 1,pos.z).relative(offset, i);
            projPos = projPos.offset(0.0D, 1.5D, 0.0D);
            EntityMissileSpell spell = new EntityMissileSpell(world, resolver,duration,activate,(float)stats.getAoeMultiplier(),shooter);
            spell.setPos((double)projPos.getX(), (double)projPos.getY(), (double)projPos.getZ());
            projectiles.add(spell);
        }

        float velocity = 1.0F + (float)stats.getBuffCount(AugmentAccelerate.INSTANCE);
        Iterator var14 = projectiles.iterator();

        while(var14.hasNext()) {
            EntityMissileSpell proj = (EntityMissileSpell)var14.next();
            Vec3 shooterPos = shooter.position();
            Vec3 currentPos = new Vec3(pos.x,pos.y,pos.z);
            Vec3 direction = currentPos.subtract(shooterPos);
            if(direction.distanceTo(Vec3.ZERO)<0.25f){
                proj.shoot(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, 0.8F);
            }
            else {
                proj.shoot(direction.x, direction.y, direction.z, velocity, 0.8F);
            }
            world.addFreshEntity(proj);
        }
    }

    public void sendPacket(Level world, HitResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats stats) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = spellContext.clone().withSpell(newSpell);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            summonProjectiles(world, rayTraceResult.getLocation(),shooter, stats, resolver);
        }
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext,spellStats);
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext,spellStats);
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 2, 2, 2);
    }

    public int getDefaultManaCost() {
        return 300;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return FormMissile.INSTANCE.getCompatibleAugments();
    }

    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nullable
    public Item getCraftingReagent() {
        return Items.REPEATER;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }
}
