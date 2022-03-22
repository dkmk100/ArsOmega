package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientDelayEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDelay;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.entity.Entity;
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

public class PropagateProjectile extends AbstractEffect {
    public static PropagateProjectile INSTANCE = new PropagateProjectile("propagate_projectile","Propagate Projectile");

    private PropagateProjectile(String tag, String description) {
        super(tag,description);
    }

    public void summonProjectiles(World world, Vector3d pos, LivingEntity shooter, List<AbstractAugment> augments, SpellResolver resolver) {
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver);
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
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
            spell.setPos((double)projPos.getX(), (double)projPos.getY(), (double)projPos.getZ());
            projectiles.add(spell);
        }

        Iterator var14 = projectiles.iterator();

        while(var14.hasNext()) {
            EntityProjectileSpell proj = (EntityProjectileSpell)var14.next();
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
        this.PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 3, 3, 3);
    }

    public int getManaCost() {
        return 200;
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentSensitive.INSTANCE});
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
