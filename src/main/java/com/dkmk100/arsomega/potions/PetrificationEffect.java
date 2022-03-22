package com.dkmk100.arsomega.potions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class PetrificationEffect extends Effect {
    public static final DamageSource PETRIFY = (new DamageSource("petrify")).bypassArmor().bypassInvul().bypassMagic();

    public PetrificationEffect(String name) {
        super(EffectType.HARMFUL, 9211020);
        this.setRegistryName("arsomega", name);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0dee8a21-f182-42c8-8361-1ad6186cac30", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList();//does not include cleansing gem on purpose, ritual is complete
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager manager, int level) {
        super.removeAttributeModifiers(entity, manager, level);
        if (level >= 1) {
            entity.setHealth(1);
            ArmorStandEntity ent = new ArmorStandEntity(entity.getCommandSenderWorld(), entity.getX(), entity.getY(), entity.getZ());
            entity.getCommandSenderWorld().addFreshEntity(ent);
            ent.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlotType.CHEST, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlotType.LEGS, new ItemStack(Items.STONE, 1));
            ent.setItemSlot(EquipmentSlotType.FEET, new ItemStack(Items.STONE, 1));
            entity.getCommandSenderWorld().addFreshEntity(new ItemEntity(entity.getCommandSenderWorld(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.STONE, 1)));
            ent.setPose(entity.getPose());
            ent.setYHeadRot(entity.getYHeadRot());
            ent.setYBodyRot(entity.yBodyRot);
            entity.hurt(PETRIFY, Float.MAX_VALUE);
        }
    }
}
