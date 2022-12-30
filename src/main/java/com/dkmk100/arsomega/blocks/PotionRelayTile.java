package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class PotionRelayTile extends ModdedTile implements ITooltipProvider, IWandable, ITickable {
    private BlockPos toPos;
    private BlockPos fromPos;
    public boolean disabled;

    public PotionRelayTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.PotionRelayType.get(), pos, state);
    }

    public PotionRelayTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockPos getToPos() {
        return this.toPos;
    }

    public void setToPos(BlockPos toPos) {
        this.toPos = toPos;
    }

    public BlockPos getFromPos() {
        return this.fromPos;
    }

    public void setFromPos(BlockPos fromPos) {
        this.fromPos = fromPos;
    }

    public boolean setTakeFrom(BlockPos pos) {
        if (!(BlockUtil.distanceFrom(pos, this.worldPosition) > (double)this.getMaxDistance()) && !pos.equals(this.getBlockPos())) {
            this.fromPos = pos;
            this.update();
            return true;
        } else {
            return false;
        }
    }

    public boolean setSendTo(BlockPos pos) {
        if (!(BlockUtil.distanceFrom(pos, this.worldPosition) > (double)this.getMaxDistance()) && !pos.equals(this.getBlockPos())) {
            this.toPos = pos;
            this.update();
            return true;
        } else {
            return false;
        }
    }

    public int getMaxDistance() {
        return 30;
    }

    public void clearPos() {
        this.toPos = null;
        this.fromPos = null;
        this.update();
    }

    public boolean update() {
        if (this.worldPosition != null && this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 2);
            return true;
        } else {
            return false;
        }
    }

    public int transferPotion(PotionJarTile from, PotionJarTile to) {
        int transferRate = this.getTransferRate(from, to);
        if(from.getPotion() != to.getPotion() && !to.canAcceptNewPotion()){
            return 0;
        }
        from.setAmount(from.getAmount() - transferRate);//remove
        to.addAmount(transferRate);
        to.setPotion(from.getPotion(),from.getCustomEffects());
        return transferRate;
    }

    public int getTransferRate(PotionJarTile from, PotionJarTile to) {
        return Math.min(Math.min(this.getTransferRate(), from.getAmount()), to.getMaxFill() - to.getAmount());
    }

    public int getTransferRate() {
        return 500;
    }

    public boolean closeEnough(BlockPos pos) {
        return BlockUtil.distanceFrom(pos, this.worldPosition) <= (double)this.getMaxDistance() && !pos.equals(this.getBlockPos());
    }

    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null && !this.level.isClientSide && !storedPos.equals(this.getBlockPos())) {
            if (this.setSendTo(storedPos.immutable())) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.send", new Object[]{DominionWand.getPosString(storedPos)}));
                ParticleUtil.beam(storedPos, this.worldPosition, this.level);
            } else {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
            }

        }
    }

    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null && !storedPos.equals(this.getBlockPos())) {
            if (!(this.level.getBlockEntity(storedPos) instanceof RelayTile)) {
                if (this.setTakeFrom(storedPos.immutable())) {
                    PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.take", new Object[]{DominionWand.getPosString(storedPos)}));
                } else {
                    PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
                }

            }
        }
    }

    public void onWanded(Player playerEntity) {
        this.clearPos();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
    }

    public void tick() {
        if (!this.level.isClientSide && !this.disabled) {
            if (this.level.getGameTime() % 20L == 0L) {
                PotionJarTile fromTile;
                PotionJarTile toTile;
                if (this.fromPos != null && this.toPos != null) {
                    if (!(this.level.getBlockEntity(this.fromPos) instanceof PotionJarTile)) {
                        this.fromPos = null;
                        this.update();
                        return;
                    }
                    if (!(this.level.getBlockEntity(this.toPos) instanceof PotionJarTile)) {
                        this.fromPos = null;
                        this.update();
                        return;
                    }
                    BlockEntity var2 = this.level.getBlockEntity(this.fromPos);
                    BlockEntity var3 = this.level.getBlockEntity(this.toPos);
                    if (var2 instanceof PotionJarTile && var3 instanceof PotionJarTile) {
                        toTile = (PotionJarTile) var3;
                        fromTile = (PotionJarTile) var2;

                        if (this.transferPotion(fromTile,toTile) > 0) {
                            this.update();
                            this.spawnColoredFollowProjectile(this.level, this.fromPos, this.worldPosition, ParticleColor.fromInt(fromTile.getColor()));
                            this.spawnColoredFollowProjectile(this.level, this.worldPosition, this.toPos, ParticleColor.fromInt(fromTile.getColor()));
                        }
                    }
                }
            }
        }
    }

    public static void spawnColoredFollowProjectile(Level world, BlockPos from, BlockPos to, ParticleColor color) {
        if (world.isLoaded(to) && world.isLoaded(from)) {
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, from, to);
            aoeProjectile.setColor(color.toWrapper());
            world.addFreshEntity(aoeProjectile);
        }

    }

    public void load(CompoundTag tag) {
        if (NBTUtil.hasBlockPos(tag, "to")) {
            this.toPos = NBTUtil.getBlockPos(tag, "to");
        } else {
            this.toPos = null;
        }

        if (NBTUtil.hasBlockPos(tag, "from")) {
            this.fromPos = NBTUtil.getBlockPos(tag, "from");
        } else {
            this.fromPos = null;
        }

        this.disabled = tag.getBoolean("disabled");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.toPos != null) {
            NBTUtil.storeBlockPos(tag, "to", this.toPos);
        } else {
            NBTUtil.removeBlockPos(tag, "to");
        }

        if (this.fromPos != null) {
            NBTUtil.storeBlockPos(tag, "from", this.fromPos);
        } else {
            NBTUtil.removeBlockPos(tag, "from");
        }

        tag.putBoolean("disabled", this.disabled);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.toPos == null) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_to"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_to", new Object[]{1}));
        }

        if (this.fromPos == null) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_from"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_from", new Object[]{1}));
        }

        if (this.disabled) {
            tooltip.add(Component.translatable("ars_nouveau.tooltip.turned_off"));
        }

    }
}
