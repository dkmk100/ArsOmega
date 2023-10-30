package com.dkmk100.arsomega.glyphs;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.compat.CompatHandler;
import com.dkmk100.arsomega.crafting.EnchantRecipe;
import com.dkmk100.arsomega.util.ExperienceUtil;
import com.dkmk100.arsomega.util.LevelUtil;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EnchantGlyph extends AbstractEffect {
    public static EnchantGlyph INSTANCE = new EnchantGlyph("enchant", "Enchant");

    public EnchantGlyph(String tag, String description) {
        super(RegistryHandler.getGlyphName(tag), description);
    }

    static RandomSource random = RandomSource.create();

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world instanceof ServerLevel) {
            double aoeBuff = spellStats.getAoeMultiplier();
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());

            WrappedEnchanter enchanter = new WrappedEnchanter(shooter, spellContext, world);
            if (enchanter.canEnchant()) {

                List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, (new AABB(rayTraceResult.getBlockPos())).inflate((double) aoeBuff + 1.0D));
                Iterator var5 = itemEntities.iterator();

                while (var5.hasNext()) {
                    ItemEntity itemEntity = (ItemEntity) var5.next();
                    ItemStack current = itemEntity.getItem();

                    EnchantResult result = enchantItem(enchanter, current, level(ampBuff), 1);

                    if (result.succeeded() && enchanter.spendExperience(result.experienceToSpend())) {
                        itemEntity.setItem(result.modifiedStack());
                        for (ItemStack stack : result.otherStacks()) {
                            LevelUtil.spawnAtLocation(stack, 0.5f, rayTraceResult.getBlockPos(), world);
                        }
                    }
                }
            }
        }

    }

    public ForgeConfigSpec.IntValue LEVELS_PER_AMP;
    public ForgeConfigSpec.IntValue BASE_LEVEL;
    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        LEVELS_PER_AMP = builder.defineInRange("levelsPerAmp", 2, 0, 100);
        BASE_LEVEL = builder.defineInRange("baseLevel", 4, 0, 100);
    }

    int level(int amp){
        return LEVELS_PER_AMP.get()*amp + BASE_LEVEL.get();
    }
    public int getAmp(int level){
        return (level-LEVELS_PER_AMP.get())/BASE_LEVEL.get();
    }
    int power(int level){
        return Math.min(Math.max(level,0), CompatHandler.getMaxEnchantLevel());
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(rayTraceResult.getEntity() instanceof LivingEntity target){
            int ampBuff = (int) Math.round(spellStats.getAmpMultiplier());

            WrappedEnchanter enchanter = new WrappedEnchanter(shooter, spellContext, world);
            if(enchanter.canEnchant()) {
                for(InteractionHand hand : InteractionHand.values()) {
                    if(ItemStack.matches(target.getItemInHand(hand),spellContext.getCasterTool())){
                        continue;//don't enchant casting item used to cast enchant spell
                    }
                    EnchantResult result = enchantItem(enchanter, target.getItemInHand(hand), level(ampBuff), 1);
                    //make sure spending the experience works before doing the enchants
                    if (result.succeeded && enchanter.spendExperience(result.experienceToSpend)) {
                        target.setItemInHand(hand, result.modifiedStack);
                        for (ItemStack stack : result.otherStacks) {
                            if (target instanceof ServerPlayer player) {
                                if (player.addItem(stack)) {

                                } else {
                                    LevelUtil.spawnAtLocation(stack, 0.5f, target.blockPosition(), world);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //todo: turrets use gems from drygmies
    private static class WrappedEnchanter {
        private LivingEntity shooter;
        private ServerPlayer player = null;
        private  SpellContext context;
        private Level world;

        private int seed;

        public WrappedEnchanter(LivingEntity shooter, SpellContext context, Level world) {
            this.shooter = shooter;
            this.context = context;
            this.world = world;
            //we need to set seed no matter what
            //and player if it's not a fake player
            if (shooter instanceof ServerPlayer serverPlayer && INSTANCE.isNotFakePlayer(shooter)) {
                player = serverPlayer;
                seed = player.getEnchantmentSeed();
            }
            else{
                seed = world.random.nextInt();
            }
        }

        public Level getLevel(){
            return world;
        }

        public int getSeed(){
            return seed;
        }

        public boolean canEnchant() {
            return true;//for now
        }

        public int getExperience() {
            if (player == null) {

                int xp = 0;
                List<SlotReference> slotReferences = new ArrayList<>();
                InventoryManager inv = context.getCaster().getInvManager();
                for(FilterableItemHandler handler : inv.getInventory()) {
                    slotReferences.addAll(inv.findItems(handler, (stack) -> ExperienceUtil.getExperienceValue(stack) > 0, InteractType.EXTRACT, 100));
                }

                for(SlotReference ref : slotReferences){
                    ItemStack stack = ref.getHandler().getStackInSlot(ref.getSlot());

                    int pointsPerItem = ExperienceUtil.getExperienceValue(stack);

                    xp += pointsPerItem * stack.getCount();
                }

                LoggerContext.getContext().getLogger(EnchantGlyph.class).info("experience in caster tile: "+xp);

                return xp;
            }
            LoggerContext.getContext().getLogger(EnchantGlyph.class).info("experience in player "+ExperienceUtil.getExperiencePoints(player));
            return ExperienceUtil.getExperiencePoints(player);
        }

        public void OnEnchant(ItemStack stack){
            if(player == null){
                seed = world.random.nextInt();
            }
            else{
                player.onEnchantmentPerformed(stack, 0);
                seed = player.getEnchantmentSeed();
            }
        }

        public boolean spendExperience(int points) {
            if (player == null) {
                int pointsLeft = points;
                List<SlotReference> slotReferences = new ArrayList<>();
                InventoryManager inv = context.getCaster().getInvManager();
                for(FilterableItemHandler handler : inv.getInventory()) {
                    slotReferences.addAll(inv.findItems(handler, (stack) -> ExperienceUtil.getExperienceValue(stack) > 0, InteractType.EXTRACT, 100));
                }

                for(SlotReference ref : slotReferences){
                    ItemStack stack = ref.getHandler().getStackInSlot(ref.getSlot());

                    int pointsPerItem = ExperienceUtil.getExperienceValue(stack);

                    int toUse = Math.min((pointsLeft / pointsPerItem) + 1,stack.getCount());

                    ref.getHandler().extractItem(ref.getSlot(),toUse,false);

                    pointsLeft -= toUse * pointsPerItem;

                    if(pointsLeft == 0){
                        break;
                    }
                }

                //todo: way to refund and return false if necessary?
                return true;
            }
            LoggerContext.getContext().getLogger(EnchantGlyph.class).info("spending experience: "+points);
            ExperienceUtil.spendExperience(player, points);
            return true;
        }

        public void AwardAchievement(ItemStack stack, int level) {
            if (player == null) {
                return;
            }
            player.awardStat(Stats.ENCHANT_ITEM);
            CriteriaTriggers.ENCHANTED_ITEM.trigger(player, stack, level);
        }
    }


    private static record EnchantResult(boolean succeeded, int experienceToSpend, ItemStack modifiedStack, List<ItemStack> otherStacks) {

    }

    public EnchantResult enchantItem(WrappedEnchanter wrappedEnchanter, ItemStack stackIn, int powerLevel, int maxItemsProcessed) {

        return enchantItem(wrappedEnchanter.getLevel(), wrappedEnchanter, wrappedEnchanter.getSeed(), stackIn, powerLevel, wrappedEnchanter.getExperience(), maxItemsProcessed);
    }

    @NotNull
    public EnchantResult enchantItem(Level world, WrappedEnchanter wrappedEnchanter, int seed, ItemStack stackIn, int level, int xpPointsAvailable, int maxItemsProcessed) {
        int power = power(level);

        if (stackIn.isEmpty()) {
            return new EnchantResult(false, 0, stackIn, new ArrayList<>());
        }

        int xpCost = ExperienceUtil.getExperienceForLevel(power);

        List<EnchantRecipe> recipes = world.getRecipeManager().getAllRecipesFor(RegistryHandler.ENCHANT_TYPE);
        EnchantRecipe selectedRecipe = null;
        for (EnchantRecipe recipe : recipes) {
            if (stackIn.is(recipe.input.getItem()) && level >= recipe.minLevel) {
                selectedRecipe = recipe;
                xpCost = ExperienceUtil.getExperienceForLevel(recipe.minLevel);
                break;
            }
        }

        if (selectedRecipe != null) {

            ItemStack stackOut = selectedRecipe.output.copy();

            int amountProcessed = stackIn.getCount() / selectedRecipe.input.getCount();

            //limit by experience available
            amountProcessed = Math.min(amountProcessed, xpPointsAvailable / xpCost);

            //limit by max items processed
            amountProcessed = Math.min(amountProcessed, maxItemsProcessed / selectedRecipe.output.getCount());

            stackOut.setCount(amountProcessed * selectedRecipe.output.getCount());

            stackIn.shrink(amountProcessed * selectedRecipe.input.getCount());

            if (stackIn.isEmpty()) {
                return new EnchantResult(true, xpCost * amountProcessed, stackOut, List.of());
            }
            else{
                return new EnchantResult(true, xpCost * amountProcessed, stackIn, List.of(stackOut));
            }
        } else if (stackIn.isEnchanted()) {
            return new EnchantResult(false, 0, stackIn, new ArrayList<>());
        }

        ItemStack stackOut = null;
        List<ItemStack> extraStacks = new ArrayList<>(maxItemsProcessed);
        boolean succeeded = false;
        int experienceLeft = xpPointsAvailable;

        for (int i = 0; i < maxItemsProcessed && experienceLeft >= xpCost; i++) {

            ItemStack result = stackIn.copy();
            result.setCount(1);

            List<EnchantmentInstance> list = this.getEnchantmentList(result, seed, power);
            if (list.isEmpty()) {
                break;
            }

            //calculate result stack
            boolean isBook = result.getItem() == Items.BOOK;
            if (isBook) {
                result = new ItemStack(Items.ENCHANTED_BOOK, 1);
                CompoundTag compoundnbt = stackIn.getTag();
                if (compoundnbt != null) {
                    result.setTag(compoundnbt.copy());
                }
            }
            for (int j = 0; j < list.size(); ++j) {
                EnchantmentInstance enchantmentdata = list.get(j);
                if (isBook) {
                    EnchantedBookItem.addEnchantment(result, enchantmentdata);
                } else {
                    result.enchant(enchantmentdata.enchantment, enchantmentdata.level);
                }
            }

            wrappedEnchanter.AwardAchievement(result, level);


            //once result is calculated, use it and set the input
            //at this point, an enchantment has succeeded or the loop has broken
            succeeded = true;
            stackIn.shrink(1);

            experienceLeft -= xpCost;
            wrappedEnchanter.OnEnchant(result);

            if (stackIn.isEmpty()) {
                stackOut = result;
            } else {
                for (ItemStack stack : extraStacks) {
                    if (ItemStack.isSameItemSameTags(result, stack)) {
                        int maxAdded = stack.getMaxStackSize() - stack.getCount();
                        stack.grow(Math.min(maxAdded, result.getCount()));
                        result.shrink(maxAdded);
                        break;
                    }
                }
                if (!result.isEmpty()) {
                    extraStacks.add(result);
                }
            }

        }

        if(stackOut == null){
            stackOut = stackIn;
        }

        int xpSpent = xpPointsAvailable - experienceLeft;
        return new EnchantResult(succeeded, xpSpent, stackOut, extraStacks);
    }


    private List<EnchantmentInstance> getEnchantmentList(ItemStack p_178148_1_, long p_178148_2_, int p_178148_3_) {
        random.setSeed(p_178148_2_);
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, p_178148_1_, p_178148_3_, false);


        return list;
    }

    @Override
    public int getDefaultManaCost() {
        return 660;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(new AbstractAugment[]{AugmentAmplify.INSTANCE});
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(new SpellSchool[]{SpellSchools.ABJURATION,SpellSchools.MANIPULATION});
    }
}
