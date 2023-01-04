package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.entities.EntityClayGolem;
import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayerFactory;
public class ChalkTile extends ModdedTile implements ITickable {

    public ChalkLineData data;

    public Entity touchedEntity;

    public boolean savesData = false;

    public ChalkTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.ChalkTileType.get(), pos, state);
        data = new ChalkLineData(new Spell());
    }

    public boolean SetData(ChalkLineData newData){
        if(data != newData) {
            data = newData;
            return true;
        }
        return false;
    }


    public void setSpell(Spell spell) {
        data.spell = spell;
    }

    public void setSpellColor(ParticleColor color) {
        data.color = color;
    }

    public boolean setCharges(int charges2){
        int oldc = data.charges;
        data.charges = Math.max(Math.min(charges2,15),0);

        this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChalkLineBlock.POWER,data.charges));
        return oldc!= data.charges;
    }

    public boolean tryAddCharges(int charges2,int maxCharges){
        int oldc = data.charges;
        data.charges += charges2;

        data.charges = Math.max(Math.min(data.charges,15),0);

        this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChalkLineBlock.POWER,data.charges));
        return oldc!= data.charges;
    }

    public boolean canCastSpell(){
        return data.spell!=null;
    }

    public boolean shouldHitEntity(Entity entity){
        return (data.owner == null || (entity != level.getPlayerByUUID(data.owner) && ( !(entity instanceof EntityClayGolem) || ((EntityClayGolem)entity).getOwner() != level.getPlayerByUUID(data.owner))));
    }

    public void castSpell(Entity entity) {
        if (entity != null) {
            if (data.charges > 0 && !data.spell.isEmpty() && this.level instanceof ServerLevel && data.CanHitEntity(entity,level.getGameTime())) {
                try {
                    Player playerEntity = data.owner != null ? this.level.getPlayerByUUID(data.owner) : FakePlayerFactory.getMinecraft((ServerLevel)this.level);
                    playerEntity = playerEntity == null ? FakePlayerFactory.getMinecraft((ServerLevel)this.level) : playerEntity;
                    EntitySpellResolver resolver = new EntitySpellResolver((new SpellContext(level,data.spell, (LivingEntity)playerEntity)).withCastingTile(this).withType(SpellContext.CasterType.RUNE).withColors(data.color));
                    resolver.onCastOnEntity(ItemStack.EMPTY, entity, InteractionHand.MAIN_HAND);

                    data.charges-=1;

                    //keeping just in case but probably won't ever self destruct
                    if (false) {
                        this.level.destroyBlock(this.worldPosition, false);
                        return;
                    }
                    int maxCharges = 15;
                    //clamp
                    data.charges = Math.max(Math.min(data.charges,maxCharges),0);

                    data.AddEntity(entity,level.getGameTime());

                    this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChalkLineBlock.POWER,data.charges));
                } catch (Exception var4) {
                    PortUtil.sendMessage(entity, Component.literal("Chalk line error, please report on the Ars Omega github!"));
                    var4.printStackTrace();
                    this.level.destroyBlock(this.worldPosition, false);
                }

            }
        }
    }

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(savesData) {
            data.Serialize(tag);
            ArsOmega.LOGGER.info("saved at pos: " + this.worldPosition);
        }
        tag.putBoolean("savesData",savesData);

    }

    public void saveClientData(CompoundTag tag) {
        super.saveAdditional(tag);
        data.Serialize(tag);
        ArsOmega.LOGGER.info("saved client data at pos: " + this.worldPosition);
        tag.putBoolean("savesData",savesData);

    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveClientData(tag);
        return tag;
    }

    public void load(CompoundTag tag) {
        if(tag.getBoolean("savesData")) {
            savesData = true;
            data = new ChalkLineData(tag);
            if(this.level!=null&&ChalkLineBlock.basicValidChalk(this.level.getBlockState(this.worldPosition))) {
                this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChalkLineBlock.POWER, data.charges));
            }
            ArsOmega.LOGGER.info("loaded from pos: " + this.getBlockPos());
            ArsOmega.LOGGER.info("loaded charges:: " + data.charges);
        }
        else{
            savesData = false;
            data = new ChalkLineData(new Spell());
        }
        super.load(tag);
    }
}
