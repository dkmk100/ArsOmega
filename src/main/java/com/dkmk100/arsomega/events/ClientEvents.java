package com.dkmk100.arsomega.events;

import com.dkmk100.arsomega.ArsOmega;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsOmega.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    /*
    @SubscribeEvent
    public static void magneticArmour(LivingEvent.LivingUpdateEvent event) {
        float radius = 0.75f;//starting range, only applies if there is magnetism
        float strength = 1f;//starting strength, currently always 1
        float speedMultiplier = 0.175f;
        LivingEntity entity = event.getEntityLiving();
        World world = entity.level;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;

            if (entity.getY() < -4f) {
                entity.addEffect(new EffectInstance(Effects.SLOW_FALLING, 600, 0));
                if (entity.getY() < -5f) {
                    RegistryKey<World> registrykey = RegistryHandler.voidDim;
                    if(registrykey==null) {
                        registrykey=RegistryKey.create(Registry.DIMENSION_REGISTRY, RegistryHandler.DIMTYPE);
                    }

                    ServerWorld voidDimension = world.getServer().getLevel(registrykey);
                    if(voidDimension==null){
                        throw new NullPointerException("WTF where'd the void dimension go");
                    }
                    int height = 78;
                    BlockPos pos = new BlockPos(entity.getPosition(0).x, height + 5, entity.getPosition(0).z);
                    teleportEntity(entity,pos,voidDimension,(ServerWorld) entity.level);
                    //teleportToDimension(entity,voidDimension,pos);
                }
            }

        }
        if (entity instanceof PlayerEntity || entity instanceof ArmorStandEntity) {
            Set<String> tags = entity.getTags();
            //magnetic items
            Iterable<ItemStack> heldItems = entity.getHandSlots();
            Iterable<ItemStack> armourItems = entity.getArmorSlots();
            boolean holdingMagnetic = false;
            boolean armourMagnetic = false;
            List<MagneticItem> magneticTools = RegistryHandler.magneticTools;
            List<MagneticItem> magneticArmors = RegistryHandler.magneticArmors;
            for (MagneticItem magneticItem : magneticTools) {
                for (ItemStack stack : heldItems) {
                    if (stack.getItem() == magneticItem.item && !holdingMagnetic) {
                        holdingMagnetic = true;
                        radius += magneticItem.range;
                    }
                }
            }
            for (MagneticItem magneticItem : magneticArmors) {
                for (ItemStack stack : armourItems) {
                    if (stack.getItem() == magneticItem.item) {
                        armourMagnetic = true;
                        radius += magneticItem.range;
                    }
                }
            }
            if (holdingMagnetic || armourMagnetic) {
                double entityX = entity.getX();
                double entityY = entity.getY() + 0.1;
                double entityZ = entity.getZ();
                Vector3d entityPos = new Vector3d(entityX, entityY, entityZ);
                float minRadius = 0.4f;
                BlockPos pos1 = new BlockPos(entityX - radius, entityY - radius, entityZ - radius);
                BlockPos pos2 = new BlockPos(entityX + radius, entityY + radius, entityZ + radius);
                List<ItemEntity> targets = world.getEntities(EntityType.ITEM, new AxisAlignedBB(pos1, pos2), EntityPredicates.NO_SPECTATORS);
                for (ItemEntity target : targets) {
                    Boolean demagnetized = false;
                    for (String tag : tags) {
                        if (tag.equals("PreventRemoteMovement")) {
                            demagnetized = true;
                        }
                    }
                    if (!demagnetized) {
                        double targetX = target.getX();
                        double targetY = target.getY();
                        double targetZ = target.getZ();
                        Vector3d targetPos = new Vector3d(targetX, targetY, targetZ);
                        double distance = Math.sqrt(Math.pow(Math.abs(target.getX() - entityX), 2) + Math.pow(Math.abs(target.getY() - entityY), 2) + Math.pow(Math.abs(target.getZ() - entityZ), 2));
                        if (distance > minRadius && radius > distance) {
                            speedMultiplier = speedMultiplier * strength;
                            if (distance > 0.55) {
                                speedMultiplier += (float) ((radius - distance) * 0.035);
                            }
                            Vector3d newPosVec = entityPos.subtract(targetPos);
                            Vector3d newPos = MathUtil.GetVecDir(newPosVec).multiply(new Vector3d(speedMultiplier, speedMultiplier, speedMultiplier));
                            target.setDeltaMovement(newPos.x, newPos.y, newPos.z);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void RightClickHandler(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        World world = event.getWorld();

        if (!world.isClientSide()) {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos pos = event.getPos();
            ItemStack stack = player.getItemInHand(event.getHand());
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block.equals(RegistryHandler.OVERWORLD_PORTAL_BROKEN.get())) {
                if (stack.getItem().equals(RegistryHandler.VOID_GEM.get())) {
                    stack.setCount(stack.getCount() - 1);
                    world.setBlockAndUpdate(pos, RegistryHandler.OVERWORLD_PORTAL.get().defaultBlockState());
                }
            } else if (block.equals(RegistryHandler.OVERWORLD_PORTAL.get()) && serverWorld.dimension().toString()==RegistryHandler.voidDim.toString()) {
                player.addEffect(new EffectInstance(Effects.SLOW_FALLING, 600, 0));
                teleportEntity(player, new BlockPos(pos.getX(), pos.getY() + 10, pos.getZ()), serverWorld.getServer().overworld(),(ServerWorld) player.level);
            } else if (block.equals(RegistryHandler.BOSS_SUMMONER.get())) {
                if (stack.getItem().equals(RegistryHandler.VOID_GEM.get())) {
                    stack.setCount(stack.getCount() - 1);
                    RegistryHandler.VOID_LICH.get().spawn(serverWorld, null, null, pos.offset(new BlockPos(0, 1, 0)), SpawnReason.EVENT, true, false);

                }
                else {
                    RegistryHandler.VOID_BOSS.get().spawn(serverWorld, null, null, pos.offset(new BlockPos(0, 1, 0)), SpawnReason.EVENT, true, false);
                }
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        ModSpawnEggItem.initSpawnEggs();
    }
    public static void teleportEntity(Entity entity, BlockPos destPos, ServerWorld destinationWorld, ServerWorld originalWorld) {

        // makes sure chunk is made
        destinationWorld.getChunk(destPos);

        if (entity instanceof PlayerEntity) {
            ((ServerPlayerEntity) entity).teleportTo(
                    destinationWorld,
                    destPos.getX() + 0.5D,
                    destPos.getY() + 1D,
                    destPos.getZ() + 0.5D,
                    entity.getRotationVector().y,
                    entity.getRotationVector().x);
        }
        else {
            //Entity entity2 = entity.getType().create(destinationWorld);
            Entity entity2 = EntityType.loadEntityRecursive(entity.serializeNBT(),destinationWorld,Function.identity());
            if (entity2 != null) {
                entity2.setPos(destPos.getX(),destPos.getY(),destPos.getZ());
                entity2.setDeltaMovement(entity.getDeltaMovement());
                destinationWorld.addFromAnotherDimension(entity2);
            }
            entity.remove();
            destinationWorld.getProfiler().endTick();
            //originalWorld.entity ;
            //destinationWorld.resetUpdateEntityTick();
            destinationWorld.getProfiler().endTick();
        }
    }
    /*
    public static void teleportToDimension(Entity entity, ServerWorld world, BlockPos pos){
        entity.changeDimension(world);
        //entity.setPos(pos.getX(),pos.getY(),pos.getZ());
    }
     //*/
    //*/
}
