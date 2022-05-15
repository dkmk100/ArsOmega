package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.entities.EntityTornado;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class TornadoGlyph extends TierFourEffect{

    public static DamageSource TORNADO_DAMAGE = new DamageSource("tornado");

    public static TornadoGlyph INSTANCE = new TornadoGlyph("tornado","Tornado");

    public TornadoGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        EntityTornado tornado = new EntityTornado(world,shooter);
        Vector3d pos = rayTraceResult.getLocation();
        tornado.setColor(spellContext.colors);
        tornado.setPos(pos.x,pos.y + 0.5,pos.z);
        int ticks = 250 + 75 * spellStats.getBuffCount(AugmentExtendTime.INSTANCE) - 50 * spellStats.getBuffCount(AugmentDurationDown.INSTANCE);
        tornado.setDuration(ticks);
        tornado.setAccelerate(spellStats.getBuffCount(AugmentAccelerate.INSTANCE));
        tornado.setAoe(spellStats.getBuffCount(AugmentAOE.INSTANCE));
        world.addFreshEntity(tornado);
    }

    @Override
    public int getManaCost() {
        return 1000;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAOE.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAccelerate.INSTANCE});
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_AIR});
    }
}
