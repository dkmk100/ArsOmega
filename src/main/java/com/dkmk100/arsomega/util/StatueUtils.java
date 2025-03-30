package com.dkmk100.arsomega.util;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.client.StatueClientUtils;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.types.templates.CompoundList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StatueUtils {

    public static ItemStack CreateStatueItem(Entity entity){
        ItemStack stack = new ItemStack(RegistryHandler.STATUE_ITEM.get());

        CompoundTag blockTag = new CompoundTag();

        CompoundTag entityTag = new CompoundTag();
        //use custom save function to fix issues with player
        saveEntity(entity,entityTag);
        blockTag.put("entity",entityTag);

        String entityBackupId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        blockTag.putString("entity_backup_id",entityBackupId);

        CompoundTag tag = stack.getOrCreateTag();

        tag.put("BlockEntityTag", blockTag);

        return stack;
    }

    protected static ListTag newDoubleList(double... p_20064_) {
        ListTag listtag = new ListTag();
        double[] var3 = p_20064_;
        int var4 = p_20064_.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            double d0 = var3[var5];
            listtag.add(DoubleTag.valueOf(d0));
        }

        return listtag;
    }

    protected static ListTag newFloatList(float... p_20066_) {
        ListTag listtag = new ListTag();
        float[] var3 = p_20066_;
        int var4 = p_20066_.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            float f = var3[var5];
            listtag.add(FloatTag.valueOf(f));
        }

        return listtag;
    }

    public static void printRecursive(String str, Tag tag, int tier, int maxTier){
        String pre = "--".repeat(Math.max(0, tier));
        String type = tag.getType().getName();
        if(tag instanceof CompoundTag compound && tier < maxTier){
            ArsOmega.LOGGER.info("{}Tag ({}, {}): ", pre, str, type);
            for(String str2 : compound.getAllKeys()) {
                printRecursive(str2, compound.get(str2), tier + 1, maxTier);
            }
        }
        else if(tag instanceof ListTag list && tier < maxTier) {
            ArsOmega.LOGGER.info("{}Tag ({}, {}): ", pre, str, type);
            for(int i=0;i<list.size();i++){
                printRecursive("["+i+"]",list.get(i), tier + 1, maxTier);
            }
        }
        else {
            ArsOmega.LOGGER.info("{}{}, {}", pre, str, type);
        }
    }

    private static void saveBasics(LivingEntity entity, CompoundTag tag) {
        if (entity.getVehicle() != null) {
            tag.put("Pos", newDoubleList(entity.getVehicle().getX(), entity.getY(), entity.getVehicle().getZ()));
        } else {
            tag.put("Pos", newDoubleList(entity.getX(), entity.getY(), entity.getZ()));
        }

        Vec3 vec3 = entity.getDeltaMovement();
        tag.put("Motion", newDoubleList(vec3.x, vec3.y, vec3.z));
        tag.put("Rotation", newFloatList(entity.getYRot(), entity.getXRot()));
        tag.putFloat("Health", entity.getHealth());
        tag.putFloat("FallDistance", entity.fallDistance);
        tag.putShort("Fire", (short) entity.getRemainingFireTicks());
        tag.putUUID("UUID", entity.getUUID());
        Component component = entity.getCustomName();

        if (entity.hasGlowingTag()) {
            tag.putBoolean("Glowing", true);
        }

        //TODO: visual fire?
    }

    private static void saveInventoryMinimal(Inventory inv, ListTag tag){
        ArrayList<String> itemTagsKept = new ArrayList<>();
        //save item enchants for correct rendering
        itemTagsKept.add("Enchantments");

        inv.save(tag);
        for(int i=0;i<tag.size();i++){
            CompoundTag temp = tag.getCompound(i);
            //only save certain tags, to avoid saving too much data and causing a crash
            if(temp.contains("Tag")){
                CompoundTag itemTag = temp.getCompound("Tag");
                temp.remove("Tag");
                CompoundTag newItemTag = new CompoundTag();
                for(String s : itemTagsKept){
                    if(itemTag.contains(s)){
                        newItemTag.put(s, itemTag.get(s));
                    }
                }
                if(!newItemTag.getAllKeys().isEmpty()){
                    temp.put("Tag", newItemTag);
                }
            }
        }
    }

    //save necessary things for rendering, without
    private static void savePlayer(Player player, CompoundTag compound){
        compound.putString("id", player.getType().getDescriptionId());

        //save basic data
        saveBasics(player, compound);

        //save inventory
        ListTag invTag = new ListTag();
        Inventory playerInv = player.getInventory();
        saveInventoryMinimal(playerInv, invTag);
        compound.put("Inventory", invTag);
        compound.putInt("SelectedItemSlot", playerInv.selected);

        //save shoulder entities
        if (!player.getShoulderEntityLeft().isEmpty()) {
            compound.put("ShoulderEntityLeft", player.getShoulderEntityLeft());
        }
        if (!player.getShoulderEntityRight().isEmpty()) {
            compound.put("ShoulderEntityRight", player.getShoulderEntityRight());
        }

        //TODO save curios inventory
    }

    //fix some issues related to saving players
    public static void saveEntity(Entity entity, CompoundTag compound){
        if (entity instanceof Player player) {
            savePlayer(player, compound);
        } else {
            compound.putString("id", entity.getEncodeId());
            entity.saveWithoutId(compound);
        }
    }

    public static Entity CreateClientPlayer(StatuePlayerInfo info, Level level) {
        return StatueClientUtils.CreateClientPlayer(info,level);
    }

    public static class StatuePlayerInfo{
        public String name;

        public UUID uuid;

        public GameProfile profile;
        private StatuePlayerInfo(@NotNull String name, @NotNull UUID uuid, @NotNull CompoundTag profile){
            this.name = name;
            this.uuid = uuid;
            this.profile = NbtUtils.readGameProfile(profile);
        }

        private StatuePlayerInfo(@NotNull Player player){
            this.uuid = player.getUUID();
            this.name = player.getGameProfile().getName();
            this.profile = player.getGameProfile();
            //this.tag = new CompoundTag();
            //NbtUtils.writeGameProfile(this.tag,player.getGameProfile());
        }

        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            tag.putString("name", name);
            tag.putUUID("uuid", uuid);
            CompoundTag profileTag = new CompoundTag();
            NbtUtils.writeGameProfile(profileTag, profile);
            tag.put("profile", profileTag);

            return tag;
        }

        public static StatuePlayerInfo load(CompoundTag tag){
            if(tag != null && tag.contains("name") && tag.contains("uuid") && tag.contains("profile")){
                return new StatuePlayerInfo(tag.getString("name"),tag.getUUID("uuid"),tag.getCompound("profile"));
            }
            return null;
        }

        @Deprecated
        public @Nullable
        static StatuePlayerInfo of(@Nullable UUID uuid, @Nullable CompoundTag profile){
            if(uuid == null || profile == null){
                return null;
            }
            else{
                return new StatuePlayerInfo("player", uuid, profile);
            }
        }

        public @Nullable
        static StatuePlayerInfo of(@Nullable Player player){
            if(player == null){
                return null;
            }
            else{
                return new StatuePlayerInfo(player);
            }
        }

    }
}
