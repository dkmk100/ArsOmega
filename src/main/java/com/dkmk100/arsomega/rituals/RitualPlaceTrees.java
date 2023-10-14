package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.*;
import java.util.stream.Stream;

public class RitualPlaceTrees extends AbstractRitual {
    int maxPostionsToTry = 3;

    //move blockposition to the ground if possible
    static BlockPos adjustYToGround(BlockPos pos, Level level, int rangeUp, int rangeDown) {
        int x = pos.getX();
        int z = pos.getZ();
        int y = pos.getY() + rangeUp;
        boolean found = false;
        for (int i = 0; i < rangeUp + rangeDown; i++) {
            BlockState state = level.getBlockState(new BlockPos(x, y - i, z));
            if (!state.isAir() && !state.getMaterial().isReplaceable()) {
                y -= i;
                found = true;
                break;
            }
        }
        if(!found){
            y = pos.getY();
            ArsOmega.LOGGER.warn("couldn't find ground for pos: "+pos);
        }
        //above the pos in the ground
        return new BlockPos(x, y+1, z);
    }

    static class RitualPartition{
        //x1 and z1 should be the low ones
        int x1;
        int z1;
        int x2;
        int z2;

        int featureCount = 0;
        int priorityCount = 0;

        boolean hasValidY = false;
        int centeredY;
        int rangeUp;
        int rangeDown;

        protected RitualPartition withY(int centeredY, int rangeUp, int rangeDown){
            this.centeredY = centeredY;
            this.rangeUp = rangeUp;
            this.rangeDown = rangeDown;
            hasValidY = true;
            return this;
        }
        protected RitualPartition withFeatures(int featureCount, int priorityCount){
            this.featureCount = featureCount;
            this.priorityCount = priorityCount;
            return this;
        }
        protected RitualPartition withY(int centeredY){
            this.centeredY = centeredY;
            this.rangeUp = 5;
            this.rangeDown = 5;
            hasValidY = true;
            return this;
        }
        public RitualPartition(CompoundTag tag){
            this(tag.getInt("x1"),tag.getInt("x2"),tag.getInt("z1"),tag.getInt("z2"));
            this.featureCount = tag.getInt("featureCount");
            this.priorityCount = tag.getInt("priorityCount");
            if(tag.getBoolean("hasY")){
                hasValidY = true;
                centeredY = tag.getInt("y");
                rangeUp = tag.getInt("rangeUp");
                rangeDown = tag.getInt("rangeDown");
            }
        }
        public RitualPartition(int x1, int x2, int z1, int z2){
            if(x1 > x2){
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if(z1 > z2){
                int temp = z1;
                z1 = z2;
                z2 = temp;
            }

            this.x1 = x1;
            this.x2 = x2;
            this.z1 = z1;
            this.z2 = z2;
        }

        public RitualPartition(int x1, int x2, PartitionValues zVals){
            this(x1, x2, zVals.val1, zVals.val2);
        }

        public RitualPartition(PartitionValues xVals, int z1, int z2){
           this(xVals.val1, xVals.val2, z1, z2);
        }

        public RitualPartition(PartitionValues xVals, PartitionValues zVals){
            this(xVals.val1, xVals.val2, zVals.val1, zVals.val2);
        }

        public BlockPos randomPosInRange(int y, RandomSource random){
            int x = random.nextIntBetweenInclusive(x1, x2);
            int z = random.nextIntBetweenInclusive(z1, z2);
            return new BlockPos(x,y,z);
        }

        public boolean hasValidY(){
            return hasValidY;
        }

        public BlockPos randomPosInRange(Level world){
            if(!hasValidY()){
                throw new IllegalStateException("partition has no valid y!");
            }

            int y = centeredY;
            int x = world.getRandom().nextIntBetweenInclusive(x1, x2);
            int z = world.getRandom().nextIntBetweenInclusive(z1, z2);

            return adjustYToGround(new BlockPos(x,y,z),world,rangeUp,rangeDown);
        }

        public boolean couldContain(BlockPos pos){
            int x = pos.getX();
            int z = pos.getZ();
            return x > x1 && x < x2 && z > z1 && z < z2;
        }

        public boolean hasPriorityLeft(){
            return priorityCount > 0;
        }
        public boolean hasFeaturesLeft(){
            return featureCount > 0;
        }
        public boolean hasPlacementsLeft(){
            return priorityCount > 0 || featureCount > 0;
        }

        public void decrementPriority(){
            priorityCount--;
        }
        public void decrementFeatures(){
            featureCount--;
        }
        public int getArea(){
            return (x2 - x1) * (z2 - z1);
        }

        @Override
        public String toString() {
            return "RitualPartition{" +
                    "x1=" + x1 +
                    ", z1=" + z1 +
                    ", x2=" + x2 +
                    ", z2=" + z2 +
                    ", area=" + getArea() +
                    ", featureCount=" + featureCount +
                    ", priorityCount=" + priorityCount +
                    ", centeredY=" + centeredY +
                    '}';
        }

        CompoundTag toNBT(){
            CompoundTag tag = new CompoundTag();
            tag.putInt("x1",x1);
            tag.putInt("x2",x2);
            tag.putInt("z1",z1);
            tag.putInt("z2",z2);

            tag.putInt("featureCount",featureCount);
            tag.putInt("priorityCount",priorityCount);

            if(hasValidY){
                tag.putBoolean("hasY",true);
                tag.putInt("y",centeredY);
                tag.putInt("rangeUp",rangeUp);
                tag.putInt("rangeDown",rangeDown);
            }
            else{
                tag.putBoolean("hasY",false);
            }

            return tag;
        }
        public static RitualPartition fromNBT(CompoundTag tag){
            return new RitualPartition(tag);
        }
    }

