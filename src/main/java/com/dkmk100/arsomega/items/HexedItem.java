package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;

public class HexedItem extends BasicItem implements ICasterTool {

    boolean alwaysShimmer;
    public HexedItem(Properties properties, String name, boolean shimmer){
        super(properties);
        this.setRegistryName("minecraft",name);
        this.alwaysShimmer = shimmer;
    }

    @Override
    public boolean isScribedSpellValid(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch((s) -> {
            return s instanceof AbstractCastMethod;
        });
    }

    @Override
    public boolean setSpell(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList();
        recipe.add(MethodSelf.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return ICasterTool.super.setSpell(caster, player, hand, stack, spell);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = this.getSpellCaster(stack);

        ActionResult<ItemStack> cast = cast(worldIn,playerIn,handIn, new TranslationTextComponent("ars_nouveau.wand.invalid"),caster.getSpell(),caster.getColor());
        playerIn.getItemInHand(handIn).shrink(1);
        return cast;
    }

    ActionResult<ItemStack> cast(World worldIn, PlayerEntity playerIn, Hand handIn, TranslationTextComponent invalidMessage, Spell spell, ParticleColor.IntWrapper color){
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide) {
            return ActionResult.pass(playerIn.getItemInHand(handIn));
        } else if (spell == null) {
            //PortUtil.sendMessageNoSpam(playerIn, invalidMessage);
            return new ActionResult(ActionResultType.SUCCESS, stack);
        } else {
            SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).withColors(color));
            boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;
            RayTraceResult result = playerIn.pick(5.0D, 0.0F, isSensitive);
            if (result instanceof BlockRayTraceResult && worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) instanceof ScribesTile) {
                return new ActionResult(ActionResultType.SUCCESS, stack);
            } else if (result instanceof BlockRayTraceResult && !playerIn.isShiftKeyDown() && worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) != null && !(worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) instanceof IntangibleAirTile) && !(worldIn.getBlockEntity(((BlockRayTraceResult)result).getBlockPos()) instanceof PhantomBlockTile)) {
                return new ActionResult(ActionResultType.SUCCESS, stack);
            } else {
                resolver.onResolveEffect(worldIn,null,new EntityRayTraceResult(playerIn));
                return new ActionResult(ActionResultType.CONSUME, stack);
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
