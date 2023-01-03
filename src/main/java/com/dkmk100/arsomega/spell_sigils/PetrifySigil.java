package com.dkmk100.arsomega.spell_sigils;

import com.dkmk100.arsomega.potions.BindEffect;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;

public class PetrifySigil extends AbstractSpellSigil {
    public PetrifySigil() {
        super();
    }

    protected int range = 11;

    @Override
    public void OnActivateClient(Level world, BlockPos pos, Player player){
        world.playSound(player, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS,1.0f,1.0f);

        ParticleUtil.spawnRitualAreaEffect(pos, world, world.getRandom(), ParticleColor.makeRandomColor(5,255,50,world.getRandom()), range, 1, 18);
    }

    @Override
    public void OnActivate(ServerLevel world, BlockPos pos, Player player) {
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(pos.getX(),pos.getY(),pos.getZ()),range*2,range,range*2));
        for(LivingEntity target : targets){
            if(target!=player){
                Collection<MobEffectInstance> effects = target.getActiveEffects();
                MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
                MobEffectInstance[] var9 = array;
                int var10 = array.length;

                for(int var11 = 0; var11 < var10; ++var11) {
                    MobEffectInstance e = var9[var11];
                    if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))||e.isCurativeItem(new ItemStack(RegistryHandler.CLEANSING_GEM.get()))||e.getEffect() instanceof BindEffect) {
                        target.removeEffect(e.getEffect());
                    }
                }

                target.addEffect(new MobEffectInstance(ModPotions.STONE_PETRIFICATION, 800,1,false,false));
            }
        }
        world.playSound(player, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS,1.0f,1.0f);
    }


    @Override
    protected int GetCost() {
        return 800;
    }

    @Override
    protected String[] GetPattern() {
        return new String[]{
                "   xxx xxx   ",
                "   x xxx x   ",
                "  xxx   xxx  ",
                "xxx xxxxx xxx",
                "x xxx x xxx x",
                "xx x xxx x xx",
                " x xxxSxxx x ",
                "xx x xxx x xx",
                "x xxx x xxx x",
                "xxx xxxxx xxx",
                "  xxx   xxx  ",
                "   x xxx x   ",
                "   xxx xxx   "
        };
    }
}

