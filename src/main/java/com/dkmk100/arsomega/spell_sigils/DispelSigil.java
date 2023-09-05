package com.dkmk100.arsomega.spell_sigils;

import com.dkmk100.arsomega.blocks.PortalBlock;
import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.List;

public class DispelSigil extends AbstractSpellSigil{

    static final int sideRange = 7;
    static final int upRange = 4;
    static final int downRange = 4;

    @Override
    public void OnActivateClient(Level world, BlockPos pos, Player player) {
        world.playSound(player, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS,1.0f,1.0f);
        ParticleUtil.spawnRitualAreaEffect(pos, world, world.getRandom(), ParticleColor.makeRandomColor(5,255,50,world.getRandom()), sideRange, 1, 18);

    }

    @Override
    public void OnActivate(ServerLevel world, BlockPos pos, Player player) {
        int sideRange = 5;
        int upRange = 7;
        int downRange = 2;
        for(int x = -1 * sideRange;x<sideRange;x++) {
            for (int z = -1 * sideRange; z < sideRange; z++) {
                for (int y = -1 * downRange; y < upRange; y++) {
                    BlockPos pos2 = pos.offset(x, y, z);
                    if (world.isInWorldBounds(pos2)) {
                        if (world.getBlockState(pos2).getBlock() instanceof PortalBlock) {
                            world.setBlockAndUpdate(pos2, Blocks.AIR.defaultBlockState());
                        }
                        if (world.getBlockState(pos2) instanceof IDispellable dispellable) {
                            dispellable.onDispel(player);
                        }
                        if (world.getBlockEntity(pos2) instanceof IDispellable dispellable) {
                            dispellable.onDispel(player);
                        }
                    }
                }
            }
        }

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, (new AABB(pos.above(upRange-downRange))).inflate(sideRange*2 + 1, downRange + upRange,sideRange*2 + 1));
        SpellContext context = new SpellContext(world,new Spell(),null);
        for (LivingEntity entity : entities) {
            if(entity.hasEffect(ModPotions.DISPELLANT.get())){
                continue;
            }
            //dispel code
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
            SpellStats.Builder builder = new SpellStats.Builder();
            SpellStats stats = builder.build();
            if (MinecraftForge.EVENT_BUS.post(new DispelEvent(new EntityHitResult(entity), world, null, stats, context))) {
                continue;
            }

            MobEffectInstance[] var9 = array;
            int var10 = array.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                MobEffectInstance e = var9[var11];
                if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))) {
                    entity.removeEffect(e.getEffect());
                }
            }

            if (entity instanceof IDispellable && entity.isAlive() && entity.getHealth() > 0.0F && !entity.isRemoved()) {
                ((IDispellable)entity).onDispel(player);
            }
        }
    }



    @Override
    protected int GetCost() {
        return 400;
    }

    @Override
    protected String[] GetPattern() {
        return new String[]{
                "  x x x  ",
                " xxxxxxx ",
                "xxx x xxx",
                " x xxx x ",
                "XXXXSXXXX",
                " x xxx x ",
                "xxx x xxx",
                " xxxxxxx ",
                "  x x x  "
        };
    }
}
