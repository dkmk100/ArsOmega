package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.LevelUtil;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBlink;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DarkShift extends AbstractShift{

    public static DarkShift INSTANCE = new DarkShift("darkshift","Dark Shift");

    public DarkShift(String tag, String description) {
        super(tag, description);
    }

    @Override
    protected boolean validLightLevel(int light, SpellStats stats, SpellContext context, boolean isLongRange, boolean isCasterLocation) {
        int amp = (int) stats.getAmpMultiplier();
        int maxLight = Math.min(8,3 + amp);
        return light < maxLight;
    }

    @Override
    protected double getRange(SpellStats stats, SpellContext context, boolean isLongRange) {
        double aoe = stats.getAoeMultiplier();
        return isLongRange ? 30 + 3 * aoe : 10 + 2 * aoe;
    }


    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{Schools.DEMONIC,SpellSchools.MANIPULATION});
    }
}