    record PartitionValues(int val1, int val2) {

    }

    private List<RitualPartition> partitionInRange(BlockPos center, int horizontalRange, int partitionWidth, int verticalRange, float featuresPerPartition, float priorityPerPartition, boolean forbidCenterPartition, RandomSource rand){
        ArrayList<RitualPartition> partitions = new ArrayList<>();
        int width = horizontalRange * 2 + 1;//range is blocks away from the block in the center

        int fullPartitionsCount = width / partitionWidth;
        int leftoverBlocks = width % partitionWidth;
        int cornerSize = leftoverBlocks / 2;

        //I have no idea how bad of an idea this is but I feel like the code's easier to follow
        //even though the performance probably sucks
        //it's not like this is performance critical as the result is cached lol
        ArrayList<PartitionValues> sharedPartitionInfo = new ArrayList<>();

        if(cornerSize > 0) {
            //the 2 corners on each edge
            sharedPartitionInfo.add(new PartitionValues(horizontalRange - cornerSize, horizontalRange));
            sharedPartitionInfo.add(new PartitionValues(-horizontalRange, -horizontalRange + cornerSize));
        }

        //the longer part of each edge
        for(int i=0;i<fullPartitionsCount;i++){
            //start on "left" edge of first partition and move right by i partitions
            //and that edge is the spot after the end of the corner partition
            int pos = (-horizontalRange + cornerSize + 1) + i * (partitionWidth+1);

            sharedPartitionInfo.add(new PartitionValues(pos, pos + partitionWidth));
        }

        int x = center.getX();
        int z = center.getZ();
        ArrayList<PartitionValues> xPartitions = new ArrayList<>();
        ArrayList<PartitionValues> zPartitions = new ArrayList<>();

        for(PartitionValues val : sharedPartitionInfo){
            xPartitions.add(new PartitionValues(x + val.val1, x + val.val2));
            zPartitions.add(new PartitionValues(z + val.val1, z + val.val2));
        }

        int fullArea = partitionWidth * partitionWidth;
        float featureDensity = featuresPerPartition / (float) fullArea;
        float priorityDensity = priorityPerPartition / (float) fullArea;

        for(PartitionValues xVal : xPartitions){
            for(PartitionValues zVal : zPartitions){
                RitualPartition partition = new RitualPartition(xVal,zVal).withY(center.getY(), verticalRange, verticalRange);

                int currentArea = partition.getArea();
                float featureCount = featureDensity * currentArea;
                float priorityCount = priorityDensity * currentArea;

                int finalFeatureCount = (int) Math.floor(featureCount);
                featureCount -= finalFeatureCount;
                finalFeatureCount += featureCount > rand.nextFloat() ? 1 : 0;

                int finalPriorityCount = (int) Math.floor(priorityCount);
                priorityCount -= finalPriorityCount;
                finalPriorityCount += priorityCount > rand.nextFloat() ? 1 : 0;

                partition = partition.withFeatures(finalFeatureCount, finalPriorityCount);


                if(forbidCenterPartition && partition.couldContain(center)){
                    ArsOmega.LOGGER.info("skipping partition: "+partition);
                }
                else{
                    partitions.add(partition);
                    ArsOmega.LOGGER.info("added partition: "+partition);
                }
            }
        }

        return partitions;
    }

    List<RitualPartition> cachedPartitions = null;
    int currentPartition = 0;

    boolean cachedValsValid = false;
    int cachedRange = 0;

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);

