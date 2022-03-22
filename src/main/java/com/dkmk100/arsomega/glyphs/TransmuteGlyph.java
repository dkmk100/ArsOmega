package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.base_blocks.MagicAnimatable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TransmuteGlyph extends AbstractEffect {
    public static TransmuteGlyph INSTANCE = new TransmuteGlyph("transmute", "Transmute");

    public TransmuteGlyph(String tag, String description) {
        super(tag, description);
    }

    public static TransmuteRecipe[] recipes = new TransmuteRecipe[]
            {
                    new TransmuteRecipe(Items.IRON_INGOT, Items.GOLD_INGOT),
                    new TransmuteRecipe(Items.BLAZE_POWDER, Items.NETHER_WART, 1),
                    new TransmuteRecipe(Items.ENDER_PEARL, Items.BLAZE_ROD, 2),
                    new TransmuteRecipe(Items.IRON_BLOCK, Items.GOLD_BLOCK,2),
                    new TransmuteRecipe(Items.CHORUS_FLOWER, Items.WITHER_SKELETON_SKULL, 3),
                    new TransmuteRecipe(Items.DIAMOND, Items.EMERALD_BLOCK, 3)
            };

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerWorld) {
            int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());
            int maxProcess = spellStats.getBuffCount(AugmentPierce.INSTANCE) * 16 + 8;

            int maxLevel = 1;
            if(CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS_ADVANCED,shooter).isPresent()){
                maxLevel+=3;
            }
            else if(CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS,shooter).isPresent()){
                maxLevel+=1;
            }
            ampBuff = Math.min(ampBuff,maxLevel);


            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, (new AxisAlignedBB(rayTraceResult.getBlockPos())).inflate((double) aoeBuff + 1.0D));
            Iterator var5 = itemEntities.iterator();

            while (var5.hasNext()) {
                ItemEntity itemEntity = (ItemEntity) var5.next();
                ItemStack current = itemEntity.getItem();
                for (TransmuteRecipe recipe : recipes) {
                    if (ampBuff >= recipe.minAmp) {
                        if (current.getItem() == recipe.item1) {
                            ItemStack result = new ItemStack(recipe.item2,0);
                            int i = Math.min(result.getMaxStackSize(),Math.min(current.getCount(),maxProcess));
                            current.shrink(i);
                            result.grow(i);
                            world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));

                            break;
                        } else if (recipe.reversible && current.getItem() == recipe.item2) {
                            ItemStack result = new ItemStack(recipe.item1,0);
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

    static class TransmuteRecipe {
        Item item1;
        Item item2;
        boolean reversible;
        int minAmp;

        TransmuteRecipe(Item i1, Item i2) {
            item1 = i1;
            item2 = i2;
            reversible = true;
            minAmp = 0;
        }

        TransmuteRecipe(Item i1, Item i2, int minAmp) {
            item1 = i1;
            item2 = i2;
            reversible = true;
            this.minAmp = minAmp;
        }
    }

    @Override
    public int getManaCost() {
        return 160;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE,AugmentPierce.INSTANCE,AugmentAOE.INSTANCE});
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.ALCHEMY});
    }
}
