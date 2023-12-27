package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.util.LevelUtil;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBlink;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LightShift extends AbstractShift{

    public static LightShift INSTANCE = new LightShift("lightshift","Light Shift");

    public LightShift(String tag, String description) {
        super(tag, description);
    }

    @Override
    protected boolean validLightLevel(int light, SpellStats stats, SpellContext context, boolean isLongRange, boolean isCasterLocation) {
        int amp = (int) stats.getAmpMultiplier();
        int minLight = Math.max(7,12 - amp);
        return light > minLight;
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
        return this.setOf(new SpellSchool[]{Schools.CELESTIAL,SpellSchools.MANIPULATION});
    }
}
