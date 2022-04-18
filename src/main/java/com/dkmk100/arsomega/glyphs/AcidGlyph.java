package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;

public class AcidGlyph extends AbstractEffect {

    DamageSource ACID = new DamageSource("acid");

    public static AcidGlyph INSTANCE = new AcidGlyph("acid", "Acid");

    public AcidGlyph(String tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if ((world instanceof ServerLevel)) {
            try {
                BlockPos pos = rayTraceResult.getBlockPos();
                double amp = spellStats.getAmpMultiplier() + 1;

                if (CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS_ADVANCED, shooter).isPresent()) {
                    amp += 4;
                } else if (CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS, shooter).isPresent()) {
                    amp += 2;
                }

                Block block = world.getBlockState(pos).getBlock();
                Field field = ReflectionHandler.blockProperties;
                BlockBehaviour.Properties properties = ((BlockBehaviour.Properties) field.get(block));
                Field field2 = ReflectionHandler.destroyTime;
                float tier = field2.getFloat(properties);
                if(block==Blocks.GOLD_BLOCK){
                    tier += 20;
                }
                else if(block==Blocks.IRON_BLOCK){
                    tier += 10;
                }

                BlockState state = Blocks.AIR.defaultBlockState();

                boolean canRemove = true;

                if(tier>amp*2.5)
                {
                    canRemove = false;
                }

                if (canRemove) {
                    world.setBlockAndUpdate(pos, state);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double amp = spellStats.getAmpMultiplier() + 1;

        if (CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS_ADVANCED, shooter).isPresent()) {
            amp += 4;
        } else if (CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS, shooter).isPresent()) {
            amp += 2;
        }
        rayTraceResult.getEntity().hurt(ACID,(float)amp*2);
    }

    @Override
    public int getDefaultManaCost() {
        return 300;
    }

    @Override
    public String getBookDescription() {
        return "Corrodes blocks and damages entities";
    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.ALCHEMY});
    }
}
