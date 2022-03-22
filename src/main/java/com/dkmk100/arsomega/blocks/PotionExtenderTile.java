package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PotionExtenderTile extends TileEntity implements ITickableTileEntity {
    int timeMixing;
    boolean isMixing;
    boolean hasMana;

    public PotionExtenderTile() {
        super(RegistryHandler.PotionExtenderType.get());
    }

    public void tick() {
        if (!this.level.isClientSide && !this.hasMana && this.level.getGameTime() % 20L == 0L && ManaUtil.takeManaNearbyWithParticles(this.worldPosition, this.level, 5, 100) != null) {
            this.hasMana = true;
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
        }

        if (this.hasMana) {
            PotionJarTile tile1 = null;
            Direction[] var3 = Direction.values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Direction d = var3[var5];
                if (d != Direction.DOWN) {
                    if (tile1 != null) {
                        break;
                    }
                    TileEntity tileEntity = this.level.getBlockEntity(this.worldPosition.relative(d));
                    if (tileEntity instanceof PotionJarTile && ((PotionJarTile)tileEntity).getAmount() > 0) {
                        if (tile1 == null) {
                            tile1 = (PotionJarTile)tileEntity;
                        }
                    }
                }
            }

            if (tile1 != null && tile1.getAmount() >= 100) {
                PotionJarTile combJar = null;
                if (this.level.getBlockEntity(this.worldPosition.below()) instanceof PotionJarTile) {
                    combJar = (PotionJarTile)this.level.getBlockEntity(this.worldPosition.below());
                }

                if (combJar == null) {
                    this.isMixing = false;
                    this.timeMixing = 0;
                } else {
                    List<EffectInstance> combined = this.getCombinedResult(tile1);
                    if ((!combJar.isMixEqual(combined) || combJar.getMaxFill() - combJar.getCurrentFill() < 100) && combJar.getAmount() != 0) {
                        this.isMixing = false;
                        this.timeMixing = 0;
                    } else {
                        this.isMixing = true;
                        ++this.timeMixing;
                        ParticleColor color1 = ParticleColor.fromInt(tile1.getColor());
                        if (!this.level.isClientSide) {
                            if (this.timeMixing % 20 == 0 && this.timeMixing > 0 && this.timeMixing <= 60) {
                                EntityFlyingItem item = (new EntityFlyingItem(this.level, tile1.getBlockPos().above(), this.worldPosition, Math.round(255.0F * color1.getRed()), Math.round(255.0F * color1.getGreen()), Math.round(255.0F * color1.getBlue()))).withNoTouch();
                                item.setDistanceAdjust(2.0F);
                                this.level.addFreshEntity(item);
                            }

                            if (!this.level.isClientSide /* && this.timeMixing >= 120*/) {
                                ++this.timeMixing;
                                if (this.timeMixing >= 120) {
                                    this.timeMixing = 0;
                                }

                                Potion jar1Potion = tile1.getPotion();
                                if (combJar.getAmount() == 0) {
                                    combJar.setPotion(Potions.WATER, combined);
                                    combJar.setFill(100);
                                    tile1.addAmount(-100);
                                    this.hasMana = false;
                                    this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
                                } else if (combJar.isMixEqual(combined) && combJar.getMaxFill() - combJar.getCurrentFill() >= 100) {
                                    combJar.addAmount(100);
                                    tile1.addAmount(-100);
                                    this.hasMana = false;
                                    this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
                                }
                            }

                        } else {
                            if (this.timeMixing >= 80 && combJar.getPotion() != Potions.EMPTY) {
                                for(int i = 0; i < 3; ++i) {
                                    double d0 = (double)this.worldPosition.getX() + 0.5D + ParticleUtil.inRange(-0.25D, 0.25D);
                                    double d1 = (double)(this.worldPosition.getY() + 1) + ParticleUtil.inRange(-0.1D, 0.4D);
                                    double d2 = (double)this.worldPosition.getZ() + 0.5D + ParticleUtil.inRange(-0.25D, 0.25D);
                                    this.level.addParticle(GlowParticleData.createData(ParticleColor.fromInt(combJar.getColor())), d0, d1, d2, 0.0D, 0.009999999776482582D, 0.0D);
                                }
                            }

                            int offset = 30;
                            if (this.timeMixing >= 60) {
                                this.level.addParticle(GlowParticleData.createData(color1), (double)((float)this.worldPosition.getX()) + 0.5D - Math.sin((double) ClientInfo.ticksInGame / 8.0D) / 4.0D, (double)((float)this.worldPosition.getY()) + 0.75D - Math.pow(Math.sin((double)ClientInfo.ticksInGame / 32.0D), 2.0D) / 2.0D, (double)((float)this.worldPosition.getZ()) + 0.5D - Math.cos((double)ClientInfo.ticksInGame / 8.0D) / 4.0D, 0.0D, 0.0D, 0.0D);
                            }

                            if (this.timeMixing >= 80) {
                                offset = 50;
                                this.level.addParticle(GlowParticleData.createData(color1), (double)((float)this.worldPosition.getX()) + 0.5D - Math.sin((double)(ClientInfo.ticksInGame + offset) / 8.0D) / 4.0D, (double)((float)this.worldPosition.getY()) + 0.75D - Math.pow(Math.sin((double)(ClientInfo.ticksInGame + offset) / 32.0D), 2.0D) / 2.0D, (double)((float)this.worldPosition.getZ()) + 0.5D - Math.cos((double)(ClientInfo.ticksInGame + offset) / 8.0D) / 4.0D, 0.0D, 0.0D, 0.0D);
                            }

                            if (this.timeMixing >= 120) {
                                this.timeMixing = 0;
                            }
                        }
                    }
                }
            } else {
                this.isMixing = false;
                this.timeMixing = 0;
            }
        }
    }


    public List<EffectInstance> getCombinedResult(PotionJarTile jar1) {
        return getBuffedEffects(jar1.getFullEffects());
    }
    public List<EffectInstance> getBuffedEffects(List<EffectInstance> originals) {
        List<EffectInstance> newEffects = new ArrayList<EffectInstance>();
        for (EffectInstance instance:originals) {
            //code for future potion amplifier
            //newEffects.add(new EffectInstance(instance.getEffect(), instance.getDuration(), Math.min(instance.getAmplifier()+1,12)));

            newEffects.add(new EffectInstance(instance.getEffect(), Math.min(instance.getDuration()+1200,72000),instance.getAmplifier()));
        }
        return newEffects;
    }

    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.timeMixing = nbt.getInt("mixing");
        this.isMixing = nbt.getBoolean("isMixing");
        this.hasMana = nbt.getBoolean("hasMana");
    }

    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("mixing", this.timeMixing);
        compound.putBoolean("isMixing", this.isMixing);
        compound.putBoolean("hasMana", this.hasMana);
        return super.save(compound);
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        this.handleUpdateTag(this.level.getBlockState(this.worldPosition), pkt.getTag());
    }
}
