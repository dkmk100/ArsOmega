package com.dkmk100.arsomega.rituals;


import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.RitualUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.InvocationTargetException;
import java.sql.Ref;

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
            if (!choseBiome && stack.getItem() == RegistryHandler.BIOME_CRYSTAL.get() && stack.hasTag() && stack.getTag().contains("biome")) {
                biomeName = stack.getTag().getString("biome");
                choseBiome = true;
                //don't break because we'll check for dim crystals later
            }
            if (stack.getItem() == RegistryHandler.DEMON_GEM.get()) {
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
        //Biome.BiomeCategory category = (Biome.BiomeCategory) ReflectionHandler.biomeCategory.get(biome);
        //if (category == Biome.BiomeCategory.NETHER|| category == Biome.BiomeCategory.THEEND) {
        if (false) {
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


        //particles
        if (world.isClientSide) {
            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(ParticleColor.fromInt(biomeColor)), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }


        //conversion
        if (this.getProgress() >= 10) {
            if (canConvert) {
                var optional = ForgeRegistries.BIOMES.getResourceKey(ForgeRegistries.BIOMES.getValue(loc));
                ResourceKey<Biome> key = optional.get();

                //widen the area a bit lol
                final int sidewaysRange = 7;
                final int upRange = 6;
                final int sideSpacing = 1;
                final int upSpacing = 1;
                for (int x = -1 * sidewaysRange; x <= sidewaysRange; x++) {
                    for (int z = -1 * sidewaysRange; z <= sidewaysRange; z++) {
                        for (int i = -1 * upRange; i <= upRange; i++) {
                            BlockPos newPos = new BlockPos(pos.getX() + sideSpacing * x, pos.getY() + upSpacing * i, pos.getZ() + sideSpacing * z);
                            RitualUtil.changeBiome(world, newPos, key);
                        }
                    }
                }
            }
            this.setFinished();
        } else if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if (this.needsSourceNow()) {
                return;
            } else {
                this.setNeedsSource(true);
            }
            this.incrementProgress();
        }
    }

    @Override
    public int getSourceCost() {
        return 5;
    }
    @Override
    public ParticleColor getCenterColor() {
        if(this.getConsumedItems().size()>0 && this.getWorld().isClientSide()){
            ItemStack stack = getConsumedItems().get(0);
            if(stack.getItem() == RegistryHandler.BIOME_CRYSTAL.get() && stack.hasTag() && stack.getTag().contains("biome")){
                if(biome==null) {
                    String biomeName = stack.getTag().getString("biome");
                    RegistryAccess reg = this.getWorld().registryAccess();
                    Registry<Biome> a = reg.registry(Registry.BIOME_REGISTRY).get();
                    ResourceLocation loc = new ResourceLocation(biomeName);
                    biome = a.get(loc);
                }
                try {
                    //Biome. category = (Biome.BiomeCategory) ReflectionHandler.biomeCategory.get(biome);
                    int biomeColor;
                    //if (category == Biome.BiomeCategory.NETHER || category == Biome.BiomeCategory.THEEND) {
                        //biomeColor = biome.getFogColor();

                    //} else {
                        biomeColor = biome.getFoliageColor();
                    //}

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
    public boolean consumesSource() {
        return true;
    }

    @Override
    public boolean canStart() {
        return this.getConsumedItems().size() > 0;
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        int consumed = this.getConsumedItems().size();
        if(consumed==0){
            return stack.getItem()==RegistryHandler.BIOME_CRYSTAL.get();
        }
        else if(consumed==1){
            return stack.getItem()==RegistryHandler.DEMON_GEM.get();
        }

        return false;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName( "change_biome");
    }
}