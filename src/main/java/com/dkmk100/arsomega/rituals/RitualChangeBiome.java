package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RitualChangeBiome extends AbstractRitual {
    protected void tick() {
        World world = this.getWorld();
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
        DynamicRegistries reg = world.registryAccess();
        MutableRegistry<Biome> a = reg.registry(Registry.BIOME_REGISTRY).get();
        Biome biome = a.get(new ResourceLocation(biomeName));
        //fix for wrong biome name
        boolean canConvert = true;
        if(biome==null){
            biome = a.get(new ResourceLocation("minecraft:plains"));
            ArsOmega.LOGGER.error("Missing biome: "+biomeName);
            canConvert = false;
        }

        //bime color and nether check
        int biomeColor = 0;
        if(biome.getBiomeCategory()==Biome.Category.NETHER ||biome.getBiomeCategory()== Biome.Category.THEEND){
            if(!canDoNether){
                canConvert = false;
                this.setFinished();
            }
            else{
                if(world.isClientSide) {
                    biomeColor = biome.getFogColor();
                }
            }
        }
        else{
            if(world.isClientSide) {
                biomeColor = biome.getFoliageColor();
            }
        }

        //particles
        if (world.isClientSide) {
            for (int i = 0; i < 100; ++i) {
                Vector3d particlePos = (new Vector3d((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(ParticleColor.fromInt(biomeColor)), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }


        //conversion
        if(this.getProgress()>=10) {
            if(canConvert) {
                try {
                    Biome[] biomes = (Biome[]) ReflectionHandler.biomes.get(world.getChunkAt(pos).getBiomes());
                    for (int i = 0, biomesLength = biomes.length; i < biomesLength; i++) {
                        biomes[i] = biome;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                world.getChunkAt(pos).setUnsaved(true);
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
