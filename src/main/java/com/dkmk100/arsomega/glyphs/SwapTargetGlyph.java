package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ItemsRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectToss;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class SwapTargetGlyph extends AbstractEffect implements ConfigurableGlyph {
    public static SwapTargetGlyph INSTANCE = new SwapTargetGlyph("swap_target","swap_target");

    ForgeConfigSpec.BooleanValue AFFECT_PLAYERS;
    ForgeConfigSpec.BooleanValue ALLOW_TOSS;

    private SwapTargetGlyph(String tag, String description) {
        super(tag,description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        spellContext.setCanceled(true);
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof LivingEntity && world instanceof ServerLevel) {
            boolean player = entity instanceof Player;
            if(!AFFECT_PLAYERS.get() && player){
                return;
            }
            if(CuriosApi.getCuriosHelper().findFirstCurio((LivingEntity) entity, ItemsRegistry.STABILITY_CLOAK).isPresent()){
                return;
            }
            if (spellContext.getCurrentIndex() < spellContext.getSpell().recipe.size() && BlockUtil.destroyRespectsClaim(shooter, world, entity.blockPosition().below())) {
                Spell newSpell = new Spell(new ArrayList(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
                boolean hasToss = false;
                for(AbstractSpellPart part : newSpell.recipe){
                    if(part == EffectToss.INSTANCE){
                        hasToss = true;
                    }
                }
                if(!hasToss || ALLOW_TOSS.get()) {
                    SpellContext newContext = (new SpellContext(newSpell, (LivingEntity) entity)).withColors(spellContext.colors);
                    SpellResolver.resolveEffects(entity.getCommandSenderWorld(), (LivingEntity) entity, new EntityHitResult(shooter), newSpell, newContext);
                }
            }
        }
    }

    public int getDefaultManaCost() {
        return 800;
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
        builder.comment("WARNING! THE FOLLOWING ALLOWS FOR MAJOR GRIEFING!!");
        builder.comment("Only enable if you understand players could loose their stuff...");
        ALLOW_TOSS = builder.define("allow_toss", false);
    }

}
