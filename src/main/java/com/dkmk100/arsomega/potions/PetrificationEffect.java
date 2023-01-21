package com.dkmk100.arsomega.potions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.damagesource.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class PetrificationEffect extends MobEffect {
    public static final DamageSource PETRIFY = (new DamageSource("petrify")).bypassArmor().bypassInvul().bypassMagic();

    public PetrificationEffect() {
        super(MobEffectCategory.HARMFUL, 9211020);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0dee8a21-f182-42c8-8361-1ad6186cac30", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList();//does not include cleansing gem on purpose, ritual is complete
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap manager, int level) {
        super.removeAttributeModifiers(entity, manager, level);
        if (level >= 1) {
            entity.setHealth(1);
            ArmorStand ent = new ArmorStand(entity.getCommandSenderWorld(), entity.getX(), entity.getY(), entity.getZ());
            entity.getCommandSenderWorld().addFreshEntity(ent);
            ent.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.STONE, 1));
            entity.getCommandSenderWorld().addFreshEntity(new ItemEntity(entity.getCommandSenderWorld(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.STONE, 1)));
            ent.setPose(entity.getPose());
            ent.setYHeadRot(entity.getYHeadRot());
            ent.setYBodyRot(entity.yBodyRot);
            entity.hurt(PETRIFY, Float.MAX_VALUE);
        }
    }
}
