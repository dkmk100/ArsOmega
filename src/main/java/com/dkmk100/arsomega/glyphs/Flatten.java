package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;

public class Flatten extends AbstractEffect {
    public static Flatten INSTANCE = new Flatten("flatten", "Flatten");

    private Flatten(String tag, String description) {
        super(tag,description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        try {
            BlockPos pos = new BlockPos(rayTraceResult.getEntity().getPosition(0)).below();
            Block block = world.getBlockState(pos).getBlock();
            Field field = ReflectionHandler.blockProperties;
            BlockBehaviour.Properties properties = ((BlockBehaviour.Properties) field.get(block));
            Field field2 = ReflectionHandler.destroyTime;
            float tier = field2.getFloat(properties);

            //deal with bedrock
            if(tier < 0){
                tier = 50;
            }

            tier = (float) Math.sqrt(tier) * 4f;

            //nerf obsidian:
            if(tier>10){
                tier = 10 + ((tier-10)/4f);
            }

            float damage = (float) (0.165f * tier * (spellStats.getAmpMultiplier()+3+(getHarvestLevel(block)*1.5f)));

            //buff dirt and similar:
            damage = Math.max(damage,0.5f);

            this.dealDamage(world, shooter, damage, spellStats, rayTraceResult.getEntity(), DamageSource.FALL);
        }
        catch (Exception e){
            e.printStackTrace();
            shooter.addEffect(new MobEffectInstance(MobEffects.POISON,200));
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addDamageConfig(builder, 3.0D);
        this.addAmpConfig(builder, 1.0D);
    }

    int getHarvestLevel(Block block){
        return 1;//temporary, will fix later
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentFortune.INSTANCE});
    }

    @Override
    public String getBookDescription() {
        return "Flattens the target against the block below them, doing damage based on the hardness";
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_EARTH});
    }
}
