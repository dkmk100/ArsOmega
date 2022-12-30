package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyAPI;
import com.dkmk100.arsomega.empathy_api.EmpathyIngredientInstance;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import com.dkmk100.arsomega.items.ItemPlayerStorage;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class GenericEmpathyAltar extends ModdedTile implements ITooltipProvider {
    protected EmpathySpell spell = null;
    protected boolean hasShard = false;

    public GenericEmpathyAltar(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    String LocName(Item item){
        Component comp = item.getName(item.getDefaultInstance());
        return comp.getString();
    }

    public String getAltarType(){
        return "empathy spell";
    }
    public Item getFinalizeItem(){
        return Items.AMETHYST_SHARD;
    }
    public int getAlignment(){
        return 0;
    }

    protected boolean canFinalize(){
        return true;
    }
    protected void onFinalize(Player player){
        PortUtil.sendMessage(player, "The " + getAltarType() + " has been finalized");
    }

    public InteractionResult AddItem(ItemStack stack, Player player){
        //prepare altar for spellcrafting
        if(spell == null){
            //starting a new spell
            spell = new EmpathySpell();
        }
        if(spell.isFinalized()){
            if (stack.getItem() instanceof ItemPlayerStorage) {
                LivingEntity target = ItemPlayerStorage.getTarget(stack, player.getCommandSenderWorld());
                float strength = 0.25f;
                if (target == player) {
                    //cursing yourself has full strength
                    strength = 1.0f;
                } else if (hasShard) {
                    //mirror spell to both entities, and increase strength a lot
                    strength = 0.6f;
                    spell.casterWeight += 1;
                }

                //-1 alignment is 1 level of curse
                spell.CastSpell(target, strength, getAlignment());
                //reset spell
                spell = null;
                hasShard = false;
                PortUtil.sendMessage(player, "The " + getAltarType() + " has been cast on " + target.getName().getContents());
                this.updateBlock();
                return InteractionResult.SUCCESS;
            }
            else if (stack.getItem().isEdible()) {
                float strength = 0.6f;
                if (hasShard) {
                    //mirror spell to both entities, and increase strength a lot
                    strength = 0.8f;
                    spell.casterWeight += 1;
                    spell.needsCaster = true;
                }

                //-1 alignment is 1 level of curse
                spell.applyToItem(stack,strength,-1.0f);

                //reset spell
                spell = null;
                hasShard = false;
                PortUtil.sendMessage(player, "The " + getAltarType() + " has been applied to the item. ");
                this.updateBlock();
                return InteractionResult.SUCCESS;
            }
        }
        else {
            //altar interactions
            if (stack.getItem() == ItemsRegistry.ENCHANTED_MIRROR_SHARD && !hasShard) {
                this.hasShard = true;
                this.updateBlock();
                stack.shrink(1);
                PortUtil.sendMessage(player, "The effects of the " + getAltarType() + " will now be mirrored");
                return InteractionResult.SUCCESS;
            }
            if(stack.getItem() == getFinalizeItem()) {
                if (spell.getIngredients().size() == 0) {
                    PortUtil.sendMessage(player, "Cannot finalize an empty " + getAltarType());
                    return InteractionResult.PASS;

                } else if(!canFinalize()){
                    PortUtil.sendMessage(player, "Cannot finalize the " + getAltarType());
                    return InteractionResult.PASS;
                }else {
                    stack.shrink(1);
                    spell.FinalizeSpell(player);
                    onFinalize(player);
                    return InteractionResult.SUCCESS;
                }
            }
            AbstractEmpathyIngredient ingredient = EmpathyAPI.getIngredient(stack.getItem());
            final int maxUniqueIngredients = 5;
            if (ingredient == null) {
                if(!stack.isEmpty()) {
                    PortUtil.sendMessage(player, Component.literal("no ingredient for item: ").append(stack.getHoverName().getString()));
                }
                this.updateBlock();
                return InteractionResult.PASS;
            }
            //max ingredients is 5 for now
            else if (spell.getIngredients().size() >= maxUniqueIngredients && spell.getIngredient(ingredient) == null) {
                PortUtil.sendMessageNoSpam(player, Component.literal("Cannot add more than " + maxUniqueIngredients + " different ingredients"));
                this.updateBlock();
                return InteractionResult.PASS;
            }
            else {
                AbstractEmpathyIngredient.AddResult result = spell.canAddIngredient(ingredient);
                //normal ingredient
                if (result.succeded) {
                    //add the actual ingredient
                    AbstractEmpathyIngredient.AddResult result2 = spell.tryAdd(ingredient);
                    if (result2.succeded) {
                        PortUtil.sendMessage(player, "Added ingredient.");
                        stack.shrink(1);
                    }
                    else{
                        PortUtil.sendMessage(player, "Failed to add ingredient: "+result2.message);
                    }
                    this.updateBlock();
                    return InteractionResult.CONSUME_PARTIAL;
                } else {
                    PortUtil.sendMessageNoSpam(player, Component.literal("Unable to add item: "+result.message));
                }
            }
        }
        this.updateBlock();
        return InteractionResult.PASS;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag spellTag = new CompoundTag();
        if(spell!=null) {
            spell.WriteTo(spellTag);
            tag.put("spell", spellTag);
        }
        tag.putBoolean("hasShard",hasShard);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("spell")) {
            CompoundTag spellTag = tag.getCompound("spell");
            spell = new EmpathySpell(spellTag,getLevel());
        } else {
            spell = null;
        }
        hasShard = tag.getBoolean("hasShard");
        super.load(tag);
    }

    @Override
    public void getTooltip(List<Component> list) {
        if(spell != null && spell.getIngredients().size() > 0) {
            list.add( Component.literal("Current Recipe: "));
            for (EmpathyIngredientInstance ingredient : spell.getIngredients()) {
                list.add( Component.literal("Item: " ).append(LocName(ingredient.getIngredient().GetItem())).append( ",  count: "+ingredient.getAmount()));
            }
            if(hasShard){
                list.add(Component.literal("Has mirror shard"));
            }

            if(spell.isFinalized()){
                list.add( Component.literal("Add a food item add the " + getAltarType() + " to it, or use an iron needle to weakly affect  "));
            }
            else{
                list.add( Component.literal("Right-click with a(n) " + LocName(getFinalizeItem()) +" to finalize the curse, making it castable"));
                if(!hasShard){
                    list.add( Component.literal("Add an enchanted mirror shard to increase strength, but affect the caster equally. "));
                }
            }
        }
        else{
            list.add( Component.literal("No Ingredients"));
        }
    }
}