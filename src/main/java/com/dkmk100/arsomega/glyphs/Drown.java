package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class Drown extends TierFourEffect {

    public static Drown INSTANCE = new Drown("drown", "drown");

    public Drown(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double amp = spellStats.getAmpMultiplier() + 2;
        BlockPos pos = rayTraceResult.getEntity().blockPosition();
        int depth = 0;
        int i=0;
        while(i<15){
            pos = pos.above();
            if(world.getBlockState(pos).getBlock() == Blocks.WATER){
                depth+=1;
            }
            else{
                break;
            }
        }


        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity living = (LivingEntity)rayTraceResult.getEntity();
            if(!living.hasEffect(Effects.WATER_BREATHING)){
                this.dealDamage(world,shooter,(float)amp*0.5f*depth,spellStats,living,DamageSource.DROWN);
            }
        }
    }

    @Override
    public int getManaCost() {
        return 700;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_WATER});
    }
}
