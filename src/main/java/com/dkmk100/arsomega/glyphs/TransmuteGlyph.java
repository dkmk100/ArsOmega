package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.crafting.TransmuteRecipe;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TransmuteGlyph extends AbstractEffect {
    public static TransmuteGlyph INSTANCE = new TransmuteGlyph("transmute", "Transmute");

    public TransmuteGlyph(String tag, String description) {
        super(tag, description);
    }

    public static int focusMax = 2;
    public static int advancedFocusMax = 4;
    public static int normalMax = 1;

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerLevel) {
            List<TransmuteRecipe> recipes = world.getRecipeManager().getAllRecipesFor(RegistryHandler.TRANSMUTE_TYPE);
            double aoeBuff = spellStats.getAoeMultiplier();
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());
            int maxProcess = spellStats.getBuffCount(AugmentPierce.INSTANCE) * 16 + 8;

            int maxLevel = normalMax;
            if (CuriosApi.getCuriosHelper().findFirstCurio(shooter, RegistryHandler.FOCUS_OF_ADVANCED_ALCHEMY.get()).isPresent()) {
                maxLevel = advancedFocusMax;
            } else if (CuriosApi.getCuriosHelper().findFirstCurio(shooter,RegistryHandler.FOCUS_OF_ALCHEMY.get()).isPresent()) {
                maxLevel = focusMax;
            }
            ampBuff = Math.min(ampBuff,maxLevel);


            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, (new AABB(rayTraceResult.getBlockPos())).inflate((double) aoeBuff + 1.0D));
            Iterator var5 = itemEntities.iterator();

            while (var5.hasNext()) {
                ItemEntity itemEntity = (ItemEntity) var5.next();
                ItemStack current = itemEntity.getItem();
                for (TransmuteRecipe recipe : recipes) {
                    if (ampBuff >= recipe.minAmp) {
                        if (current.getItem() == recipe.input.getItem()) {
                            ItemStack result = new ItemStack(recipe.output.getItem(),0);
                            int i = Math.min(result.getMaxStackSize(),Math.min(current.getCount(),maxProcess));
                            current.shrink(i);
                            result.grow(i);
                            world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));

                            break;
                        } else if (recipe.reversible && current.getItem() == recipe.output.getItem()) {
                            ItemStack result = new ItemStack(recipe.input.getItem(),0);
                            int i = Math.min(result.getMaxStackSize(),Math.min(current.getCount(),maxProcess));
                            current.shrink(i);
                            result.grow(i);
                            world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));
                            break;
                        }
                    }
                }

            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 160;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE,AugmentPierce.INSTANCE,AugmentAOE.INSTANCE});
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION,Schools.ALCHEMY});
    }
}
