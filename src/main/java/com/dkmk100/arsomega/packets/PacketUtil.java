package com.dkmk100.arsomega.packets;

import com.dkmk100.arsomega.ArsOmega;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


//adapted from cyclops core
//and covered under the MIT licence
//https://github.com/CyclopsMC/CyclopsCore/blob/master-1.18/LICENSE.txt
public class PacketUtil {
    private static SimpleChannel networkChannel = null;

    public static void init() {
        if(networkChannel == null) {
            networkChannel = NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(ArsOmega.MOD_ID, "channel_main"), () -> "1.0.0",
                    Predicates.alwaysTrue(), Predicates.alwaysTrue());
        }
    }

    static int packets = 0;

    public static <P extends BasicPacket> void register(Class<P> packetType) {
        int discriminator = packets;
        packets+=1;
        try {
            Constructor<P> constructor = packetType.getConstructor();
            networkChannel.registerMessage(discriminator, packetType,
                    (packet, packetBuffer) -> packet.encode(packetBuffer),
                    (packetBuffer) -> {
                        P packet = null;
                        try {
                            packet = constructor.newInstance();
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        packet.decode(packetBuffer);
                        return packet;
                    },
                    (packet, contextSupplier) -> {
                        NetworkEvent.Context context = contextSupplier.get();
                        if (context.getDirection().getReceptionSide().isClient()) {
                            //if (packet.isAsync()) {
                                //handlePacketClient(context, packet);
                            //} else {
                                context.enqueueWork(() -> handlePacketClient(context, packet));
                            //}
                        } else {
                            //if (packet.isAsync()) {
                            //    handlePacketServer(context, packet);
                            //} else {
                                context.enqueueWork(() -> handlePacketServer(context, packet));
                            //}
                        }
                        context.setPacketHandled(true);
                    });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            ArsOmega.LOGGER.error("No default constructor for packet: " + packetType.getName());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handlePacketClient(NetworkEvent.Context context, BasicPacket packet) {
        packet.actionClient(Minecraft.getInstance().player.level, Minecraft.getInstance().player);
    }

    public static void handlePacketServer(NetworkEvent.Context context, BasicPacket packet) {
        packet.actionServer(context.getSender().getLevel(), context.getSender());
    }

    public static void sendToServer(BasicPacket packet) {
        networkChannel.sendToServer(packet);
    }

    public static void sendToPlayer(BasicPacket packet, ServerPlayer player) {
        networkChannel.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAllAround(BasicPacket packet, PacketDistributor.TargetPoint point) {
        PacketDistributor.PacketTarget target = PacketDistributor.NEAR.with(() -> point);
        target.send(networkChannel.toVanillaPacket(packet, target.getDirection()));
    }

    public static void sendToDimension(BasicPacket packet, ResourceKey<Level> dimension) {
        PacketDistributor.PacketTarget target = PacketDistributor.DIMENSION.with(() -> dimension);
        target.send(networkChannel.toVanillaPacket(packet, target.getDirection()));
    }

    public static void sendToAll(BasicPacket packet) {
        PacketDistributor.PacketTarget target = PacketDistributor.ALL.with(() -> null);
        target.send(networkChannel.toVanillaPacket(packet, target.getDirection()));
    }


}
