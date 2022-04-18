package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class ModPotions {

    public static DemonicCurse DEMONIC_CURSE = new DemonicCurse();
    public static PermaFlight PERMA_FLIGHT = new PermaFlight();
    public static Adrenaline ADRENALINE = new Adrenaline();
    public static MobEffect LEAD_SKIN = new GenericEffect(MobEffectCategory.BENEFICIAL,0,"lead_skin");
    public static MobEffect NO_BREAK = new GenericEffect(MobEffectCategory.NEUTRAL,0,"no_magic_break",false);
    public static MobEffect STONE_PETRIFICATION = new PetrificationEffect("stone_petrification");
    public static MobEffect VINE_BIND = new BindEffect("vine_bind",2743808);
    public static MobEffect BURNED = new GenericEffect(MobEffectCategory.HARMFUL,0,"burned",false);
    public static MobEffect SOUL_FIRE = new GenericEffect(MobEffectCategory.HARMFUL,0,"soulfire",false);
    //public static Effect FUTURE_DEATH = new DeathPotion("future_death");

    @Mod.EventBusSubscriber(
            modid = ArsOmega.MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static class RegistrationHandler {
        public RegistrationHandler() {
        }

        @SubscribeEvent
        public static void registerEffects(RegistryEvent.Register<MobEffect> event) {
            IForgeRegistry<MobEffect> registry = event.getRegistry();
            registry.registerAll(new MobEffect[]{DEMONIC_CURSE,PERMA_FLIGHT,ADRENALINE, LEAD_SKIN,NO_BREAK,STONE_PETRIFICATION,VINE_BIND,BURNED,SOUL_FIRE});
        }

        @SubscribeEvent
        public static void registerPotions(RegistryEvent.Register<Potion> event) {
            IForgeRegistry<Potion> registry = event.getRegistry();
            registry.register((new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.POISON, 3600,2)})).setRegistryName("poison_3_potion"));

        }
    }
}
