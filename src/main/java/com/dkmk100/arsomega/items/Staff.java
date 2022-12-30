package com.dkmk100.arsomega.items;
/*
import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.glyphs.IIgnoreBuffs;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Staff extends SwordItem implements IAnimatable, ICasterTool {
    public AnimationFactory factory = new AnimationFactory(this);

    public Staff(Tier iItemTier, int baseDamage, float baseAttackSpeed) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup));
        this.augmentAmount = 2;
        this.augmentAdded = AugmentAmplify.INSTANCE;
        this.amountEach = 2;
    }
    public Staff(String name, Tier iItemTier, int baseDamage, float baseAttackSpeed) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup));
        this.setRegistryName(ArsOmega.MOD_ID, name);
        this.augmentAmount = 2;
        this.augmentAdded = AugmentAmplify.INSTANCE;
        this.amountEach = 2;
    }
    int augmentAmount;
    AbstractAugment augmentAdded;
    int amountEach;
    public Staff(String name, Tier iItemTier, int baseDamage, float baseAttackSpeed, int augmentAmount, AbstractAugment augmentAdded, int amountEach) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup));
        this.setRegistryName(ArsOmega.MOD_ID, name);
        this.augmentAmount = augmentAmount;
        this.augmentAdded = augmentAdded;
        this.amountEach = amountEach;
    }
    public Staff(String name, Tier iItemTier, int baseDamage, float baseAttackSpeed, int augmentAmount, AbstractAugment augmentAdded, int amountEach, boolean fireResistant) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup).fireResistant());
        this.setRegistryName(ArsOmega.MOD_ID, name);
        this.augmentAmount = augmentAmount;
        this.augmentAdded = augmentAdded;
        this.amountEach = amountEach;
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        event.getController().setAnimation((new AnimationBuilder()).addAnimation("wand_gem_spin", true));
        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = this.getSpellCaster(stack);

        InteractionResultHolder<ItemStack> cast  = caster.castSpell(worldIn, playerIn, handIn, Component.translatable("ars_nouveau.wand.invalid"));
        if(cast.getResult() == InteractionResult.CONSUME)//why you no work
        {
            //stack.setDamageValue(stack.getDamageValue() + 1);//damage staff
        }

        return cast;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 20.0F, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch((s) -> {
            return s instanceof AbstractCastMethod;
        });
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.wand.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList();
        recipe.add(MethodProjectile.INSTANCE);
        //recipe.add(AugmentAccelerate.INSTANCE);

        int i=0;
        for(AbstractSpellPart part : spell.recipe){
            recipe.add(part);
            int boostsLeft = augmentAmount;
            if(part instanceof AbstractEffect && part.compatibleAugments.contains(augmentAdded)) {
                boolean valid = true;
                if(part instanceof IIgnoreBuffs){
                    valid = false;
                }
                else if (i + 1 < spell.recipe.size()) {
                    AbstractSpellPart part2 = spell.recipe.get(i + 1);
                    int i2 = i + 1;
                    while (valid && i2 < spell.recipe.size() && part2 instanceof AbstractAugment) {
                        part2 = spell.recipe.get(i2);
                        if (part2 == AugmentDampen.INSTANCE) {
                            valid = false;
                        }
                        i2++;
                    }
                }
                if (valid) {
                    if(boostsLeft>0) {
                        boostsLeft-=1;
                        int i3=0;
                        while(i3<amountEach) {
                            recipe.add(augmentAdded);
                            i3+=1;
                        }
                    }
                }
            }
            i+=1;
        }

        //recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        this.getInformation(stack, worldIn, tooltip2, flagIn);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}

 */
