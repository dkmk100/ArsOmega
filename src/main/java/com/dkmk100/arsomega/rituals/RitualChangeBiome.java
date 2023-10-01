package com.dkmk100.arsomega.rituals;


import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.LevelUtil;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.RitualUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.Optional;

public class RitualChangeBiome extends AbstractRitual {

    Biome biome = null;


    boolean newBiomeIsNether = false;
    boolean newBiomeIsEnd = false;
    boolean newBiomeIsDemon = false;

    boolean inNether = false;
    boolean inEnd = false;
    boolean inDemon = false;

    boolean valsCached = false;

    protected void tick() {
        Level world = this.getWorld();
        BlockPos pos = this.getPos();

        //get biome
        //todo: cache ALL this BS so it doesn't lag while collecting source

        String biomeName = "minecraft:plains";
        boolean choseBiome = false;
        boolean canDoNether = false;
        int manipEssence = 0;
        for (ItemStack stack : this.getConsumedItems()) {
            if (!choseBiome && stack.getItem() == RegistryHandler.BIOME_CRYSTAL.get() && stack.hasTag() && stack.getTag().contains("biome")) {
                biomeName = stack.getTag().getString("biome");
                choseBiome = true;
            }
            if (stack.getItem() == RegistryHandler.DEMON_GEM.get()) {
                canDoNether = true;
            }
            if(stack.is(ItemsRegistry.MANIPULATION_ESSENCE.asItem())){
                manipEssence += 1;
            }
        }

        Optional<? extends Registry<Biome>> reg = world.registryAccess().registry(Registry.BIOME_REGISTRY);
        Registry<Biome> registry = reg.get();

        ResourceLocation loc = new ResourceLocation(biomeName);
        if (biome == null) {
            biome = registry.get(loc);
        }

        //fix for wrong biome name
        boolean canConvert = true;
        if (biome == null) {
            //biome = registry.get(new ResourceLocation("minecraft:plains"));
            ArsOmega.LOGGER.error("Missing biome: " + biomeName);
            LevelUtil.sendToNearby(world,pos, Component.literal("Biome crystal didn't contain valid biome"),10,5,false);
            setFinished();
            return;
        }

        var optional = registry.getResourceKey(registry.get(loc));
        if(optional.isEmpty()){
            this.getContext().progress = 0;
            LoggerContext.getContext().getLogger(this.getClass()).error("Couldn't fetch biome: "+loc);
            return;
        }
        ResourceKey<Biome> key = optional.get();

        Holder<Biome> holder = registry.getHolder(key).get();


        //bime color
        int biomeColor = 0;
        //Biome.BiomeCategory category = (Biome.BiomeCategory) ReflectionHandler.biomeCategory.get(biome);

        if(!valsCached) {
            if(loc.getPath().equals("demon_biome")){
                newBiomeIsDemon = true;
            }
            else if (biomeInTag(Tags.Biomes.IS_DRY_END, holder, registry) || biomeInTag(Tags.Biomes.IS_HOT_END, holder, registry)) {
                newBiomeIsEnd = true;
            }
            else if (biomeInTag(Tags.Biomes.IS_DRY_NETHER, holder, registry) || biomeInTag(Tags.Biomes.IS_HOT_NETHER, holder, registry)) {
                newBiomeIsNether = true;
            }

            var dimRegistry = world.registryAccess().registry(Registry.DIMENSION_TYPE_REGISTRY).get();
            String dimName = dimRegistry.getKey(world.dimensionType()).getPath();
            if (dimName.equals("the_nether")) {
                inNether = true;
            } else if (dimName.equals("the_end")) {
                inEnd = true;
            } else if (dimName.equals("demon_realm")) {
                inDemon = true;
            }

            valsCached = true;
        }

        if(inDemon){
            LevelUtil.sendToNearby(world,pos, Component.literal("Cannot convert biomes in demon realm, the dark energy is too strong! "),10,5,false);
            setFinished();
            return;
        }

        if(world.isClientSide){
            //particles
            if(newBiomeIsNether || newBiomeIsEnd){
                biomeColor = biome.getFogColor();
            }
            else{
                biomeColor = biome.getFoliageColor();
            }
        }
        else{
            //valid dimension logic
            if(newBiomeIsEnd != inEnd || newBiomeIsNether != inNether || newBiomeIsDemon != inDemon){
                if(!canDoNether){
                    LevelUtil.sendToNearby(world,pos, Component.literal("Cannot convert to biome from different dimension without the correct augment. Next time, try augmenting with a demon gem."),10,5,false);
                    setFinished();
                    return;
                }
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
                //widen the area a bit lol
                //manipulation essence adds more horizontal range
                final int sidewaysRange = 7 + 2*manipEssence;
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

    private static boolean biomeInTag(TagKey<Biome> tag, Holder<Biome> holder, Registry<Biome> registry){
        var optional = registry.getTag(tag);
        if(optional.isEmpty()){
            return false;
        }
        return optional.get().contains(holder);
    }

    @Override
    public int getSourceCost() {
        return 5;
    }
    @Override
    public ParticleColor getCenterColor() {
        //todo: fix this complete mess here


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
        return this.getConsumedItems().stream().anyMatch((stack) -> stack.is(RegistryHandler.BIOME_CRYSTAL.get()));
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        boolean consumedCrystal = this.getConsumedItems().stream().anyMatch((s) -> s.is(RegistryHandler.BIOME_CRYSTAL.get()));
        boolean consumedDemonGem = this.getConsumedItems().stream().anyMatch((s) -> s.is(RegistryHandler.DEMON_GEM.get()));

        if(!consumedCrystal && stack.is(RegistryHandler.BIOME_CRYSTAL.get()) && stack.hasTag()){
            return true;
        }
        else if(!consumedDemonGem && stack.is(RegistryHandler.DEMON_GEM.get())){
            return true;
        }
        else if(stack.is(ItemsRegistry.MANIPULATION_ESSENCE.asItem())){
            return true;
        }
        return false;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("change_biome");
    }
}