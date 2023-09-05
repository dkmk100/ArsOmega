package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.glyphs.IIgnoreBuffs;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.dkmk100.arsomega.items.Staff.*;

public class ModularStaff extends SwordItem implements IAnimatable, ICasterTool {
    public AnimationFactory factory = new AnimationFactory(this);

    public ModularStaff(Tier iItemTier, int baseDamage, float baseAttackSpeed) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup));
        this.augmentAmount = 2;
        this.augmentAdded = AugmentAmplify.INSTANCE;
        this.amountEach = 2;
    }
    int augmentAmount;
    AbstractAugment augmentAdded;
    int amountEach;
    public ModularStaff(Tier iItemTier, int baseDamage, float baseAttackSpeed, int augmentAmount, AbstractAugment augmentAdded, int amountEach) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup));
        this.augmentAmount = augmentAmount;
        this.augmentAdded = augmentAdded;
        this.amountEach = amountEach;
    }
    public ModularStaff(Tier iItemTier, int baseDamage, float baseAttackSpeed, int augmentAmount, AbstractAugment augmentAdded, int amountEach, boolean fireResistant) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsOmega.itemGroup).fireResistant());
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

        Spell spell = caster.getSpell();
        SpellContext context = new SpellContext(worldIn, spell,playerIn);
        SpellResolver resolver = new SpellResolver(context);

        if(spell.isEmpty() || !(spell.recipe.get(0) instanceof AbstractCastMethod)){
            PortUtil.sendMessageNoSpam(playerIn, Component.literal("No spell"));
            return new InteractionResultHolder<>(InteractionResult.PASS,stack);
        }


        try {
            //sorry for all the reflection shenanigans

            ISpellValidator validator = ArsNouveauAPI.getInstance().getSpellCastingSpellValidator();
            List<SpellValidationError> validationErrors = validator.validate(spell.recipe);
            for(SpellValidationError error : validationErrors){
                if(!(AugmentError.isInstance(error))){
                    PortUtil.sendMessageNoSpam(playerIn, error.makeTextComponentExisting());
                    return new InteractionResultHolder<>(InteractionResult.PASS,stack);
                }
            }

            if((boolean) enoughMana.invoke(resolver,playerIn)) {
                caster.getSpell().getCastMethod().onCast(stack, playerIn, worldIn, (SpellStats) getStats.invoke(resolver), context, resolver);
                resolver.expendMana();
            }
            else{
                return new InteractionResultHolder<>(InteractionResult.PASS,stack);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS,stack);
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

