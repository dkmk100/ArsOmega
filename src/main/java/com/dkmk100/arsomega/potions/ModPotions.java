package com.dkmk100.arsomega.potions;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraft.potion.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

public class ModPotions {

    public static DemonicCurse DEMONIC_CURSE = new DemonicCurse();
    public static PermaFlight PERMA_FLIGHT = new PermaFlight();
    public static Adrenaline ADRENALINE = new Adrenaline();
    public static Effect LEAD_SKIN = new GenericEffect(EffectType.BENEFICIAL,0,"lead_skin");
    public static Effect NO_BREAK = new GenericEffect(EffectType.NEUTRAL,0,"no_magic_break",false);
    public static Effect STONE_PETRIFICATION = new PetrificationEffect("stone_petrification");
    public static Effect VINE_BIND = new BindEffect("vine_bind",2743808);
    public static Effect BURNED = new GenericEffect(EffectType.HARMFUL,0,"burned",false);
    public static Effect SOUL_FIRE = new GenericEffect(EffectType.HARMFUL,0,"soulfire",false);
    //public static Effect FUTURE_DEATH = new DeathPotion("future_death");

    @Mod.EventBusSubscriber(
            modid = ArsOmega.MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static class RegistrationHandler {
        public RegistrationHandler() {
        }

        @SubscribeEvent
        public static void registerEffects(RegistryEvent.Register<Effect> event) {
            IForgeRegistry<Effect> registry = event.getRegistry();
            registry.registerAll(new Effect[]{DEMONIC_CURSE,PERMA_FLIGHT,ADRENALINE, LEAD_SKIN,NO_BREAK,STONE_PETRIFICATION,VINE_BIND,BURNED,SOUL_FIRE});
        }

        @SubscribeEvent
        public static void registerPotions(RegistryEvent.Register<Potion> event) {
            IForgeRegistry<Potion> registry = event.getRegistry();
            registry.register((new Potion(new EffectInstance[]{new EffectInstance(Effects.POISON, 3600,2)})).setRegistryName("poison_3_potion"));

        }
    }
}
