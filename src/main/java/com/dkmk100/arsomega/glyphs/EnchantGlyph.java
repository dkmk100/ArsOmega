package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.crafting.EnchantRecipe;
import com.dkmk100.arsomega.crafting.TransmuteRecipe;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EnchantGlyph extends AbstractEffect {
    public static EnchantGlyph INSTANCE = new EnchantGlyph("enchant", "Enchant");

    public EnchantGlyph(String tag, String description) {
        super(tag, description);
    }

    static RandomSource random = RandomSource.create();

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel) {
            double aoeBuff = spellStats.getAoeMultiplier();
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());

            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, (new AABB(rayTraceResult.getBlockPos())).inflate((double) aoeBuff + 1.0D));
            Iterator var5 = itemEntities.iterator();

            while (var5.hasNext()) {
                ItemEntity itemEntity = (ItemEntity) var5.next();
                ItemStack current = itemEntity.getItem();
                //random = world.random;
                int seed = random.nextInt();
                if(shooter instanceof Player){
                    seed+=((Player)shooter).getEnchantmentSeed();
                }
                ItemStack result = enchantItem(shooter,seed,current,level(ampBuff));
                if(result!=null) {
                    world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));
                    current.setCount(0);
                }
            }
        }
    }

    int level(int amp){return 2*amp + 4;}
    public int getAmp(int level){
        return (level-4)/2;
    }
    int power(int level){
        return Math.min(Math.max(level,0),30);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(rayTraceResult.getEntity() instanceof Player) {
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());
            Player entity = (Player)rayTraceResult.getEntity();
            ItemStack result = enchantItem(entity, entity.getEnchantmentSeed(), entity.getItemInHand(InteractionHand.MAIN_HAND),level(ampBuff));
            if(result!=null) {
                entity.setItemInHand(InteractionHand.MAIN_HAND, result);
            }
            else{
                result = enchantItem(entity, entity.getEnchantmentSeed(), entity.getItemInHand(InteractionHand.OFF_HAND),level(ampBuff));
                if(result!=null) {
                    entity.setItemInHand(InteractionHand.OFF_HAND, result);

                }
            }
        }
    }

    @Nullable
    public ItemStack enchantItem(LivingEntity player, int seed, ItemStack itemstack, int level) {
        int power = power(level);
        int i = seed + 1;
        if (itemstack.isEmpty() || itemstack.isEnchanted()) {
            return null;
        } else {
            ItemStack itemstack2 = itemstack.copy();
            List<EnchantmentInstance> list = this.getEnchantmentList(itemstack, seed, power);
            if (!list.isEmpty()) {
                boolean flag = itemstack.getItem() == Items.BOOK;
                if (flag) {
                    itemstack2 = new ItemStack(Items.ENCHANTED_BOOK,itemstack.getCount());
                    CompoundTag compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }
                }

                for(int j = 0; j < list.size(); ++j) {
                    EnchantmentInstance enchantmentdata = list.get(j);
                    if (flag) {
                        EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
                    } else {
                        itemstack2.enchant(enchantmentdata.enchantment, enchantmentdata.level);
                    }
                }

                if(player instanceof Player) {
                    ((Player) player).awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, itemstack2, i);
                    }
                }
            }
            else{
                List<EnchantRecipe> recipes = player.getLevel().getRecipeManager().getAllRecipesFor(RegistryHandler.ENCHANT_TYPE);

                for(EnchantRecipe recipe : recipes){
                    if(itemstack.getItem()==recipe.input.getItem() && level >= recipe.minLevel){
                        itemstack2 = new ItemStack(recipe.output.getItem(),itemstack.getCount());
                        CompoundTag compoundnbt = itemstack.getTag();
                        if (compoundnbt != null) {
                            itemstack2.setTag(compoundnbt.copy());
                        }
                    }
                }
            }
            return itemstack2;
        }
    }

    private List<EnchantmentInstance> getEnchantmentList(ItemStack p_178148_1_, long p_178148_2_, int p_178148_3_) {
        random.setSeed(p_178148_2_);
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, p_178148_1_, p_178148_3_, false);
        //if (p_178148_1_.getItem() == Items.BOOK && list.size() > 1) {
            //list.remove(this.random.nextInt(list.size()));
        //}

        return list;
    }

    @Override
    public int getDefaultManaCost() {
        return 660;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE});
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ABJURATION,SpellSchools.MANIPULATION});
    }
}
