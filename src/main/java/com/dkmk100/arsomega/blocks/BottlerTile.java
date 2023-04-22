package com.dkmk100.arsomega.blocks;


import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BottlerTile extends ModdedTile implements ITickable, WorldlyContainer, IWandable {

    ItemStack input = ItemStack.EMPTY;
    ItemStack output = ItemStack.EMPTY;

    Player test = null;

    public BottlerTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        Direction[] var3 = Direction.values();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Direction d = var3[var5];
            if (d != Direction.DOWN) {
                BlockEntity tileEntity = this.level.getBlockEntity(this.worldPosition.relative(d));
                if (tileEntity != null && tileEntity instanceof PotionJarTile) {
                    //stop on successful bottle
                    if(tryBottle(level, state, pos, (PotionJarTile) tileEntity)){
                        return;
                    }
                }
            }
        }

        //now try unbottling
        for (int var5 = 0; var5 < var4; ++var5) {
            Direction d = var3[var5];
            if (d != Direction.UP) {
                BlockEntity tileEntity = this.level.getBlockEntity(this.worldPosition.relative(d));
                if (tileEntity != null && tileEntity instanceof PotionJarTile) {
                    //stop on successful unbottle
                    if(tryUnbottle(level, state, pos, (PotionJarTile) tileEntity)){
                        return;
                    }
                }
            }
        }
    }

    public BottlerTile(BlockPos pos, BlockState state) {
        super(RegistryHandler.PotionBottlerType.get(), pos, state);
    }

    private boolean tryBottle(Level level, BlockState state, BlockPos pos, PotionJarTile jar){
        if(jar.getAmount() < 100 || input.isEmpty() || !isBottle(input)){
            return false;
        }
        if(output.isEmpty() || (jar.getData().areSameEffects(new PotionData(output)) && output.getCount() < output.getMaxStackSize())){
            if(output.isEmpty()){
                output = new ItemStack(getPotionItem(input),1);
            }
            else{
                output.setCount(output.getCount() + 1);
            }
            PotionUtils.setPotion(output,jar.getData().getPotion());
            PotionUtils.setCustomEffects(output,jar.getData().getCustomEffects());
            jar.remove(100);
            input.shrink(1);
            return true;
        }
        return false;
    }

    private boolean tryUnbottle(Level level, BlockState state, BlockPos pos, PotionJarTile jar){
        if(jar.getAmount() + 100 > jar.getMaxFill() || input.isEmpty() || !isPotion(input)){
            return false;
        }
        if(output.isEmpty() || (output.getItem() == getBottleItem(input) && output.getCount() < output.getMaxStackSize())){
            if(output.isEmpty()){
                output = new ItemStack(getBottleItem(input),1);
            }
            else{
                output.setCount(output.getCount() + 1);
            }
            PotionData data = new PotionData(input);
            jar.add(data,100);
            input.shrink(1);
            return true;
        }
        return false;
    }

    public static boolean isBottle(ItemStack stack){
        Item item = stack.getItem();
        return item == Items.GLASS_BOTTLE || item == RegistryHandler.SPLASH_BOTTLE.get() || item == RegistryHandler.LINGERING_BOTTLE.get();
    }
    public static boolean isPotion(ItemStack stack){
        return stack.getItem() == Items.POTION || stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION;
    }

    public static Item getPotionItem(ItemStack input){
        Item item = input.getItem();
        if(item == RegistryHandler.SPLASH_BOTTLE.get()){
            return Items.SPLASH_POTION;
        }
        else if(item == RegistryHandler.LINGERING_BOTTLE.get()){
            return Items.LINGERING_POTION;
        }
        return Items.POTION;
    }

    public static Item getBottleItem(ItemStack input){
        Item item = input.getItem();

        if(item == Items.SPLASH_POTION){
            return RegistryHandler.SPLASH_BOTTLE.get();
        }
        else if(item == Items.LINGERING_POTION){
            return RegistryHandler.LINGERING_BOTTLE.get();
        }
        return Items.GLASS_BOTTLE;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return input.isEmpty() && output.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if(slot==0){
            return input;
        }
        return output;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = this.getItem(slot);
        ItemStack toReturn = stack.copy().split(amount);
        stack.shrink(1);
        this.updateBlock();
        return toReturn;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if(slot==0){
            ItemStack stack = input;
            if(stack.isEmpty()){
                return ItemStack.EMPTY;
            }
            else {
                input = ItemStack.EMPTY;
                return stack;
            }
        }
        ItemStack stack = output;
        if(stack.isEmpty()){
            return ItemStack.EMPTY;
        }
        else {
            output = ItemStack.EMPTY;
            return stack;
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemstack = slot == 0 ? input : output;
        boolean flag = !itemstack.isEmpty() && itemstack.sameItem(itemstack) && ItemStack.tagMatches(itemstack, itemstack);
        if(slot == 0){
            input = stack;
        }
        else{
            output = stack;
        }
        if(!flag){
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player p_58340_) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return p_58340_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.input = ItemStack.EMPTY;
        this.output = ItemStack.EMPTY;
    }

    @Override
    public void onWanded(Player playerEntity) {
        test = playerEntity;
        if(!input.isEmpty()) {
            ItemEntity ent = new ItemEntity(playerEntity.getLevel(),playerEntity.getX(),playerEntity.getY(),playerEntity.getZ(),input);
            playerEntity.getLevel().addFreshEntity(ent);
            input = ItemStack.EMPTY;
        }
        IWandable.super.onWanded(playerEntity);
    }

    public void load(CompoundTag compound) {
        super.load(compound);
        this.input = compound.contains("inStack") ? ItemStack.of((CompoundTag)compound.get("inStack")) : ItemStack.EMPTY;
        this.output = compound.contains("outStack") ? ItemStack.of((CompoundTag)compound.get("outStack")) : ItemStack.EMPTY;
    }

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.input != null) {
            CompoundTag reagentTag = new CompoundTag();
            this.input.save(reagentTag);
            tag.put("inStack", reagentTag);
        }
        if (this.output != null) {
            CompoundTag reagentTag = new CompoundTag();
            this.output.save(reagentTag);
            tag.put("outStack", reagentTag);
        }

    }

    @Override
    public int[] getSlotsForFace(Direction dir) {
        if(dir == Direction.DOWN) {
            return new int[]{1,0};
        }
        else{
            return new int[]{0};
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && (input == null || stack == input || input.isEmpty()) && (isBottle(stack) || isPotion(stack));
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack p_19240_, Direction dir) {
        //remove non bottles to prevent issues
        return slot > 0 || (slot == 0 && !isBottle(input) && !isPotion(input));
    }
}
