package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.CuriosApi;

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

    static Random random = new Random();

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerWorld) {
            int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());

            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, (new AxisAlignedBB(rayTraceResult.getBlockPos())).inflate((double) aoeBuff + 1.0D));
            Iterator var5 = itemEntities.iterator();

            while (var5.hasNext()) {
                ItemEntity itemEntity = (ItemEntity) var5.next();
                ItemStack current = itemEntity.getItem();
                //random = world.random;
                int seed = random.nextInt();
                if(shooter instanceof PlayerEntity){
                    seed+=((PlayerEntity)shooter).getEnchantmentSeed();
                }
                ItemStack result = enchantItem(shooter,seed,current,power(ampBuff));
                if(result!=null) {
                    world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));
                    current.setCount(0);
                }
            }
        }
    }

    int power(int amp){
        return Math.min(2 * amp + 5,30);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(rayTraceResult.getEntity() instanceof PlayerEntity) {
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());
            PlayerEntity entity = (PlayerEntity)rayTraceResult.getEntity();
            ItemStack result = enchantItem(entity, entity.getEnchantmentSeed(), entity.getItemInHand(Hand.MAIN_HAND),power(ampBuff));
            if(result!=null) {
                entity.setItemInHand(Hand.MAIN_HAND, result);
            }
            else{
                result = enchantItem(entity, entity.getEnchantmentSeed(), entity.getItemInHand(Hand.OFF_HAND),power(ampBuff));
                if(result!=null) {
                    entity.setItemInHand(Hand.OFF_HAND, result);

                }
            }
        }
    }

    @Nullable
    public ItemStack enchantItem(LivingEntity player, int seed, ItemStack itemstack, int level) {
        int i = seed + 1;
        if (itemstack.isEmpty() || itemstack.isEnchanted()) {
            return null;
        } else {
            ItemStack itemstack2 = itemstack.copy();
            List<EnchantmentData> list = this.getEnchantmentList(itemstack, seed, level);
            if (!list.isEmpty()) {
                boolean flag = itemstack.getItem() == Items.BOOK;
                if (flag) {
                    itemstack2 = new ItemStack(Items.ENCHANTED_BOOK,itemstack.getCount());
                    CompoundNBT compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }
                }

                for(int j = 0; j < list.size(); ++j) {
                    EnchantmentData enchantmentdata = list.get(j);
                    if (flag) {
                        EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
                    } else {
                        itemstack2.enchant(enchantmentdata.enchantment, enchantmentdata.level);
                    }
                }

                if(player instanceof PlayerEntity) {
                    ((PlayerEntity) player).awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity) player, itemstack2, i);
                    }
                }
            }
            else{
                if(itemstack.getItem()==Items.GOLDEN_APPLE && level>=20){
                    itemstack2 = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE,itemstack.getCount());
                    CompoundNBT compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }
                }
                else if(itemstack.getItem()==Items.HONEY_BOTTLE && level>=20){
                    itemstack2 = new ItemStack(Items.EXPERIENCE_BOTTLE,itemstack.getCount());
                    CompoundNBT compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }
                }
            }
            return itemstack2;
        }
    }

    private List<EnchantmentData> getEnchantmentList(ItemStack p_178148_1_, long p_178148_2_, int p_178148_3_) {
        this.random.setSeed(p_178148_2_);
        List<EnchantmentData> list = EnchantmentHelper.selectEnchantment(this.random, p_178148_1_, p_178148_3_, false);
        //if (p_178148_1_.getItem() == Items.BOOK && list.size() > 1) {
            //list.remove(this.random.nextInt(list.size()));
        //}

        return list;
    }

    @Override
    public int getManaCost() {
        return 660;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE});
    }

    @Override
    public ISpellTier.Tier getTier() {
        return ISpellTier.Tier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ABJURATION});
    }
}
