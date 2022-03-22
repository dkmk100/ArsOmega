package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;

public class Scald extends AbstractEffect {

    public static DamageSource SCALD(Entity entity,@Nullable LivingEntity source) {
        DamageSource SCALD = new IndirectEntityDamageSource("scald",entity,source).setIsFire();
        return SCALD;
    }

    public static Scald INSTANCE = new Scald("scald", "scald");

    public Scald(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double amp = spellStats.getAmpMultiplier() + 2;
        int time = spellStats.getBuffCount(AugmentExtendTime.INSTANCE);

        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity living = (LivingEntity)rayTraceResult.getEntity();
            living.addEffect(new EffectInstance(ModPotions.BURNED,60 + 30*time));
        }
        rayTraceResult.getEntity().hurt(SCALD(shooter,shooter),(float)amp*1.5f);
    }

    @Override
    public int getManaCost() {
        return 200;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE,SpellSchools.ELEMENTAL_WATER});
    }
}

