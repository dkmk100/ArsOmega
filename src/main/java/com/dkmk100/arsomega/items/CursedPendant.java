package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.potions.ModPotions;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBlink;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDispel;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CursedPendant extends BasicItem implements Vanishable {
    final int effectCount;
    final int defSeed;
    final float intensity;

    public CursedPendant(String reg, int effects, int defaultSeed, float intensityMult) {
        super((new Item.Properties()).stacksTo(1).tab(ArsNouveau.itemGroup).rarity(Rarity.values()[Math.min(Rarity.values().length - 1, Math.round(intensityMult))]),reg);
        defSeed = defaultSeed;
        effectCount = effects;
        intensity = intensityMult;
    }

    public int getSeed(ItemStack stack){
        return defSeed;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return effectCount > 1 ? true : super.isFoil(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        tooltip2.add(new TextComponent("Adds adverse effects to every effect in the wearer's spells."));
        tooltip2.add(new TextComponent("Has no practical purpose, fun for challenges though"));
        tooltip2.add(new TextComponent("Curse count: "+effectCount));
        tooltip2.add(new TextComponent("Curse strength: "+intensity));
        if(effectCount >3){
            tooltip2.add(new TextComponent("Not for the faint of heart"));
        }
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    public static void ApplyCursePre(ItemStack stack, EffectResolveEvent.Pre event){
        if(stack.getItem() instanceof CursedPendant){
            CursedPendant pendant = (CursedPendant) stack.getItem();
            long seed = pendant.getSeed(stack) + event.resolveEffect.name.hashCode();
            Random random = new Random(seed);
            for(int i=0;i<pendant.effectCount;i++){
                EffectCurse curse = EffectCurse.allCurses.get(random.nextInt(EffectCurse.allCurses.size()));
                curse.OnApplyPre(event, pendant.intensity);
            }
        }
    }
    public static void ApplyCursePost(ItemStack stack, EffectResolveEvent.Post event){
        if(stack.getItem() instanceof CursedPendant){
            CursedPendant pendant = (CursedPendant) stack.getItem();
            long seed = pendant.getSeed(stack) + event.resolveEffect.name.hashCode();
            Random random = new Random(seed);
            for(int i=0;i<pendant.effectCount;i++){
                EffectCurse curse = EffectCurse.allCurses.get(random.nextInt(EffectCurse.allCurses.size()));
                curse.OnApplyPost(event, pendant.intensity);
            }
        }
    }

    public abstract static class EffectCurse{
        public static List<EffectCurse> allCurses = List.of(Explode.instance,
                Effect.poison,Effect.wither,Effect.blindness, Effect.slow, Effect.weak, Effect.no_break, Effect.fatigue,
                Effect.dispellant, Effect.gravity, Effect.hex,
                Harm.instance, Dampen.instance, Blink.instance);

        void OnApplyPre(EffectResolveEvent.Pre event, float intensity){

        }
        void OnApplyPost(EffectResolveEvent.Post event, float intensity){

        }
        public static class Explode extends EffectCurse{
            public static Explode instance = new Explode();
            @Override
            void OnApplyPost(EffectResolveEvent.Post event, float intensity) {
                Vec3 vec =  event.rayTraceResult.getLocation();
                EffectExplosion.INSTANCE.explode(event.world, null, null, null, vec.x, vec.y, vec.z, intensity, false, Explosion.BlockInteraction.DESTROY, -1.5f + intensity);
            }
        }
        public static class Harm extends EffectCurse{
            public static Harm instance = new Harm();
            @Override
            void OnApplyPost(EffectResolveEvent.Post event, float intensity) {
                event.shooter.hurt(DamageSource.MAGIC, event.world.random.nextInt(1,3)*intensity);
            }
        }
        public static class Dampen extends EffectCurse{
            public static Dampen instance = new Dampen();
            @Override
            void OnApplyPre(EffectResolveEvent.Pre event, float intensity) {
                event.spellStats.setAmpMultiplier(event.spellStats.getAmpMultiplier() - intensity);
            }
        }


        public static class Blink extends EffectCurse{
            public static Blink instance = new Blink();
            @Override
            void OnApplyPre(EffectResolveEvent.Pre event, float intensity) {
                if(event.world.random.nextFloat() < 0.2f + 0.3f * intensity) {
                    EffectBlink.warpEntity(event.shooter, new BlockPos(event.rayTraceResult.getLocation()));
                }
            }
        }
        public static class Effect extends EffectCurse{
            public static Effect poison = new Effect(MobEffects.POISON,80,0);
            public static Effect wither = new Effect(MobEffects.WITHER,40,0);
            public static Effect blindness = new Effect(MobEffects.BLINDNESS,80,0);
            public static Effect slow = new Effect(MobEffects.MOVEMENT_SLOWDOWN, 80,2);
            public static Effect weak = new Effect(MobEffects.BLINDNESS,80,1);
            public static Effect no_break = new Effect(ModPotions.NO_BREAK,80,0);
            public static Effect fatigue = new Effect(MobEffects.DIG_SLOWDOWN,160,1);

            public static Effect dispellant = new Effect(ModPotions.DISPELLANT,30,0);

            public static Effect hex = new Effect(com.hollingsworth.arsnouveau.common.potions.ModPotions.HEX_EFFECT,30,0);

            public static Effect gravity = new Effect(com.hollingsworth.arsnouveau.common.potions.ModPotions.GRAVITY_EFFECT,30,0);

            MobEffect effect;
            int dur;
            int amp;
            public Effect(MobEffect effect, int dur, int amp){
                this.effect = effect;
                this.dur = dur;
                this.amp = amp;
            }
            @Override
            void OnApplyPost(EffectResolveEvent.Post event, float intensity) {
                event.shooter.addEffect(new MobEffectInstance(effect,Math.round(dur*intensity),amp));
            }
        }
    }
}
