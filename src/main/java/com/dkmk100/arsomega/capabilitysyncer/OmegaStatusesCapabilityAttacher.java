package com.dkmk100.arsomega.capabilitysyncer;

import com.dkmk100.arsomega.ArsOmega;
import com.google.common.base.Predicates;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

public class OmegaStatusesCapabilityAttacher extends CapabilityAttacher {

    public static SimpleChannel channel;
    public static final ResourceLocation LOCATION = new ResourceLocation(ArsOmega.MOD_ID,"statuses");
    public static final Capability<LivingEntityCapability> CAPABILITY = getCapability(new CapabilityToken<>() {});


    public static LazyOptional<LivingEntityCapability> getLivingEntityCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, LivingEntity entity) {
        genericAttachCapability(event, new OmegaStatusesCapability(entity), CAPABILITY, LOCATION);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static LivingEntityCapability getLivingEntityCapabilityUnwrap(LivingEntity entity) {
        return getLivingEntityCapability(entity).orElse(null);
    }


    public static void setupChannel(){
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(ArsOmega.MOD_ID, "channel_entity_capability"), () -> "1.0.0",
                Predicates.alwaysTrue(), Predicates.alwaysTrue());
    }

    public static void register() {
        CapabilityAttacher.registerCapability(OmegaStatusesCapability.class);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, OmegaStatusesCapabilityAttacher::attach, OmegaStatusesCapabilityAttacher::getLivingEntityCapability, true);
        SimpleEntityCapabilityStatusPacket.register(channel,0);
        SimpleEntityCapabilityStatusPacket.registerRetriever(LOCATION, OmegaStatusesCapabilityAttacher::getLivingEntityCapabilityUnwrap);
    }

}
