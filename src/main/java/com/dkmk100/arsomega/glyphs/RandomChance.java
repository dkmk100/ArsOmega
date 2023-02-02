package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class RandomChance extends AbstractEffect {
    public float cancelChance;

    public static RandomChance LOW_CHANCE = new RandomChance("random_25","Low Chance",0.75f);
    public static RandomChance MID_CHANCE = new RandomChance("random_50","Mid Chance",0.5f);
    public static RandomChance HIGH_CHANCE = new RandomChance("random_75","High Chance",0.25f);
    public RandomChance(String tag, String description, float cancelChance) {
        super(tag, description);
        this.cancelChance = cancelChance;
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(world.random.nextFloat() < cancelChance){
            spellContext.setCanceled(true);
        }
        else {
            super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf();
    }
}
