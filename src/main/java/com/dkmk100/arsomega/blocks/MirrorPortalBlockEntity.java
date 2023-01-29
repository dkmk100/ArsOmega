package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.ItemsRegistry;
import com.dkmk100.arsomega.items.CelestialStaff;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.core.jmx.Server;

import javax.annotation.Nullable;
import java.util.List;

/*

NOTE: this file contains major spoilers for mod content and story
just so you're aware

 */

public class MirrorPortalBlockEntity extends ModdedTile implements ITooltipProvider {

    public int shards = 0;
    boolean active = false;

    boolean hasInteracted = false;

    static ItemRequest[] requestOptions = {
            new ItemRequest(ItemsRegistry.INFUSED_DIAMOND, 128),
            new ItemRequest(ItemsRegistry.GORGON_GEM, 8),
            new ItemRequest(ItemsRegistry.DEMONIC_GEM, 64),
            new ItemRequest(ItemsRegistry.ARCANE_CLAY, 4),
            new ItemRequest(ItemsRegistry.ARCANE_ESSENCE, 32),
    };
    static final int requestsNeeded = 4;
    static final int powersRequests = 1;

    int requestsFilled = 0;
    int currentRequest = 0;
    int currentProgress = 0;

    int powerAbsorbed = 0;
    boolean hasRequest = false;


    protected static class ItemRequest{
        public ItemRequest(Item item, int amount){
            this.requestedItem = item;
            this.amount = amount;
        }
        public final Item requestedItem;
        public final int amount;
    }

