package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.util.ReflectionHandler;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class ChalkTile extends ModdedTile implements ITickable {

    public Spell spell;
    public int charges;
    public UUID uuid;
    public ParticleColor color;

    long lastHit = 0;

    final int hitDelay = 11;

    public Entity touchedEntity;

    public ChalkTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.ChalkTileType.get(), pos, state);
        this.spell = new Spell();
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    public void setSpellColor(ParticleColor color) {
        this.color = color;
    }

    public boolean setCharges(int charges2){
        int oldc = this.charges;
        this.charges = charges2;

        this.charges = Math.max(Math.min(this.charges,15),0);

        this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChalkLineBlock.POWER,charges));
        return oldc!=this.charges;
    }

    public boolean tryAddCharges(int charges2,int maxCharges){
        int oldc = this.charges;
        this.charges += charges2;

        this.charges = Math.max(Math.min(this.charges,Math.min(maxCharges,15)),0);

        this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChalkLineBlock.POWER,charges));
        return oldc!=this.charges;
    }

    public boolean canCastSpell(){
        return spell!=null && this.level.getGameTime() - hitDelay > lastHit;
    }

    public boolean shouldHitEntity(Entity entity){
        return uuid == null || entity.getUUID() != uuid;
    }

    public void castSpell(Entity entity) {
        if (entity != null) {
            if (this.charges > 0 && !this.spell.isEmpty() && this.level instanceof ServerLevel && this.spell.recipe.get(0) instanceof MethodTouch) {
                try {
                    Player playerEntity = this.uuid != null ? this.level.getPlayerByUUID(this.uuid) : FakePlayerFactory.getMinecraft((ServerLevel)this.level);
                    playerEntity = playerEntity == null ? FakePlayerFactory.getMinecraft((ServerLevel)this.level) : playerEntity;
                    EntitySpellResolver resolver = new EntitySpellResolver((new SpellContext(this.spell, (LivingEntity)playerEntity)).withCastingTile(this).withType(SpellContext.CasterType.RUNE).withColors(this.color.toWrapper()));
                    resolver.onCastOnEntity(ItemStack.EMPTY, (LivingEntity)playerEntity, entity, InteractionHand.MAIN_HAND);

                    charges-=1;

                    //keeping just in case but probably won't ever self destruct
                    if (false) {
                        this.level.destroyBlock(this.worldPosition, false);
                        return;
                    }
                    int maxCharges = 15;
                    //clamp
                    charges = Math.max(Math.min(charges,maxCharges),0);
                    lastHit = this.level.getGameTime();
                    this.level.setBlockAndUpdate(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChalkLineBlock.POWER,charges));
                } catch (Exception var4) {
                    PortUtil.sendMessage(entity, new TranslatableComponent("ars_nouveau.rune.error"));
                    var4.printStackTrace();
                    this.level.destroyBlock(this.worldPosition, false);
                }

            }
        }
    }

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("spell", this.spell.serialize());
        tag.putInt("charges", this.charges);
        if (this.uuid != null) {
            tag.putUUID("uuid", this.uuid);
        }

        if (this.color != null) {
            tag.putString("color", this.color.toWrapper().serialize());
        }

    }

    public void load(CompoundTag tag) {
        this.spell = Spell.deserialize(tag.getString("spell"));
        this.charges = tag.getInt("charges");
        if (tag.contains("uuid")) {
            this.uuid = tag.getUUID("uuid");
        }

        this.color = ParticleColor.IntWrapper.deserialize(tag.getString("color")).toParticleColor();
        super.load(tag);
    }


}
