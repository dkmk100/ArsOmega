package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.empathy_api.AbstractEmpathyIngredient;
import com.dkmk100.arsomega.empathy_api.EmpathyAPI;
import com.dkmk100.arsomega.empathy_api.EmpathyIngredientInstance;
import com.dkmk100.arsomega.empathy_api.EmpathySpell;
import com.dkmk100.arsomega.items.ItemPlayerStorage;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CurseAltarTile extends ModdedTile implements ITooltipProvider {

    EmpathySpell spell = null;
    boolean hasShard = false;

    public CurseAltarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }
    public CurseAltarTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.CurseAltarType.get(), pos, state);
    }

    public InteractionResult AddItem(ItemStack stack, Player player){
        //prepare altar for spellcrafting
        if(spell == null){
            //starting a new spell
            spell = new EmpathySpell();
        }

        //altar interactions
        if(stack.getItem() == ItemsRegistry.ENCHANTED_MIRROR_SHARD && !hasShard){
            this.hasShard = true;
            this.updateBlock();
            stack.shrink(1);
            PortUtil.sendMessage(player,"The effects of the curse will now be mirrored");
            return InteractionResult.SUCCESS;
        }
        if(stack.getItem() instanceof ItemPlayerStorage){
            spell.FinalizeSpell(player);
            LivingEntity target = ItemPlayerStorage.getTarget(stack,player.getCommandSenderWorld());


            float strength = 0.25f;
            if(target == player){
                //cursing yourself has full strength
                strength = 1.0f;
            }
            else if(hasShard){
                //mirror spell to both entities, and increase strength a lot
                strength = 0.6f;
                spell.casterWeight += 1;
            }

            //-1 alignment is 1 level of curse
            spell.CastSpell(target,strength,-1.0f);

            //reset spell
            spell = null;
            hasShard = false;
            PortUtil.sendMessage(player,"The curse has been cast on "+target.getName().getContents());
            this.updateBlock();
            return InteractionResult.SUCCESS;
        }
        AbstractEmpathyIngredient ingredient = EmpathyAPI.getIngredient(stack.getItem());
        if(ingredient == null){
            this.updateBlock();
            return InteractionResult.PASS;
        }
        //max ingredients is 5 for now
        else if(spell.getIngredients().size() >= 5 && spell.getIngredient(ingredient)==null){
            PortUtil.sendMessageNoSpam(player,new TextComponent("Cannot add more than 5 different ingredients"));
            this.updateBlock();
            return InteractionResult.PASS;
        }

        //normal ingredient
        else if(spell.canAddIngredient(ingredient)) {
            //only add 1 of the ingredient for now lol
            PortUtil.sendMessage(player,"trying to add ingredient");
            if(spell.tryAdd(ingredient)) {
                PortUtil.sendMessage(player,"added ingredient");
                stack.shrink(1);
            }
            this.updateBlock();
            return InteractionResult.CONSUME_PARTIAL;
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
            spell = new EmpathySpell(spellTag);
        } else {
            spell = null;
        }
        hasShard = tag.getBoolean("hasShard");
        super.load(tag);
    }

    @Override
    public void getTooltip(List<Component> list) {
        if(spell != null && spell.getIngredients().size() > 0) {
            list.add(new TextComponent("Current Recipe: "));
            for (EmpathyIngredientInstance ingredient : spell.getIngredients()) {
                list.add(new TextComponent("Item: " + ingredient.getIngredient().GetItem() + ",  count: "+ingredient.getAmount()));
            }
            if(hasShard){
                list.add(new TextComponent("Has mirror shard"));
            }
            list.add(new TextComponent("Right-click with a set iron needle to cast the curse on a target. Note that curses cast on self are significantly stronger than those cast on others. "));
            if(!hasShard){
                list.add(new TextComponent("Add an enchanted mirror shard to increase strength, but affect the caster equally. "));
            }
        }
        else{
            list.add(new TextComponent("No Ingredients"));
        }
    }
}
