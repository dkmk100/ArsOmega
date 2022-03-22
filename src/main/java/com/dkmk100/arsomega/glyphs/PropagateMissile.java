package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityMissileSpell;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PropagateMissile extends AbstractEffect {
    public static PropagateMissile INSTANCE = new PropagateMissile("propagate_missile","Propagate Missile");

    private PropagateMissile(String tag, String description) {
        super(tag,description);
    }

    public void summonProjectiles(World world, Vector3d pos, LivingEntity shooter, List<AbstractAugment> augments, SpellResolver resolver) {
        final boolean activate = true;
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList();
        int duration = 50 + (10*getBuffCount(augments, AugmentExtendTime.class)) - (15*getBuffCount(augments, AugmentDurationDown.class));
        duration = Math.max(7,duration);
        EntityMissileSpell projectileSpell = new EntityMissileSpell(world, resolver, duration, activate,shooter);
        projectileSpell.setPos((double)pos.x, (double)(pos.y + 1), (double)pos.z);
        projectiles.add(projectileSpell);
        int numSplits = getBuffCount(augments, AugmentSplit.class);

        for(int i = 1; i < numSplits + 1; ++i) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) {
                offset = offset.getOpposite();
            }

            BlockPos projPos = new BlockPos(pos.x,pos.y,pos.z).relative(offset, i);
            projPos = projPos.offset(0.0D, 1.5D, 0.0D);
            EntityMissileSpell spell = new EntityMissileSpell(world, resolver, duration, activate,shooter);
            spell.setPos((double)projPos.getX(), (double)projPos.getY(), (double)projPos.getZ());
            projectiles.add(spell);
        }

        Iterator var14 = projectiles.iterator();

        while(var14.hasNext()) {
            EntityMissileSpell proj = (EntityMissileSpell)var14.next();
            Vector3d shooterPos = shooter.position();
            Vector3d currentPos = new Vector3d(pos.x,pos.y,pos.z);
            Vector3d direction = currentPos.subtract(shooterPos);
            if(direction.distanceTo(Vector3d.ZERO)<0.25f){
                proj.shoot(shooter, shooter.xRot, shooter.yRot, 0.0F, 1.0f, 0.8F);
            }
            else {
                proj.shoot(direction.x, direction.y, direction.z, 1.0f, 0.8F);
            }
            world.addFreshEntity(proj);
        }
    }

    public void sendPacket(World world, RayTraceResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats stats) {
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
            Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
            SpellContext newContext = (new SpellContext(newSpell, shooter)).withColors(spellContext.colors);
            SpellResolver resolver = new EntitySpellResolver(newContext);
            //List<AbstractAugment> newAugments = new ArrayList<AbstractAugment>();
            List<AbstractAugment> newAugments = stats.getAugments();
            summonProjectiles(world, rayTraceResult.getLocation(),shooter,newAugments, resolver);
        }
    }

    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext,spellStats);
    }

    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        this.sendPacket(world, rayTraceResult, shooter, spellContext,spellStats);
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 2, 2, 2);
    }

    public int getManaCost() {
        return 300;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentSensitive.INSTANCE, AugmentDurationDown.INSTANCE, AugmentExtendTime.INSTANCE});
    }

    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }



    public Tier getTier() {
        return Tier.TWO;
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
