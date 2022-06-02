package com.dkmk100.arsomega.rituals;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;
import java.util.List;

public class RitualFlowingTime extends AbstractRitual {

    protected void tick() {
        int range = 4;
        int tickAmount = 10;

        Level world = this.getWorld();
        BlockPos pos = this.getPos();
        if (this.getWorld().isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(this.getPos(), this.getWorld(), this.rand, this.getOuterColor(), range, 24, 5);
        } else {
            if (this.getWorld().getGameTime() % 20L != 0L)//pretty fast cause small range and weak effects
            {
                return;
            }

            boolean didWorkOnce;
            Iterator var5;

            didWorkOnce = false;
            //slightly higher for tall machines like potion stuff
            var5 = BlockPos.betweenClosed(pos.offset(range, -1, range), pos.offset(-range, 3, -range)).iterator();

            while (var5.hasNext()) {
                BlockPos b = (BlockPos) var5.next();
                if (b!=this.getPos() && TickBlock(b, (ServerLevel) world,tickAmount)) {
                    didWorkOnce = true;
                }
            }

            if (didWorkOnce) {
                this.setNeedsMana(true);
            }
        }

    }

    public boolean TickBlock(BlockPos pos, ServerLevel world, int amount){
        BlockEntity tile = world.getBlockEntity(pos);
        //we blacklist ritual braziers cause it causes too many issues
        if(tile!=null && !(tile instanceof RitualBrazierTile)) {
            for (int i = 0; i < amount; i++) {
                BlockState state = tile.getBlockState();
                BlockEntityTicker<BlockEntity> blockentityticker = state.getTicker(world, (BlockEntityType<BlockEntity>) tile.getType());
                if (blockentityticker != null) {
                    blockentityticker.tick(world, pos, state, tile);
                }
                return true;
            }
        }

        return false;
    }


    public int getManaCost() {
        //low cost only because it activates so often
        return 500;
    }

    public boolean canConsumeItem(ItemStack stack) {
        return false;
    }

    public String getID() {
        return "flowing_time";
    }

    public ParticleColor getCenterColor() {
        return new ParticleColor(255,255,255);
    }
}

