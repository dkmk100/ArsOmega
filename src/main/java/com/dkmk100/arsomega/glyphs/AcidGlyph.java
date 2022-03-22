package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if ((world instanceof ServerWorld)) {
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
                AbstractBlock.Properties properties = ((AbstractBlock.Properties) field.get(block));
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
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double amp = spellStats.getAmpMultiplier() + 1;

        if (CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS_ADVANCED, shooter).isPresent()) {
            amp += 4;
        } else if (CuriosApi.getCuriosHelper().findEquippedCurio(ArsRegistry.ALCHEMY_FOCUS, shooter).isPresent()) {
            amp += 2;
        }
        rayTraceResult.getEntity().hurt(ACID,(float)amp*2);
    }

    @Override
    public int getManaCost() {
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
