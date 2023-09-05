package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class StatueTile extends ModdedTile {

    @Nullable
    private Entity cachedEntity;
    private CompoundTag entityTag;
    private String entityBackupId;

    public StatueTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.StatueType.get(), pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        Entity entity = getEntity();
        if(entity != null){
            CompoundTag compound = new CompoundTag();
            entity.save(compound);
            tag.put("entity",compound);

            entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
            tag.putString("entity_backup_id",entityBackupId);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("entity")){
            entityTag = tag.getCompound("entity");

        }
        if(tag.contains("entity_backup_id")){
            entityBackupId = tag.getString("entity_backup_id");
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.cachedEntity = null;
        this.entityTag = null;
        super.onDataPacket(net, pkt);
    }


    public void setEntity(@Nullable Entity entity){
        if(entity == null){
            cachedEntity = null;
            entityTag = null;
            entityBackupId = null;
        }
        else{
            entityTag = new CompoundTag();
            entity.save(entityTag);
            cachedEntity = EntityType.loadEntityRecursive(entityTag,level,Function.identity());

            entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        }
    }

    public static Entity getEntity(@Nullable Entity cached, CompoundTag tag, String backupId, @Nullable Level level){
        if(cached != null){
            return cached;
        }

        if(tag != null){
            AtomicReference<Entity> entity = new AtomicReference<>(EntityType.loadEntityRecursive(tag, level, Function.identity()));
            if(entity.get() == null){
                if(backupId != null && level != null){
                    EntityType.byString(backupId).ifPresent((type) -> {
                        entity.set((Entity) type.create(level));
                    });
                }
            }
            cached = entity.get();
            return cached;
        }

        return null;
    }

    @Nullable
    public Entity getEntity() {
        Entity entity = getEntity(cachedEntity,entityTag,entityBackupId,getLevel());

        return entity;
    }
}
