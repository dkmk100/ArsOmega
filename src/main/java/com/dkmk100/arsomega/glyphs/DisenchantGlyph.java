package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DisenchantGlyph extends AbstractEffect {
    public static DisenchantGlyph INSTANCE = new DisenchantGlyph("disenchant", "Disenchant");

    public DisenchantGlyph(String tag, String description) {
        super(tag, description);
    }

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
                ItemStack result = disenchantItem(shooter, current, spellStats);
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
            ItemStack result = disenchantItem(entity, entity.getItemInHand(Hand.MAIN_HAND), spellStats);
            if(result!=null) {
                entity.setItemInHand(Hand.MAIN_HAND, result);
            }
            else{
                result = disenchantItem(entity, entity.getItemInHand(Hand.OFF_HAND), spellStats);
                if(result!=null) {
                    entity.setItemInHand(Hand.OFF_HAND, result);
                }
            }
        }
    }

    @Nullable
    public ItemStack disenchantItem(LivingEntity player, ItemStack itemstack, SpellStats stats) {
        return disenchantItem(player,itemstack,stats.hasBuff(AugmentSensitive.INSTANCE),
                stats.getBuffCount(AugmentDampen.INSTANCE),(int)Math.round(stats.getAmpMultiplier()));
    }
    @Nullable
    public ItemStack disenchantItem(LivingEntity player, ItemStack itemstack, boolean sensitive, int dampens, int amps) {
        if (itemstack.isEmpty()) {
            return null;
        }
        else {
            ItemStack itemstack2 = itemstack.copy();
            if(itemstack.isEnchanted()) {
                int maxRemove = dampens > 0 ? 0 : 1 + amps;//dampen will prevent all removal.
                int removed = 0;
                ListNBT keys = itemstack2.getEnchantmentTags();
                Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(keys);
                Map<Enchantment, Integer> map2 = new HashMap<Enchantment, Integer>();
                for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
                    if ((sensitive && !entry.getKey().isCurse()) || removed >= maxRemove) {
                        map2.put(entry.getKey(), entry.getValue());
                    } else {
                        removed += 1;
                    }
                }
                EnchantmentHelper.setEnchantments(map2, itemstack2);
            }
            else {
                if (amps > 6 && itemstack2.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                    itemstack2 = new ItemStack(Items.GOLDEN_APPLE, itemstack2.getCount());
                    CompoundNBT compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }

                } else if (amps > 2 && itemstack2.getItem() == Items.EXPERIENCE_BOTTLE) {
                    itemstack2 = new ItemStack(Items.HONEY_BOTTLE, itemstack2.getCount());
                    CompoundNBT compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }

                }
            }
            return itemstack2;
        }
    }


    @Override
    public int getManaCost() {
        return 320;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentSensitive.INSTANCE, AugmentDampen.INSTANCE, AugmentAmplify.INSTANCE});
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
