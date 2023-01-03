package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.packets.PacketUtil;
import com.dkmk100.arsomega.packets.ResetChunkColorsPacket;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import joptsimple.internal.Reflection;
import net.minecraft.core.*;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.util.DebugBuffer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.sql.Ref;
import java.util.List;

public class RitualChangeBiome extends AbstractRitual {

    Biome biome = null;
    protected void tick() {
        Level world = this.getWorld();
        BlockPos pos = this.getPos();

        //get biome
        String biomeName = "minecraft:plains";
        boolean choseBiome = false;
        boolean canDoNether = false;
        for (ItemStack stack : this.getConsumedItems()) {
            if (!choseBiome && stack.getItem() == ItemsRegistry.BIOME_CRYSTAL && stack.hasTag() && stack.getTag().contains("biome")) {
                biomeName = stack.getTag().getString("biome");
                choseBiome = true;
                //don't break because we'll check for dim crystals later
            }
            if (stack.getItem() == ItemsRegistry.DEMONIC_GEM) {
                canDoNether = true;
            }
        }
        RegistryAccess reg = world.registryAccess();
        Registry<Biome> a = reg.registry(Registry.BIOME_REGISTRY).get();
        ResourceLocation loc = new ResourceLocation(biomeName);
        //cache biome for better performance
        if (biome == null) {
            biome = a.get(loc);
        }
        //fix for wrong biome name
        boolean canConvert = true;
        if (biome == null) {
            biome = a.get(new ResourceLocation("minecraft:plains"));
            ArsOmega.LOGGER.error("Missing biome: " + biomeName);
            canConvert = false;
        }

        //bime color and nether check
        int biomeColor = 0;
        try {
            Biome.BiomeCategory category = (Biome.BiomeCategory) ReflectionHandler.biomeCategory.get(biome);
            if (category == Biome.BiomeCategory.NETHER || category == Biome.BiomeCategory.THEEND) {
                if (!canDoNether) {
                    canConvert = false;
                    this.setFinished();
                } else {
                    if (world.isClientSide) {
                        biomeColor = biome.getFogColor();
                    }
                }
            } else {
                if (world.isClientSide) {
                    biomeColor = biome.getFoliageColor();
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //particles
        if (world.isClientSide) {
            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(ParticleColor.fromInt(biomeColor)), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
            return;
        }
        //everything down here is serverside

        //conversion
        if (this.getProgress() >= 10) {
            if (canConvert) {
                Holder<Biome> newBiome = a.getOrCreateHolder(ResourceKey.create(Registry.BIOME_REGISTRY, loc));

                Player player = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 25, false);
                //widen the area a bit lol
                final int sidewaysRange = 7;
                final int upRange = 6;
                final int sideSpacing = 1;
                final int upSpacing = 1;
                for (int x = -1 * sidewaysRange; x <= sidewaysRange; x++) {
                    for (int z = -1 * sidewaysRange; z <= sidewaysRange; z++) {
                        for (int i = -1 * upRange; i <= upRange; i++) {
                            BlockPos newPos = new BlockPos(pos.getX() + sideSpacing * x, pos.getY() + upSpacing * i, pos.getZ() + sideSpacing * z);
                            if (player!= null && BlockUtil.destroyRespectsClaim(player, world, newPos))
                            {
                                setBiome(world, newPos, newBiome);
                            }
                        }
                    }
                }
            }
            ChunkPos chunkPos = world.getChunkAt(pos).getPos();

            updateChunkAfterBiomeChange(world, chunkPos);

            this.setFinished();
        } else if (world.getGameTime() % 20L == 0L) {
            if (this.needsManaNow()) {
                return;
            } else {
                this.setNeedsMana(true);
            }
            this.incrementProgress();
        }
    }

    //adapted from EvilCraft
    public static void updateChunkAfterBiomeChange(Level world, ChunkPos chunkPos) {
        ArsOmega.LOGGER.info("updating colors in chunk: "+chunkPos.toString());
        LevelChunk chunkSafe = world.getChunkSource().getChunk(chunkPos.x, chunkPos.z, false);
        ChunkMap chunkMap = ((ServerChunkCache) world.getChunkSource()).chunkMap;
        ((ServerChunkCache) world.getChunkSource()).chunkMap.getPlayers(chunkPos, false).forEach((player) -> {
            try {
                ThreadedLevelLightEngine lightEngine = (ThreadedLevelLightEngine) ReflectionHandler.getLightEngine.invoke(chunkMap);
                player.connection.send(new ClientboundLevelChunkWithLightPacket(chunkSafe, lightEngine, null, null, true));
                PacketUtil.sendToPlayer(new ResetChunkColorsPacket(chunkPos.x, chunkPos.z), player);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static void setBiome(Level world, BlockPos pos, Holder<Biome> biome){
        try {
            LevelChunk chunk = world.getChunkAt(pos);

            BlockPos noisePos = new BlockPos(QuartPos.fromBlock(pos.getX()),QuartPos.fromBlock(pos.getY()),QuartPos.fromBlock(pos.getZ()));

            int i = QuartPos.fromBlock(chunk.getMinBuildHeight());
            int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
            int l = Mth.clamp(noisePos.getY(), i, k);
            int j = chunk.getSectionIndex(QuartPos.toBlock(l));

            int x = noisePos.getX() & 3;
            int y = l & 3;
            int z = noisePos.getZ() & 3;

            PalettedContainer<Holder<Biome>> biomes = (PalettedContainer<Holder<Biome>>) ReflectionHandler.biomes.get(chunk.getSections()[j]);
            biomes.set(x, y, z, biome);
            chunk.setUnsaved(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getManaCost() {
        return 5;
    }
    @Override
    public ParticleColor getCenterColor() {
        if(this.getConsumedItems().size()>0 && this.getWorld().isClientSide()){
            ItemStack stack = getConsumedItems().get(0);
            if(stack.getItem() == ItemsRegistry.BIOME_CRYSTAL && stack.hasTag() && stack.getTag().contains("biome")){
                if(biome==null) {
                    String biomeName = stack.getTag().getString("biome");
                    RegistryAccess reg = this.getWorld().registryAccess();
                    Registry<Biome> a = reg.registry(Registry.BIOME_REGISTRY).get();
                    ResourceLocation loc = new ResourceLocation(biomeName);
                    biome = a.get(loc);
                }
                try {
                    Biome.BiomeCategory category = (Biome.BiomeCategory) ReflectionHandler.biomeCategory.get(biome);
                    int biomeColor;
                    if (category == Biome.BiomeCategory.NETHER || category == Biome.BiomeCategory.THEEND) {
                        biomeColor = biome.getFogColor();

                    } else {
                        biomeColor = biome.getFoliageColor();
                    }

                    return ParticleColor.fromInt(biomeColor);
                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }

            }
        }
        return new ParticleColor(220,240,25);
    }
    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        int consumed = this.getConsumedItems().size();
        if(consumed==0){
            return stack.getItem()==ItemsRegistry.BIOME_CRYSTAL;
        }
        else if(consumed==1){
            return stack.getItem()==ItemsRegistry.DEMONIC_GEM;
        }

        return false;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.getConsumedItems().size() > 0;
    }

    @Override
    public String getID() {
        return "change_biome";
    }
}

