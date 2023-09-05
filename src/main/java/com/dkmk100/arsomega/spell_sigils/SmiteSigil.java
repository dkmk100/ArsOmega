package com.dkmk100.arsomega.spell_sigils;

import com.dkmk100.arsomega.glyphs.Scald;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SmiteSigil extends AbstractSpellSigil{

    @Override
    public void OnActivateClient(Level world, BlockPos pos, Player player){
        world.playSound(player,pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS,1.0f,1.0f);
        ParticleUtil.spawnRitualAreaEffect(pos, world, world.getRandom(), new ParticleColor(70 + world.getRandom().nextInt(15),70 + world.getRandom().nextInt(15),5), 5, 2, 15);
    }

    @Override
    public void OnActivate(ServerLevel world, BlockPos pos, Player player) {
        int range = 5;
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(pos.getX(),pos.getY(),pos.getZ()),range*2,range,range*2));
        for(LivingEntity target : targets){
            if(target!=player){
                if(target instanceof Mob mob && (mob.getMobType() == MobType.UNDEAD || mob.getMobType() == MobType.ILLAGER)) {
                    target.hurt(DamageSource.MAGIC, 100);
                    target.addEffect(new MobEffectInstance(ModPotions.SHOCKED_EFFECT.get(),600));
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,600));
                }
                else{
                    target.hurt(DamageSource.MAGIC, 10);
                }
            }
        }
        world.playSound(player,pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS,1.0f,1.0f);
    }


    @Override
    protected int GetCost() {
        return 300;
    }

    @Override
    protected String[] GetPattern() {
        return new String[]{
                "   xxx   ",
                "  xx xx  ",
                "  x   x  ",
                "  x   x  ",
                "  x   x  ",
                "  x   x  ",
                "xxxxSxxxx",
                "x       x",
                "xxxx xxxx",
                "   x x   ",
                "   x x   ",
                "   xxx   "
        };
    }
}
