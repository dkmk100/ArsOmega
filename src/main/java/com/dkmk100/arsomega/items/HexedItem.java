package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.block.tile.MageBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
  
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class HexedItem extends BasicItem implements ICasterTool {

    boolean alwaysShimmer;
    public HexedItem(Properties properties, String name, boolean shimmer){
        super(properties);
        this.setRegistryName("minecraft",name);
        this.alwaysShimmer = shimmer;
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch((s) -> {
            return s instanceof AbstractCastMethod;
        });
    }

    @Override
    public boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList();
        recipe.add(MethodSelf.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = this.getSpellCaster(stack);

        InteractionResultHolder<ItemStack> cast = cast(worldIn,playerIn,handIn, Component.translatable("ars_nouveau.wand.invalid"),caster.getSpell(),caster.getColor());
        playerIn.getItemInHand(handIn).shrink(1);
        return cast;
    }

    InteractionResultHolder<ItemStack> cast(Level worldIn, Player playerIn, InteractionHand handIn, TranslatableComponent invalidMessage, Spell spell, ParticleColor.IntWrapper color){
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide) {
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
        } else if (spell == null) {
            //PortUtil.sendMessageNoSpam(playerIn, invalidMessage);
            return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
        } else {
            SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).withColors(color));
            boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;
            HitResult result = playerIn.pick(5.0D, 0.0F, isSensitive);
            if (result instanceof BlockHitResult && worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) instanceof ScribesTile) {
                return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
            } else if (result instanceof BlockHitResult && !playerIn.isShiftKeyDown() && worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) != null && !(worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) instanceof IntangibleAirTile) && !(worldIn.getBlockEntity(((BlockHitResult)result).getBlockPos()) instanceof MageBlockTile)) {
                return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
            } else {
                resolver.onResolveEffect(worldIn,null,new EntityHitResult(playerIn));
                return new InteractionResultHolder(InteractionResult.CONSUME, stack);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if(alwaysShimmer) {
            return true;
        }
        else{
            return super.isFoil(stack);
        }
    }
}
