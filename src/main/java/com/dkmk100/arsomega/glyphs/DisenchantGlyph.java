package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DisenchantGlyph extends AbstractEffect implements ConfigurableGlyph {
    public static DisenchantGlyph INSTANCE = new DisenchantGlyph("disenchant", "Disenchant");

    ForgeConfigSpec.BooleanValue affectPlayers;

    public DisenchantGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world instanceof ServerLevel) {
            double aoeBuff = spellStats.getAoeMultiplier();
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());

            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, (new AABB(rayTraceResult.getBlockPos())).inflate((double) aoeBuff + 1.0D));
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
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(rayTraceResult.getEntity() instanceof Player && affectPlayers.get()) {
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());
            Player entity = (Player)rayTraceResult.getEntity();
            ItemStack result = disenchantItem(entity, entity.getItemInHand(InteractionHand.MAIN_HAND), spellStats);
            if(result!=null) {
                entity.setItemInHand(InteractionHand.MAIN_HAND, result);
            }
            else{
                result = disenchantItem(entity, entity.getItemInHand(InteractionHand.OFF_HAND), spellStats);
                if(result!=null) {
                    entity.setItemInHand(InteractionHand.OFF_HAND, result);
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
                ListTag keys = itemstack2.getEnchantmentTags();
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
                    CompoundTag compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }

                } else if (amps > 2 && itemstack2.getItem() == Items.EXPERIENCE_BOTTLE) {
                    itemstack2 = new ItemStack(Items.HONEY_BOTTLE, itemstack2.getCount());
                    CompoundTag compoundnbt = itemstack.getTag();
                    if (compoundnbt != null) {
                        itemstack2.setTag(compoundnbt.copy());
                    }

                }
            }
            return itemstack2;
        }
    }


    @Override
    public int getDefaultManaCost() {
        return 420;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentSensitive.INSTANCE, AugmentDampen.INSTANCE, AugmentAmplify.INSTANCE});
    }
    @Override
    public void buildExtraConfig(ForgeConfigSpec.Builder builder) {
        this.affectPlayers = builder.comment("Allows the glyph to hit players, and disenchant held items. Cool but kinda grief-y feature.").define("affect_players",false);
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
