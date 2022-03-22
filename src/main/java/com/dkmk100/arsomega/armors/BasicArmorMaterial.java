package com.dkmk100.arsomega.armors;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.armor.Materials;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.function.Supplier;

public enum BasicArmorMaterial implements IArmorMaterial {
    Arcane(ArsOmega.MOD_ID+":arcane", 40, new int[]{4, 7, 9, 4}, 55, SoundEvents.ARMOR_EQUIP_LEATHER, 2.5F, () -> {
        return Ingredient.of(new IItemProvider[]{Items.NETHERITE_INGOT});
    },0.05f),
    Defensive(ArsOmega.MOD_ID+":defense", 50, new int[]{5, 8, 10, 5}, 30, SoundEvents.ARMOR_EQUIP_CHAIN, 3.0F, () -> {
        return Ingredient.of(new IItemProvider[]{Items.NETHERITE_INGOT});
    },0.075f);

    private static final int[] MAX_DAMAGE_ARRAY=new int[] {13, 15, 16, 11};
    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairMaterial;

    BasicArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness, Supplier<Ingredient> repairMaterial, float knockbackResistance){
        this.name=name;
        this.maxDamageFactor=maxDamageFactor;
        this.damageReductionAmountArray=damageReductionAmountArray;
        this.enchantability=enchantability;
        this.soundEvent=soundEvent;
        this.toughness=toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairMaterial=repairMaterial;
    }
    @Override
    public int getDurabilityForSlot(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slotIn) {
        return this.damageReductionAmountArray[slotIn.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
