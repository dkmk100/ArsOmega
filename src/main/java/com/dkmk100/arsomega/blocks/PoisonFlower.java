package com.dkmk100.arsomega.blocks;

import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PoisonFlower extends FlowerBlock {
    public static final IntegerProperty POWER = BlockStateProperties.LEVEL;

    public PoisonFlower(MobEffect p_i49968_1_, BlockBehaviour.Properties p_i49968_2_) {
        super(p_i49968_1_, 8, p_i49968_2_);
    }

    protected boolean mayPlaceOn(BlockState p_200014_1_, BlockGetter p_200014_2_, BlockPos p_200014_3_) {
        return super.mayPlaceOn(p_200014_1_, p_200014_2_, p_200014_3_);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState p_180655_1_, Level p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
        VoxelShape voxelshape = this.getShape(p_180655_1_, p_180655_2_, p_180655_3_, CollisionContext.empty());
        Vec3 vector3d = voxelshape.bounds().getCenter();
        double d0 = (double)p_180655_3_.getX() + vector3d.x;
        double d1 = (double)p_180655_3_.getZ() + vector3d.z;

        for(int i = 0; i < 3; ++i) {
            if (p_180655_4_.nextBoolean()) {
                p_180655_2_.addParticle(ParticleTypes.SMOKE, d0 + p_180655_4_.nextDouble() / 5.0D, (double)p_180655_3_.getY() + (0.5D - p_180655_4_.nextDouble()), d1 + p_180655_4_.nextDouble() / 5.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(POWER);
    }

    public void entityInside(BlockState p_196262_1_, Level world, BlockPos p_196262_3_, Entity p_196262_4_) {
        if (!world.isClientSide) {
            if (p_196262_4_ instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)p_196262_4_;
                if (!livingentity.isInvulnerableTo(DamageSource.MAGIC)) {
                    int power = world.getBlockState(p_196262_3_).getValue(POWER);
                    float damage = (float)(3.0 + 0.5 * power);
                    livingentity.hurt(DamageSource.CACTUS,damage);
                    livingentity.addEffect(new MobEffectInstance(MobEffects.POISON, 200 + 30*power,1));
                }
            }

        }
    }
}
