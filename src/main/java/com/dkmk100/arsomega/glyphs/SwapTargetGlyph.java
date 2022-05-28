package com.dkmk100.arsomega.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class SwapTargetGlyph extends AbstractEffect implements ConfigurableGlyph {
    public static SwapTargetGlyph INSTANCE = new SwapTargetGlyph("swap_target","swap_target");

    ForgeConfigSpec.BooleanValue AFFECT_PLAYERS;

    private SwapTargetGlyph(String tag, String description) {
        super(tag,description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        spellContext.setCanceled(true);
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity && world instanceof ServerLevel) {
            if(!AFFECT_PLAYERS.get() && entity instanceof Player){
                return;
            }
            if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size()) {
                Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
                SpellContext newContext = (new SpellContext(newSpell, (LivingEntity) entity)).withColors(spellContext.colors);
                SpellResolver.resolveEffects(entity.getCommandSenderWorld(), (LivingEntity)entity, new EntityHitResult(shooter), newSpell, newContext);
            }
        }
    }

    public int getDefaultManaCost() {
        return 500;
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{});
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.MANIPULATION});
    }

    @Override
    public void buildExtraConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("If set to false, Swap Target will only affect non-player entities: ");
        AFFECT_PLAYERS = builder.define("affect_players", true);
    }

    @Override
    public void setConfig(ForgeConfigSpec spec) {
        this.CONFIG = spec;
    }
}
