package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import joptsimple.internal.Reflection;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.InvocationTargetException;
import java.sql.Ref;

public class RitualChangeBiome  extends AbstractRitual {
    protected void tick() {
        Level world = this.getWorld();
        BlockPos pos = this.getPos();

        //get biome
        String biomeName = "minecraft:plains";
        boolean choseBiome = false;
        boolean canDoNether = false;
        for(ItemStack stack : this.getConsumedItems()){
            if(!choseBiome && stack.getItem() == ItemsRegistry.BIOME_CRYSTAL && stack.hasTag() && stack.getTag().contains("biome")){
                biomeName = stack.getTag().getString("biome");
                choseBiome = true;
                //don't break because we'll check for dim crystals later
            }
            if(stack.getItem() == ItemsRegistry.DEMONIC_GEM){
                canDoNether = true;
            }
        }
        RegistryAccess reg = world.registryAccess();
        Registry<Biome> a = reg.registry(Registry.BIOME_REGISTRY).get();
        ResourceLocation loc = new ResourceLocation(biomeName);
        Biome biome = a.get(loc);
        //fix for wrong biome name
        boolean canConvert = true;
        if(biome==null){
            biome = a.get(new ResourceLocation("minecraft:plains"));
            ArsOmega.LOGGER.error("Missing biome: "+biomeName);
            canConvert = false;
        }

        //bime color and nether check
        int biomeColor = 0;
        try {
            Biome.BiomeCategory category = (Biome.BiomeCategory) ReflectionHandler.biomeCategory.get(biome);
            if (category == Biome.BiomeCategory.NETHER|| category == Biome.BiomeCategory.THEEND) {
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
        }


        //conversion
        if(this.getProgress()>=10) {
            if(canConvert) {
                Holder<Biome> newBiome = a.getOrCreateHolder(ResourceKey.create(Registry.BIOME_REGISTRY,loc));

                //setBiome(world,pos,newBiome);

                //widen the area a bit lol
                final int sidewaysRange = 7;
                final int upRange = 6;
                final int sideSpacing = 1;
                final int upSpacing = 1;
                for(int x=-1 * sidewaysRange;x<=sidewaysRange;x++) {
                    for(int z=-1 * sidewaysRange;z<=sidewaysRange;z++) {
                        for (int i = -1 * upRange; i <= upRange; i++) {
                            BlockPos newPos = new BlockPos(pos.getX() + sideSpacing * x, pos.getY() + upSpacing * i, pos.getZ() + sideSpacing*z);
                            setBiome(world, newPos, newBiome);
                        }
                    }
                }
            }
            this.setFinished();
        }
        else if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if(this.needsManaNow()){
                return;
            }
            else{
                this.setNeedsMana(true);
            }
            this.incrementProgress();
        }
    }

    private static void setBiome(Level world, BlockPos pos, Holder<Biome> biome){
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

    //pretty sure I don't want this but I'm gonna keep it just in case...
    //seems useless though
    private static BlockPos getNoisePos(BlockPos pos, BiomeManager manager) throws IllegalAccessException, InvocationTargetException {
        int i = pos.getX() - 2;
        int j = pos.getY() - 2;
        int k = pos.getZ() - 2;
        int l = i >> 2;
        int i1 = j >> 2;
        int j1 = k >> 2;
        double d0 = (double)(i & 3) / 4.0D;
        double d1 = (double)(j & 3) / 4.0D;
        double d2 = (double)(k & 3) / 4.0D;
        int k1 = 0;
        double d3 = Double.POSITIVE_INFINITY;

        for(int l1 = 0; l1 < 8; ++l1) {
            boolean flag = (l1 & 4) == 0;
            boolean flag1 = (l1 & 2) == 0;
            boolean flag2 = (l1 & 1) == 0;
            int i2 = flag ? l : l + 1;
            int j2 = flag1 ? i1 : i1 + 1;
            int k2 = flag2 ? j1 : j1 + 1;
            double d4 = flag ? d0 : d0 - 1.0D;
            double d5 = flag1 ? d1 : d1 - 1.0D;
            double d6 = flag2 ? d2 : d2 - 1.0D;
            long biomeZoomSeed = ReflectionHandler.zoomSeed.getLong(manager);
            double d7 = (double) ReflectionHandler.getFiddledDistance.invoke(null, biomeZoomSeed, i2, j2, k2, d4, d5, d6);
            if (d3 > d7) {
                k1 = l1;
                d3 = d7;
            }
        }

        int l2 = (k1 & 4) == 0 ? l : l + 1;
        int i3 = (k1 & 2) == 0 ? i1 : i1 + 1;
        int j3 = (k1 & 1) == 0 ? j1 : j1 + 1;

        return new BlockPos(l2,i3,j3);
    }

    @Override
    public int getManaCost() {
        return 5;
    }
    @Override
    public ParticleColor getCenterColor() {

        return new ParticleColor(220,240,25);
    }
    @Override
    public boolean consumesMana() {
        return true;
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.getItem()==ItemsRegistry.BIOME_CRYSTAL || stack.getItem()==ItemsRegistry.DEMONIC_GEM;
    }

    @Override
    public String getID() {
        return "change_biome";
    }
}

