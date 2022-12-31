package com.dkmk100.arsomega.blocks;

/*
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PotionAmplifierTile extends BlockEntity implements ITickable {
    int timeMixing;
    boolean isMixing;
    boolean hasMana;

    public PotionAmplifierTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.PotionAmplifierType.get(),pos,state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && !this.hasMana && this.level.getGameTime() % 20L == 0L && SourceUtil.takeSourceNearbyWithParticles(this.worldPosition, this.level, 5, 100) != null) {
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
                    BlockEntity tileEntity = this.level.getBlockEntity(this.worldPosition.relative(d));
                    if (tileEntity instanceof PotionJarTile && ((PotionJarTile)tileEntity).getAmount() > 0) {
                        if (tile1 == null) {
                            tile1 = (PotionJarTile)tileEntity;
                        }
                    }
                }
            }

            if (tile1 != null && tile1.getAmount() >= 200) {
                PotionJarTile combJar = null;
                if (this.level.getBlockEntity(this.worldPosition.below()) instanceof PotionJarTile) {
                    combJar = (PotionJarTile)this.level.getBlockEntity(this.worldPosition.below());
                }

                if (combJar == null) {
                    this.isMixing = false;
                    this.timeMixing = 0;
                } else {
                    List<MobEffectInstance> combined = this.getCombinedResult(tile1);
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

                            if (!this.level.isClientSide) {
                                ++this.timeMixing;
                                if (this.timeMixing >= 120) {
                                    this.timeMixing = 0;
                                }

                                Potion jar1Potion = tile1.getPotion();
                                if (combJar.getAmount() == 0) {
                                    combJar.setPotion(ModPotions.PotionsRegistry.BLENDED_POT, combined);
                                    combJar.setFill(100);
                                    tile1.addAmount(-200);
                                    this.hasMana = false;
                                    this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
                                } else if (combJar.isMixEqual(combined) && combJar.getMaxFill() - combJar.getCurrentFill() >= 100) {
                                    combJar.addAmount(100);
                                    tile1.addAmount(-200);
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


    public List<MobEffectInstance> getCombinedResult(PotionJarTile jar1) {
        return getBuffedEffects(jar1.getFullEffects());
    }
    public List<MobEffectInstance> getBuffedEffects(List<MobEffectInstance> originals) {
        List<MobEffectInstance> newEffects = new ArrayList<MobEffectInstance>();
        boolean hasBuffedEffect = false;
        for (MobEffectInstance instance:originals) {
            if(!hasBuffedEffect){
                newEffects.add(new MobEffectInstance(instance.getEffect(), instance.getDuration(), Math.min(instance.getAmplifier()+1,20)));
                hasBuffedEffect = true;
            }
            else{
                newEffects.add(new MobEffectInstance(instance.getEffect(), instance.getDuration(), instance.getAmplifier()));
            }
        }
        return newEffects;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.timeMixing = nbt.getInt("mixing");
        this.isMixing = nbt.getBoolean("isMixing");
        this.hasMana = nbt.getBoolean("hasMana");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putInt("mixing", this.timeMixing);
        compound.putBoolean("isMixing", this.isMixing);
        compound.putBoolean("hasMana", this.hasMana);
    }
}
 */