        if(cachedPartitions!=null){
            int partitionCount = cachedPartitions.size();
            tag.putInt("partitionCount",partitionCount);
            for(int i=0;i<partitionCount;i++){
                tag.put("partition_"+i,cachedPartitions.get(i).toNBT());
            }
            tag.putInt("currentPartition",currentPartition);
        }

        if(cachedValsValid){
            tag.putInt("range",cachedRange);
        }

    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if(tag.contains("partitionCount")){
            int partitionCount = tag.getInt("partitionCount");
            cachedPartitions = new ArrayList<>(partitionCount);
            for(int i=0;i<partitionCount;i++){
                cachedPartitions.add(RitualPartition.fromNBT(tag.getCompound("partition_"+i)));
            }
            currentPartition = tag.getInt("currentPartition");
        }

        if(tag.contains("range")){
            cachedRange = tag.getInt("range");
            cachedValsValid = true;
        }
    }

    @Override
    protected void tick() {
        Level level = getWorld();

        int delay = 20;
        if(cachedValsValid){
            if(cachedRange > 50){
                delay = 1;
            }
            if(cachedRange > 25){
                delay = 2;
            }
            if(cachedRange > 18){
                delay = 5;
            }
            else if(cachedRange > 12){
                delay = 10;
            }
        }
        if(level instanceof ServerLevel serverLevel && level.getGameTime() % delay == 0) {
            //inner deadzone radius and outer reach radius of the ritual
            int r1 = 4;
            int r2 = 6;

            float partitionFeatureDensity = 1.5f;
            float partitionPriorityDensity = 0.5f;

            int ry = 6;

            //yea no I have no idea why either
            r2 = r2 > r1 ? r2 : r1 + 2;
            int partitionSize = 2*r1 + 1;



            //this one at least is self evident but also unnecessary so like WTF
            if(partitionSize % 2 == 0){
                partitionSize +=1;
                ArsOmega.LOGGER.warn("invalid partition size in place tree ritual!");
            }

            //starting range
            if(!cachedValsValid) {
                int range = 2*r2 + 1;

                for(ItemStack stack : this.getConsumedItems()){
                    if(stack.is(ItemsRegistry.MANIPULATION_ESSENCE.get())){
                        range += 2;
                    }
                }

                cachedRange = range;
                cachedValsValid = true;
            }

            if(cachedPartitions == null){
                //get partitions we need to use
                cachedPartitions = partitionInRange(getPos(), cachedRange, partitionSize, ry, partitionFeatureDensity, partitionPriorityDensity, true, level.random);
                //shuffle so that
                Collections.shuffle(cachedPartitions);
                //init current partition so we can round robin
                currentPartition = 0;
            }

            this.incrementProgress();


            //get a partition
            RitualPartition selectedPartition = cachedPartitions.get(currentPartition);
            int startedPartition = currentPartition;

            //using partitions round robin style
            currentPartition++;
            if(currentPartition >= cachedPartitions.size()){
                currentPartition -= cachedPartitions.size();
            }

            //get next partition with features left
            while(!selectedPartition.hasPlacementsLeft()){
                if(currentPartition == startedPartition){
                    //went through all partitions and all are empty now
                    this.setFinished();
                    return;
                }
                selectedPartition = cachedPartitions.get(currentPartition);

                currentPartition++;
                if(currentPartition >= cachedPartitions.size()){
                    currentPartition -= cachedPartitions.size();
                }
            }

            var features = getTreeFeatures(level.getBiome(getPos()),level);

            boolean isPriority = selectedPartition.hasPriorityLeft();

            //try to place features in different spots before giving up
            for(int j = 0; j < maxPostionsToTry; j++) {

                //get decent random position
                BlockPos pos = selectedPartition.randomPosInRange(level);

                ArsOmega.LOGGER.debug("considering pos: "+pos);

                int featuresSize = features.size();
                if (featuresSize > 0) {
                    //try random feature, but try them all in order after that
                    int firstFeature = serverLevel.random.nextInt(featuresSize);
                    for (int i = 0; i < featuresSize; i++) {
                        var feature = features.get((i + firstFeature) % featuresSize);

                        var configured = feature.feature().get();

                        //prioritize certain features so they get placed at all
                        if(isPriority && !isPriorityFeature(configured) && j==0){
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

                                if(isPriority){
                                    selectedPartition.decrementPriority();
                                }
                                else{
                                    selectedPartition.decrementFeatures();
                                }

                                return;
                            }

                        }
                    }
                }

            }

            ArsOmega.LOGGER.debug("incrementing progress after failed search for feature");
            if(isPriority){
                selectedPartition.decrementPriority();
            }
            else{
                selectedPartition.decrementFeatures();
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