    public MirrorPortalBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public MirrorPortalBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(RegistryHandler.MirrorPortalType.get(), p_155229_, p_155230_);
    }


    @Nullable
    public ItemEntity spawnAtLocation(ItemStack p_70099_1_, BlockPos pos) {
        if (p_70099_1_.isEmpty()) {
            return null;
        } else if (getLevel().isClientSide) {
            return null;
        } else {
            ItemEntity itementity = new ItemEntity(this.getLevel(), pos.getX(),pos.getY(),pos.getZ(), p_70099_1_);
            itementity.setDefaultPickUpDelay();
            this.getLevel().addFreshEntity(itementity);
            return itementity;
        }
    }
    public InteractionResult OnRightClick(ItemStack stack, ServerPlayer player, InteractionHand hand){
        if(active){
            RegistryHandler.RESTORATION.Trigger(player);
            if(hasInteracted){
                RegistryHandler.CONTACT.Trigger(player);
                if(requestsFilled >= powersRequests) {
                    RegistryHandler.POWERS.Trigger(player);
                }
            }
            if(powerAbsorbed > 200000){
                TellNearby("<?> I don't need anymore help at the moment.");
            }
            else if(stack.getItem() == ItemsRegistry.CELESTIAL_STAFF){
                if(powerAbsorbed > 200000){
                    TellNearby("<?> Thank you! I've absorbed all the power I want. " +
                            "Feel free to use the Celestial Staff to absorb more demonic energy, but I have no need of it. " +
                            "Do not fear, the staff can contain a fair bit of energy safely. Just don't hand it to anyone you don't trust.");
                    return InteractionResult.SUCCESS;
                }
                else if(CelestialStaff.getPower(stack) == 0){
                    TellNearby("<?> The demon realm is full of dangerous energy, you can remove some of it by using this staff. (right click in the air)");
                    return InteractionResult.SUCCESS;
                }
                powerAbsorbed += CelestialStaff.getPower(stack);
                CelestialStaff.setPower(stack,0);
                if(powerAbsorbed > 200000){
                    TellNearby("<?> Thank you! I've absorbed all the power I want. " +
                            "Feel free to use the Celestial Staff to absorb more demonic energy, but I have no need of it. " +
                            "Do not fear, the staff can contain a fair bit of energy safely. Just don't hand it to anyone you don't trust.");
                }
                else{
                    TellNearby("<?> Thank you! Keep collecting more demonic energy, and return it to me for safekeeping. It can be dangerous in the wrong hands.");
                }
                return InteractionResult.SUCCESS;
            }
            else if(requestsFilled >= requestsNeeded){
                if(stack.getItem() == ItemsRegistry.ARCANE_STAFF){
                    RegistryHandler.DESTINY.Trigger(player);
                    player.setItemInHand(hand,new ItemStack(ItemsRegistry.CELESTIAL_STAFF));
                    TellNearby("<?> Here is the Celestial Staff. Good luck on your quest, and may the stars guide you. ");
                    TellNearby("<?> If you succeed, please return with the energy you have absorbed so I can lock it away. It could be dangerous in the wrong hands. ");
                }
                else {
                    RegistryHandler.DESTINY.Trigger(player);
                    TellNearby("<?> I have one final request that I believe can benefit us both. ");
                    TellNearby("<?> The demon realm you currently reside in, it is so dangerous. The very energy of the world is toxic to life. " +
                            "But I've found a way to fix it, to absorb that energy and lock it away safely. " +
                            "If you give me an arcane staff, I shall entrust you with the Celestial Staff, " +
                            "which you could use to absorb this energy.");
                }
                return InteractionResult.SUCCESS;
            }
            else if(hasInteracted){
                if(hasRequest){
                    Item item =  requestOptions[currentRequest].requestedItem;
                    int missing = requestOptions[currentRequest].amount - currentProgress;
                    TellNearby("<?> I still need " + missing + " more "+item.getName(item.getDefaultInstance()).getString(), true);
                }
                else{
                    currentRequest = level.random.nextInt(requestOptions.length);
                    currentProgress = 0;
                    hasRequest = true;
                    Item item =  requestOptions[currentRequest].requestedItem;
                    int missing = requestOptions[currentRequest].amount - currentProgress;
                    TellNearby("<?> I'd like to offer a deal. I find myself in need of " + missing + " " + item.getName(item.getDefaultInstance()).getString());
                    TellNearby("<?> If you provide the, I'll give you a powerful item in exchange.");
                }

                return InteractionResult.SUCCESS;
            }
            else{

            }
            return InteractionResult.PASS;
        }
        else{
            if(stack.getItem() == ItemsRegistry.ANCIENT_SHARD){
                shards+=1;
                if(shards>=8){
                    RegistryHandler.RESTORATION.Trigger(player);
                    active = true;
                }
                this.updateBlock();
                return InteractionResult.SUCCESS;
            }
            else if(stack.getItem() == ItemsRegistry.ENCHANTED_MIRROR_SHARD){
                TellNearby("The mirror shard doesn't seem to fit... maybe there's another kind somewhere?");
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }

    static ItemStack[] gifts = {
            new ItemStack(ItemsRegistry.ENCHANTED_MIRROR_SHARD,4),
            new ItemStack(ItemsRegistry.ENCHANTERS_WOOL_ITEM,2),
            new ItemStack(ItemsRegistry.ARCANE_CLOTH,2),
            new ItemStack(Items.WITHER_SKELETON_SKULL,8),
            new ItemStack(ItemsRegistry.ARCANE_APPLE,2),
            new ItemStack(Items.NETHER_STAR,1),
            new ItemStack(ItemsRegistry.SIGIL_BINDING_ACTIVE,8),
    };
    void SpawnGift(BlockPos pos){
        spawnAtLocation(gifts[level.random.nextInt(gifts.length)],pos);
    }
    public boolean OnTossItem(ItemStack stack){
        if(hasInteracted) {
            Player player = level.getNearestPlayer(TargetingConditions.forNonCombat(),getX(),getY(),getZ());
            RegistryHandler.CONTACT.Trigger((ServerPlayer)player);
            if(hasRequest){
                Item item =  requestOptions[currentRequest].requestedItem;
                if(stack.getItem() == item){
                    int missing = requestOptions[currentRequest].amount - currentProgress;
                    if(stack.getCount() >= missing) {
                        TellNearby("<?> Thank you, that's all the items I need for now!");
                        requestsFilled+=1;
                        hasRequest = false;
                        TellNearby("<?> Here is your reward. If you wish to trade again, I'm always interested. ");
                        if(requestsFilled >= powersRequests) {
                            RegistryHandler.POWERS.Trigger((ServerPlayer) player);
                        }
                        SpawnGift(player.blockPosition().above(3));
                    }
                    else{
                        currentProgress += stack.getCount();
                        missing = requestOptions[currentRequest].amount - currentProgress;
                        TellNearby("<?> Thanks, but I still need "+missing+" more "+item.getName(item.getDefaultInstance()).getString(), false);
                    }
                    return true;
                }
                else{
                    TellNearby("<?> I have no need of that item right now, can you get me some "+item.getName(item.getDefaultInstance()).getString(), true);
                    return false;
                }
            }
            else {
                TellNearby("<?> Currently I have no need of any items, but perhaps we can arrange a trade in the future?", true);
                return false;
            }
        }
        else{
            Player player = level.getNearestPlayer(TargetingConditions.forNonCombat(),getX(),getY(),getZ());
            RegistryHandler.CONTACT.Trigger((ServerPlayer)player);
            hasInteracted = true;
            TellNearby("<?> Thanks for the gift... it's been so long since I've received one!");
            return true;
        }
    }
    public void OnSpellReceived(EntityProjectileSpell spell){

    }

    public void TellNearby(String message){
        BlockPos test = this.getBlockPos().offset(-25,-15,-25);
        BlockPos test2 = this.getBlockPos().offset(25,15,25);
        List<Player> toTell = level.getNearbyPlayers(TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight(),null,new AABB(test,test2));
        for (Player player : toTell){
            PortUtil.sendMessage(player, new TextComponent(message));
        }
    }

    public void TellNearby(String message, boolean preventSpam){
        BlockPos test = this.getBlockPos().offset(-25,-15,-25);
        BlockPos test2 = this.getBlockPos().offset(25,15,25);
        List<Player> toTell = level.getNearbyPlayers(TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight(),null,new AABB(test,test2));
        for (Player player : toTell){
            if(preventSpam){
                PortUtil.sendMessageNoSpam(player, new TextComponent(message));
            }
            else {
                PortUtil.sendMessage(player, new TextComponent(message));
            }
        }
    }


    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("shards", shards);
        tag.putBoolean("active",active);
        tag.putBoolean("interacted",hasInteracted);
        tag.putInt("requests",requestsFilled);
        tag.putInt("currentRequest",currentRequest);
        tag.putInt("progress",currentProgress);
        tag.putInt("power",powerAbsorbed);
    }

    @Override
    public void load(CompoundTag tag) {
        if(tag.contains("shards")) {
            this.shards = tag.getInt("shards");
        }
        if(tag.contains("active")) {
            this.active = tag.getBoolean("active");
        }
        if(tag.contains("interacted")) {
            this.hasInteracted = tag.getBoolean("interacted");
        }
        if(tag.contains("requests")) {
            this.requestsFilled = tag.getInt("requests");
        }
        if(tag.contains("currentRequest")) {
            this.currentRequest = tag.getInt("currentRequest");
        }
        if(tag.contains("progress")) {
            this.currentProgress = tag.getInt("progress");
        }
        if(tag.contains("power")) {
            this.powerAbsorbed = tag.getInt("power");
        }
        super.load(tag);
    }

    @Override
    public void getTooltip(List<Component> list) {
        if(active){
            if(hasInteracted){
                list.add(new TextComponent("You can see dark colors swirling in the mirror."));
                list.add(new TextComponent("There's something on the other side of the portal, but can't see it."));
                list.add(new TextComponent("Right-click to communicate with the being."));
                list.add(new TextComponent("You can also toss in an item as an offering."));
            }
            else {
                list.add(new TextComponent("You can see dark colors swirling in the mirror."));
                list.add(new TextComponent("It appears to be a portal, but you can't seem to put your hand through..."));
                list.add(new TextComponent("Maybe try throwing in an item?"));
            }
        }
        else{
            if(shards == 0){
                list.add(new TextComponent("This is a mysterious broken mirror frame..."));
                list.add(new TextComponent("Maybe some kind of mirror shard fits here?"));
            }
            else {
                list.add(new TextComponent("This is a mysterious broken mirror frame..."));
                list.add(new TextComponent("It can fit " + (8 - shards) + " more ancient mirror shards."));

            }
        }
    }
}

