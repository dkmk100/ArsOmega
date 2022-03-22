package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.renderer.item.WandRenderer;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHarm;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Staff extends SwordItem implements IAnimatable, ICasterTool {
    public AnimationFactory factory = new AnimationFactory(this);

    public Staff(IItemTier iItemTier, int baseDamage, float baseAttackSpeed) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup).setISTER(() -> {
            return WandRenderer::new;
        }));
        this.augmentAmount = 2;
        this.augmentAdded = AugmentAmplify.INSTANCE;
        this.amountEach = 2;
    }
    public Staff(String name, IItemTier iItemTier, int baseDamage, float baseAttackSpeed) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup).setISTER(() -> {
            return WandRenderer::new;
        }));
        this.setRegistryName(ArsOmega.MOD_ID, name);
        this.augmentAmount = 2;
        this.augmentAdded = AugmentAmplify.INSTANCE;
        this.amountEach = 2;
    }
    int augmentAmount;
    AbstractAugment augmentAdded;
    int amountEach;
    public Staff(String name, IItemTier iItemTier, int baseDamage, float baseAttackSpeed, int augmentAmount, AbstractAugment augmentAdded, int amountEach) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup).setISTER(() -> {
            return WandRenderer::new;
        }));
        this.setRegistryName(ArsOmega.MOD_ID, name);
        this.augmentAmount = augmentAmount;
        this.augmentAdded = augmentAdded;
        this.amountEach = amountEach;
    }
    public Staff(String name, IItemTier iItemTier, int baseDamage, float baseAttackSpeed, int augmentAmount, AbstractAugment augmentAdded, int amountEach, boolean fireResistant) {
        super(iItemTier, baseDamage, baseAttackSpeed, (new Properties()).stacksTo(1).tab(ArsNouveau.itemGroup).setISTER(() -> {
            return WandRenderer::new;
        }).fireResistant());
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
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = this.getSpellCaster(stack);

        ActionResult<ItemStack> cast  = caster.castSpell(worldIn, playerIn, handIn, new TranslationTextComponent("ars_nouveau.wand.invalid"));
        if(cast.getResult() == ActionResultType.CONSUME)//why you no work
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
    public boolean isScribedSpellValid(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch((s) -> {
            return s instanceof AbstractCastMethod;
        });
    }

    @Override
    public void sendInvalidMessage(PlayerEntity player) {
        PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_nouveau.wand.invalid"));
    }

    @Override
    public boolean setSpell(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList();
        recipe.add(MethodProjectile.INSTANCE);
        //recipe.add(AugmentAccelerate.INSTANCE);

        int i=0;
        for(AbstractSpellPart part : spell.recipe){
            recipe.add(part);
            int boostsLeft = augmentAmount;
            if(part instanceof AbstractEffect && part.getCompatibleAugments().contains(AugmentAmplify.INSTANCE)) {
                boolean valid = true;
                if (i + 1 < spell.recipe.size()) {
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        this.getInformation(stack, worldIn, tooltip2, flagIn);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
