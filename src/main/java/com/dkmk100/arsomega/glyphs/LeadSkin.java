package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class LeadSkin extends AbstractEffect implements ILimitedPotion {

    public static LeadSkin INSTANCE = new LeadSkin("lead_skin", "Lead Skin");

    public LeadSkin(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        int focusLevel = 0;
        if(shooter!=null) {

            if (CuriosApi.getCuriosHelper().findFirstCurio(shooter, RegistryHandler.FOCUS_OF_ADVANCED_ALCHEMY.get()).isPresent()) {
                focusLevel = 2;
            } else if (CuriosApi.getCuriosHelper().findFirstCurio(shooter, RegistryHandler.FOCUS_OF_ALCHEMY.get()).isPresent()) {
                focusLevel = 1;
            }
        }

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            this.applyLimitedEffect(living, ModPotions.LEAD_SKIN.get(), spellStats, 2 + focusLevel);
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

    @Override
    public int getBaseDuration() {
        return 30;
    }

    @Override
    public int getExtendTimeDuration() {
        return 15;
    }
}
