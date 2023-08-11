package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LightTest extends AbstractEffect {

    public static LightTest INSTANCE = new LightTest("light_test", "Light Test");

    public LightTest(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(shooter instanceof Player) {
            BlockPos pos;
            if(rayTraceResult instanceof BlockHitResult blockHit){
                pos = blockHit.isInside() ? blockHit.getBlockPos() : blockHit.getBlockPos().relative(blockHit.getDirection());
            }
            else{
                pos = new BlockPos(rayTraceResult.getLocation());
            }

            //alternative option that includes moon phases
            //in light and darkshift this would be a config option
            //int skyLightIgnored = world.isNight() ? 14 - Math.round(8f * world.getMoonBrightness()) : 0;

            int skyLightIgnored = world.isNight() ? 14 : 0;

            int light = world.getLightEngine().getRawBrightness(pos,skyLightIgnored);

            PortUtil.sendMessage(shooter, Component.literal("light level: "+light));

            //light and dark shift:
            //light level 0 to 6: darkshift works
            //light level 8 to 15: lightshift works
            //night counts as low sky light so that darkshift works
        }
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public int getDefaultManaCost() {
        return 200;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        ArrayList<AbstractAugment> list = new ArrayList<AbstractAugment>();
        return Collections.unmodifiableSet(new HashSet(list));
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ELEMENTAL_FIRE,SpellSchools.ELEMENTAL_EARTH});
    }
}
