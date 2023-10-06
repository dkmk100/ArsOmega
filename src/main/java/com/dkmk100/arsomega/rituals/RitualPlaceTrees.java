package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.*;
import java.util.stream.Stream;

public class RitualPlaceTrees extends AbstractRitual {
    int baseFeatureCount= 5;

    int basePriorityCount = 1;

    int maxPostionsToTry = 3;

    static int randDistInRange(int min, int max, RandomSource rand){
        int magnitude = rand.nextInt(min,max);
        return magnitude * (rand.nextBoolean() ? 1 : -1);
    }

    //get random position at least a certain distance away on each axis
    static BlockPos randPosInRange(BlockPos center, int minDistHoriz, int maxDistHoriz, RandomSource rand){
        return new BlockPos(center.getX() + randDistInRange(minDistHoriz,maxDistHoriz,rand),center.getY(), center.getZ() + randDistInRange(minDistHoriz,maxDistHoriz,rand));
    }

    //move blockposition to the ground if possible
    static BlockPos adjustYToGround(BlockPos pos, Level level) {
        int x = pos.getX();
        int z = pos.getZ();
        int y = pos.getY() + 10;
        for (int i = 0; i < 20; i++) {
            BlockState state = level.getBlockState(new BlockPos(x, y - i, z));
            if (!state.isAir() && !state.getMaterial().isReplaceable()) {
                y -= i;
                break;
            }
        }
        //above the pos in the ground
        return new BlockPos(x, y+1, z);
    }

    static int getLegalArea(int minRange, int maxRange){
        //calculate blacklisted area in the center
        int blacklistArea = (minRange*2 + 1);
        blacklistArea *= blacklistArea;

        //calculate area of outer square
        int area = (maxRange*2 + 1);
        area *= area;

        //subtract inner square
        area -= blacklistArea;
        return area;
    }

    static float calculateFeaturesPerBlock(int featureCount, int minRange, int maxRange){
        return ((float) featureCount) / ((float) getLegalArea(minRange,maxRange));
    }

    static int calculateNewFeatureCount(float featuresPerBlock, int minRange, int maxRange, float finalPower, int baseCount){
        return (int) Math.round(Math.pow(featuresPerBlock * getLegalArea(minRange, maxRange) - baseCount,finalPower)) + baseCount;
    }

