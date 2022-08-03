package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.mixin.TierFour;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Flatten extends AbstractEffect {
    public static Flatten INSTANCE = new Flatten("flatten", "Flatten");

    private Flatten(String tag, String description) {
        super(tag,description);
    }

    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        try {
            BlockPos pos = new BlockPos(rayTraceResult.getEntity().position()).below();
            Block block = world.getBlockState(pos).getBlock();
            Field field = ReflectionHandler.blockProperties;
            AbstractBlock.Properties properties = ((AbstractBlock.Properties) field.get(block));
            Field field2 = ReflectionHandler.destroyTime;
            float tier = field2.getFloat(properties);
            tier = (float) Math.sqrt(tier) * 4f;

            //deal with bedrock
            if(tier < 0){
                tier = 50;
            }

            //nerf obsidian:
            if(tier>10){
                tier = 10 + ((tier-10)/4f);
            }

            float damage = (float) (0.165f * tier * (spellStats.getAmpMultiplier()+3+(properties.getHarvestLevel()*1.5f)));

            //buff dirt and similar:
            damage = Math.max(damage,0.5f);

            this.dealDamage(world, shooter, damage, spellStats, rayTraceResult.getEntity(), DamageSource.FALL);
        }
        catch (Exception e){
            e.printStackTrace();
            shooter.addEffect(new EffectInstance(Effects.POISON,200));
        }
    }

    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addDamageConfig(builder, 3.0D);
        this.addAmpConfig(builder, 1.0D);
    }

    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentFortune.INSTANCE});
    }

    public String getBookDescription() {
        return "Flattens the target against the block below them, doing damage based on the hardness";
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_EARTH});
    }
}
