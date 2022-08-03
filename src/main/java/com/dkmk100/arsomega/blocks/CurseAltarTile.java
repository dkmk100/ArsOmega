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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CurseAltarTile extends GenericEmpathyAltar {

    float blood = 0;
    float maxBlood = 100;

    public CurseAltarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }
    public CurseAltarTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.CurseAltarType.get(), pos, state);
    }

    @Override
    public String getAltarType(){
        return "curse";
    }
    @Override

    public Item getFinalizeItem(){
        return ItemsRegistry.DEMONIC_GEM;
    }
    @Override

    public int getAlignment(){
        return -1;
    }

    @Override
    protected boolean canFinalize(){
        return true;
    }

    @Override
    protected void onFinalize(Player player){
        super.onFinalize(player);
    }
}
