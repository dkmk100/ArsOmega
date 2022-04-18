package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class LeadSkin extends AbstractEffect {

    public static LeadSkin INSTANCE = new LeadSkin("lead_skin", "Lead Skin");

    public LeadSkin(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        int focusLevel = 0;
        if(CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS_ADVANCED,shooter).isPresent()){
            focusLevel=2;
        }
        else if(CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS,shooter).isPresent()){
            focusLevel=1;
        }

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            this.applyPotionWithCap(living, ModPotions.LEAD_SKIN, spellStats, 30,15,1 + focusLevel);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 800;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "A powerful harming effect that is almost impossible to cure";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.getPotionAugments();
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.ALCHEMY});
    }
}
