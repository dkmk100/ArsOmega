package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


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
        return RegistryHandler.DEMON_GEM.get();
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