    @Override
    protected void tick() {
        Level level = getWorld();

        //2 per second seems fast enough
        if(level instanceof ServerLevel serverLevel && level.getGameTime() % 10 == 0) {



            int featureCount = baseFeatureCount;
            int priorityCount = basePriorityCount;
            int minRange = 3;
            int maxRange = 7;

            float featuresPerBlock = calculateFeaturesPerBlock(featureCount, minRange, maxRange);
            float priorityPerBlock = calculateFeaturesPerBlock(priorityCount, minRange, maxRange);

            for(ItemStack stack : getConsumedItems()){
                if(stack.is(ItemsRegistry.MANIPULATION_ESSENCE.get())){
                    maxRange += 1;
                }
            }

            //get feature count slightly reduced on large areas
            featureCount = calculateNewFeatureCount(featuresPerBlock, minRange, maxRange,0.9f, baseFeatureCount);

            //reduce priority count a lot since it's just to ensure you get at least 1 tree mostly
            priorityCount = calculateNewFeatureCount(priorityPerBlock, minRange, maxRange,0.75f, basePriorityCount);

            ArsOmega.LOGGER.info("new feature count: "+featureCount + ", new priority count: "+priorityCount);


            var features = getTreeFeatures(level.getBiome(getPos()), level);

            int minDist = minRange;
            int maxDist = maxRange;

            //try to place features in different spots before giving up
            for(int j = 0; j < maxPostionsToTry; j++) {
                ArsOmega.LOGGER.debug("looking for feature candidate");
                ArsOmega.LOGGER.debug("iterations so far: "+j);
                //get decent random position
                BlockPos pos = adjustYToGround(randPosInRange(getPos(), minDist, maxDist, level.random), level);

                int featuresSize = features.size();
                if (featuresSize > 0) {
                    //try random feature, but try them all in order after that
                    int firstFeature = serverLevel.random.nextInt(featuresSize);
                    for (int i = 0; i < featuresSize; i++) {
                        boolean shouldTryToPlace = true;
                        var feature = features.get((i + firstFeature) % featuresSize);

                        var configured = feature.feature().get();

                        //prioritize certain features so they get placed at all
                        if(getProgress() < priorityCount && !isPriorityFeature(configured) && j==0){
                            continue;
                        }

                        //do my best to skip grass cause it's boring
                        if(shouldSkipFeature(configured)){
                            continue;
                        }

                        //process position to place feature
                        //mostly for filters
                        Stream<BlockPos> stream = Stream.of(pos);
                        PlacementContext context = new PlacementContext(serverLevel,serverLevel.getChunkSource().getGenerator(),Optional.empty());
                        for (PlacementModifier placementmodifier : feature.placement()) {
                            stream = stream.flatMap((pos2) -> getPositionsFromModifierCustom(placementmodifier, context, level.random, pos2));
                        }

                        //actually try and place feature at positions
                        var positions = stream.toList();
                        for(BlockPos finalPos : positions){
                            boolean placed = configured.place(serverLevel,
                                    serverLevel.getChunkSource().getGenerator(), serverLevel.random, finalPos);
                            //when a feature is placed, finish this step of the ritual
                            if (placed) {
                                ArsOmega.LOGGER.debug("feature chosen: "+configured);
                                ArsOmega.LOGGER.debug("was priority? "+isPriorityFeature(configured));
                                this.incrementProgress();
                                return;
                            }
                        }
                    }
                }
            }
            ArsOmega.LOGGER.debug("incrementing progress after failed search for feature");
            //still increment progress so the ritual doesn't run forever if it can't place a feature
            this.incrementProgress();

            if(this.getProgress() > featureCount) {
                this.setFinished();
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.is(ItemsRegistry.MANIPULATION_ESSENCE.get());
    }

    static Stream<BlockPos> getPositionsFromModifierCustom(PlacementModifier modifier, PlacementContext context, RandomSource rand, BlockPos pos) {
        //we're not doing real worldgen so a few need to be skipped for various reasons
        if (shouldSkipModifier(modifier)) {
            return Stream.of(pos);
        }
        var baseStream = modifier.getPositions(context, rand, pos);

        var baseStreamCopy = modifier.getPositions(context, rand, pos);

        //if it's not a filter, don't let it kill the position pls thanks
        return (baseStreamCopy.count() > 0 || isFilter(modifier)) ? baseStream : Stream.of(pos);
    }

    static Dictionary<ConfiguredFeature, Boolean> priorityDict = new Hashtable<>();
    //avoid possibly laggy recursive hell as much as possible
    static boolean isPriorityFeature(ConfiguredFeature configured){
        Boolean val = priorityDict.get(configured);
        if(val == null) {
            return isPriorityFeatureRaw(configured.feature()) || isPriorityConfigRaw(configured.config());
        }
        return val;
    }

    //uses method above so indirectly uses the dict.
    static boolean isPriorityFeature(PlacedFeature placedFeature) {
        return isPriorityFeature(placedFeature.feature().get());
    }

    static boolean isPriorityFeatureRaw(ConfiguredFeature configured){
        return isPriorityFeatureRaw(configured.feature()) || isPriorityConfigRaw(configured.config());
    }

    //yes, this whole mess is recursive. shudder.
    //am I sure I don't hate myself and my mod's users?
    //not anymore
    static boolean isPriorityFeatureRaw(Feature f){
        return f instanceof TreeFeature || f instanceof HugeBrownMushroomFeature || f instanceof HugeRedMushroomFeature || f instanceof HugeFungusFeature;
    }

    //yes, this whole mess is recursive. shudder.
    //am I sure I don't hate myself and my mod's users?
    //not anymore
    static boolean isPriorityConfigRaw(FeatureConfiguration config) {
        //could I somehow use reflection for anything in here?
        //to make it work better with other mods and such

        boolean isPriority = false;
        isPriority |= (config instanceof SimpleRandomFeatureConfiguration simple && simple.features.stream().anyMatch((feature) -> isPriorityFeature(feature.get())));
        isPriority |= (config instanceof RandomFeatureConfiguration simple && simple.features.stream().anyMatch((feature) -> isPriorityFeature(feature.feature.get())));
        isPriority |= (config instanceof VegetationPatchConfiguration simple && simple.getFeatures().anyMatch((feature) -> isPriorityFeature(feature)));
        isPriority |= (config instanceof RandomPatchConfiguration simple && simple.getFeatures().anyMatch((feature) -> isPriorityFeature(feature)));
        return isPriority;
    }

    //yes, this whole mess is recursive. shudder.
    static boolean shouldSkipFeature(ConfiguredFeature configured){
        return configured.feature().toString().contains("grass");
    }

    //yes, this whole mess is recursive. shudder.
    static boolean shouldSkipModifier(PlacementModifier modifier) {
        //skipping non filters for now to avoid issues
        return !isFilter(modifier) || (modifier instanceof HeightRangePlacement || modifier instanceof EnvironmentScanPlacement
                || modifier instanceof CarvingMaskPlacement || modifier instanceof BiomeFilter);
    }

    //yes, this whole mess is recursive. shudder.
    static boolean isFilter(PlacementModifier modifier) {
        return modifier instanceof PlacementFilter;
    }


    @Override
    public ResourceLocation getRegistryName() {
        return RegistryHandler.getRitualName("place_trees");
    }

    static Dictionary<ResourceLocation, List<PlacedFeature>> cachedTreeFeatures = new Hashtable<>();

    public static List<PlacedFeature> getTreeFeatures(Holder<Biome> biomeHolder, Level world){
        Optional<? extends Registry<Biome>> registryOptional = world.registryAccess().registry(Registry.BIOME_REGISTRY);

        if (registryOptional.isEmpty()) {
            return List.of();
        }
        Registry<Biome> registry = registryOptional.get();
        ResourceLocation resourceLocation = registry.getKey(biomeHolder.value());

        List<PlacedFeature> featuresOut = cachedTreeFeatures.get(resourceLocation);
        if(featuresOut != null){
            return featuresOut;
        }
        else{
            featuresOut = new ArrayList<>();
        }

        List<HolderSet<PlacedFeature>> featuresIn = biomeHolder.get().getGenerationSettings().features();

        int step = 0;
        for(var set : featuresIn){
            var iterator = set.stream().iterator();
            while(iterator.hasNext()) {
                var feature = iterator.next().get();
                int finalStep = step;
                var configured = feature.feature().get();
                var f = configured.feature();
                if (step == 9 || isPriorityFeature(configured)) {
                    ArsOmega.LOGGER.info("tree feature found in configured:" + configured.toString());
                    featuresOut.add(feature);
                }
            }
            step +=1;
        }

        cachedTreeFeatures.put(resourceLocation, featuresOut);
        return featuresOut;
    }
}
