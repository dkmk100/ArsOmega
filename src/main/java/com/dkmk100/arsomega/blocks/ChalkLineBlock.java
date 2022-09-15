package com.dkmk100.arsomega.blocks;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.util.ChalkColor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.items.RunicChalk;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectKnockback;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class ChalkLineBlock extends TickableModBlock {
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));

    private static final VoxelShape SHAPE_DOT = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);

    private static final VoxelShape SHAPE_TEST = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final Map<Direction, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Direction.SOUTH, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST, Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST, Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
    private static final Map<Direction, VoxelShape> SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Shapes.or(SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), Direction.SOUTH, Shapes.or(SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), Direction.EAST, Shapes.or(SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), Direction.WEST, Shapes.or(SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
    private static final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();

    private int[] COLORS;

    public int GetColor(BlockState state, BlockPos pos,BlockGetter getter){
        //can do more fun stuff later
        return GetColor(state);
    }

    // legacy get color
    private int GetColor(BlockState state){
        return COLORS[state.getValue(POWER)];
    }

    private BlockState crossState;

    private VoxelShape calculateShape(BlockState p_55643_) {
        //temporary, will probably change again later
        if(true) {
            return SHAPE_TEST;
        }
        VoxelShape voxelshape = SHAPE_DOT;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = p_55643_.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (redstoneside == RedstoneSide.SIDE) {
                voxelshape = Shapes.or(voxelshape, SHAPES_FLOOR.get(direction));
            } else if (redstoneside == RedstoneSide.UP) {
                voxelshape = Shapes.or(voxelshape, SHAPES_UP.get(direction));
            }
        }

        return voxelshape;
    }

    private final int maxCharges;
    private final float costMultiplier;
    public ChalkLineBlock(BlockBehaviour.Properties p_55511_, int maxCharges, float costMultiplier, ChalkColor color) {
        super(p_55511_);

        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, RedstoneSide.NONE).setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE).setValue(WEST, RedstoneSide.NONE).setValue(POWER, Integer.valueOf(0)));
        this.crossState = this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE).setValue(EAST, RedstoneSide.SIDE).setValue(SOUTH, RedstoneSide.SIDE).setValue(WEST, RedstoneSide.SIDE);


        for(BlockState blockstate : this.getStateDefinition().getPossibleStates()) {
            if (blockstate.getValue(POWER) == 0) {
                SHAPES_CACHE.put(blockstate, this.calculateShape(blockstate));
            }
        }

        COLORS = Util.make(new int[16], (p_154319_) -> {
            for(int i = 0; i <= 15; ++i) {
                p_154319_[i] = color.getColor(i,maxCharges);
            }

        });

        this.maxCharges = maxCharges;
        this.costMultiplier = costMultiplier;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if (worldIn.getBlockEntity(pos) instanceof ChalkTile && entityIn instanceof LivingEntity) {
            ArsOmega.LOGGER.info("entity inside");
            ChalkTile rune = (ChalkTile)worldIn.getBlockEntity(pos);
            if(rune.canCastSpell() && rune.shouldHitEntity(entityIn)) {
                ArsOmega.LOGGER.info("can cast on entity");
                rune.touchedEntity = entityIn;
            }
            worldIn.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        if(worldIn.getBlockEntity(pos) instanceof ChalkTile && ((ChalkTile) worldIn.getBlockEntity(pos)).touchedEntity != null)
        {
            ChalkTile rune = (ChalkTile) worldIn.getBlockEntity(pos);
            ArsOmega.LOGGER.info("casting spell");
            rune.castSpell(rune.touchedEntity);
            if (rune.data.owner != null && rune.touchedEntity instanceof LivingEntity && rune.data.charges > 0) {
                Player player = worldIn.getPlayerByUUID(rune.data.owner);
                if(player!=null) {
                    EffectKnockback.INSTANCE.knockback(rune.touchedEntity, player, 0.3f);
                }
            }
            UpdateAdjacentSpells(worldIn, pos);

            rune.touchedEntity = null;
        }
    }

    public boolean SetSpell(ItemStack stack, @Nullable Player player, Level worldIn, BlockPos pos) {
        //shenanigans because discount plus slots is pain
        ISpellCaster caster = CasterUtil.getCaster(stack);
        Spell spell = caster.getSpell();
        Spell spell2 = spell.clone();
        spell2.setCost((int)Math.ceil(spell2.getCastingCost() * costMultiplier));
        ISpellCaster caster2 = CasterUtil.getCaster(stack);
        caster2.setSpell(spell2);


        boolean flag = false;
        ChalkTile tile = ((ChalkTile)worldIn.getBlockEntity(pos));
        if(tile.data.owner != null && player != worldIn.getPlayerByUUID(tile.data.owner)){
            PortUtil.sendMessageNoSpam(player, new TextComponent("Cannot modify another player's spell circle..."));
            flag = true;
        }

        if (spell.isEmpty() || flag) {
            return false;
        } else if (!(spell.recipe.get(0) instanceof MethodTouch)) {
            PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.rune.touch"));
            return true;
        }
        else{
            SpellResolver resolver = new SpellResolver(caster2,player);
            if(enoughMana(player,resolver)) {

                boolean setSpell = tile.data.spell.recipe != spell.recipe;
                tile.setSpell(spell);
                tile.setSpellColor(caster.getColor().toParticleColor());
                if (player != null) {
                    tile.data.owner = player.getUUID();
                }

                boolean charges = tile.tryAddCharges(1, maxCharges);
                if(setSpell || charges){
                    resolver.expendMana(player);
                    return true;
                }
            }
        }
        return false;
    }

    boolean enoughMana(LivingEntity entity,SpellResolver resolver) {
        int totalCost = resolver.getCastingCost(resolver.spell, entity);
        IManaCap manaCap = (IManaCap) CapabilityRegistry.getMana(entity).orElse((IManaCap)null);
        if (manaCap == null) {
            return false;
        } else {
            boolean canCast = (double)totalCost <= manaCap.getCurrentMana() || entity instanceof Player && ((Player)entity).isCreative();
            if (!canCast && !entity.getCommandSenderWorld().isClientSide) {
                PortUtil.sendMessageNoSpam(entity, new TranslatableComponent("ars_nouveau.spell.no_mana"));
            }

            return canCast;
        }
    }

    public void RequestAdjacentUpdates(Level worldIn, BlockPos pos){
        boolean changed = false;
        for (Direction dir : Direction.values()) {
            if (dir != Direction.DOWN && dir != Direction.UP) {
                for (int i = -1; i <= 1; i++) {
                    BlockPos pos1 = pos.above(i).relative(dir);
                    BlockState state = worldIn.getBlockState(pos1);
                    if (isValidChalk(state)) {
                        ChalkTile tile = ((ChalkTile) worldIn.getBlockEntity(pos1));
                        //set own spell based on new pos
                        if (SetData(tile.data, worldIn, pos)) {
                            if(changed){
                                worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                return;
                            }
                            changed = true;
                            //don't propagate changes, I fear abuse of connecting two lines...
                            //maybe save something and just die if trying to do that? IDK

                            break;//break the for loop to not check further vertically, we are done
                        }
                    }
                }
            }
        }
        if(changed){
            UpdateAdjacentSpells(worldIn,pos);
        }
    }

    public void UpdateAdjacentSpells(Level worldIn, BlockPos pos) {
        ArsOmega.LOGGER.info("updating adjacent from: "+pos);
        ChalkTile tile = ((ChalkTile) worldIn.getBlockEntity(pos));
        if(tile==null){
            return;
        }
        for (Direction dir : Direction.values()) {
            if (dir != Direction.DOWN && dir != Direction.UP) {
                for (int i = -1; i <= 1; i++) {
                    BlockPos pos1 = pos.above(i).relative(dir);
                    BlockState state = worldIn.getBlockState(pos1);
                    if (isValidChalk(state)) {

                        if(SetData(tile.data, worldIn, pos1)) {
                            UpdateAdjacentSpells(worldIn, pos1);//propagate changes
                        }

                        break;//break the for loop to not check further vertically, we are done
                    }
                }
            }
        }
    }

    public boolean SetData(ChalkLineData data, Level worldIn, BlockPos pos){
        ChalkTile tile = ((ChalkTile)worldIn.getBlockEntity(pos));
        BlockState state = worldIn.getBlockState(pos);
        int oldPower = state.getValue(POWER);
        if(tile.SetData(data)){
            tile.savesData = false;
            worldIn.setBlockAndUpdate(pos, state.setValue(ChalkLineBlock.POWER,data.charges));
            return true;
        }
        else if(oldPower!=data.charges) {
            worldIn.setBlockAndUpdate(pos, state.setValue(ChalkLineBlock.POWER,data.charges));
            return true;

        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(handIn);
        if (stack.getItem() instanceof SpellParchment || stack.getItem() instanceof SpellBook && !worldIn.isClientSide) {

            if(SetSpell(stack,player, worldIn, pos)){
                ChalkTile tile = ((ChalkTile)worldIn.getBlockEntity(pos));
                tile.savesData = true;
                ArsOmega.LOGGER.info("set new spell and flagged as saves data");
                UpdateAdjacentSpells(worldIn,pos);//propagate spell change
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55605_) {
        p_55605_.add(NORTH, EAST, SOUTH, WEST, POWER);
    }

    public BlockState rotate(BlockState p_55592_, Rotation p_55593_) {
        switch(p_55593_) {
            case CLOCKWISE_180:
                return p_55592_.setValue(NORTH, p_55592_.getValue(SOUTH)).setValue(EAST, p_55592_.getValue(WEST)).setValue(SOUTH, p_55592_.getValue(NORTH)).setValue(WEST, p_55592_.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return p_55592_.setValue(NORTH, p_55592_.getValue(EAST)).setValue(EAST, p_55592_.getValue(SOUTH)).setValue(SOUTH, p_55592_.getValue(WEST)).setValue(WEST, p_55592_.getValue(NORTH));
            case CLOCKWISE_90:
                return p_55592_.setValue(NORTH, p_55592_.getValue(WEST)).setValue(EAST, p_55592_.getValue(NORTH)).setValue(SOUTH, p_55592_.getValue(EAST)).setValue(WEST, p_55592_.getValue(SOUTH));
            default:
                return p_55592_;
        }
    }

    public BlockState mirror(BlockState p_55589_, Mirror p_55590_) {
        switch(p_55590_) {
            case LEFT_RIGHT:
                return p_55589_.setValue(NORTH, p_55589_.getValue(SOUTH)).setValue(SOUTH, p_55589_.getValue(NORTH));
            case FRONT_BACK:
                return p_55589_.setValue(EAST, p_55589_.getValue(WEST)).setValue(WEST, p_55589_.getValue(EAST));
            default:
                return super.mirror(p_55589_, p_55590_);
        }
    }

    @Override
    public void onPlace(BlockState p_55630_, Level p_55631_, BlockPos p_55632_, BlockState p_55633_, boolean p_55634_) {
        if (!p_55633_.is(p_55630_.getBlock()) && !p_55631_.isClientSide) {
            for(Direction direction : Direction.Plane.VERTICAL) {
                p_55631_.updateNeighborsAt(p_55632_.relative(direction), this);
            }

            this.updateNeighborsOfNeighboringWires(p_55631_, p_55632_);
        }
    }

    @Override
    public void onRemove(BlockState p_55568_, Level p_55569_, BlockPos p_55570_, BlockState p_55571_, boolean p_55572_) {
        if (!p_55572_ && !p_55568_.is(p_55571_.getBlock())) {
            super.onRemove(p_55568_, p_55569_, p_55570_, p_55571_, p_55572_);
            if (!p_55569_.isClientSide) {
                for(Direction direction : Direction.values()) {
                    p_55569_.updateNeighborsAt(p_55570_.relative(direction), this);
                }

                this.updateNeighborsOfNeighboringWires(p_55569_, p_55570_);
            }
        }
    }

    private void updateNeighborsOfNeighboringWires(Level p_55638_, BlockPos p_55639_) {
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            this.checkCornerChangeAt(p_55638_, p_55639_.relative(direction));
        }

        for(Direction direction1 : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = p_55639_.relative(direction1);
            if (p_55638_.getBlockState(blockpos).isRedstoneConductor(p_55638_, blockpos)) {
                this.checkCornerChangeAt(p_55638_, blockpos.above());
            } else {
                this.checkCornerChangeAt(p_55638_, blockpos.below());
            }
        }

    }

    private void checkCornerChangeAt(Level p_55617_, BlockPos p_55618_) {
        if (p_55617_.getBlockState(p_55618_).is(this)) {
            p_55617_.updateNeighborsAt(p_55618_, this);

            for(Direction direction : Direction.values()) {
                p_55617_.updateNeighborsAt(p_55618_.relative(direction), this);
            }

        }
    }

    @Override
    public void neighborChanged(BlockState p_55561_, Level p_55562_, BlockPos p_55563_, Block p_55564_, BlockPos p_55565_, boolean p_55566_) {
        if (!p_55562_.isClientSide) {
            if (!p_55561_.canSurvive(p_55562_, p_55563_)) {
                dropResources(p_55561_, p_55562_, p_55563_);
                p_55562_.removeBlock(p_55563_, false);
            }

        }
    }

    @Override
    public VoxelShape getShape(BlockState p_55620_, BlockGetter p_55621_, BlockPos p_55622_, CollisionContext p_55623_) {
        return SHAPES_CACHE.get(p_55620_.setValue(POWER, Integer.valueOf(0)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_55513_) {
        return this.getConnectionState(p_55513_.getLevel(), this.crossState, p_55513_.getClickedPos());
    }

    private BlockState getConnectionState(BlockGetter p_55515_, BlockState p_55516_, BlockPos p_55517_) {
        boolean flag = isDot(p_55516_);
        p_55516_ = this.getMissingConnections(p_55515_, this.defaultBlockState().setValue(POWER, p_55516_.getValue(POWER)), p_55517_);
        if (flag && isDot(p_55516_)) {
            return p_55516_;
        } else {
            boolean flag1 = p_55516_.getValue(NORTH).isConnected();
            boolean flag2 = p_55516_.getValue(SOUTH).isConnected();
            boolean flag3 = p_55516_.getValue(EAST).isConnected();
            boolean flag4 = p_55516_.getValue(WEST).isConnected();
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;
            if (!flag4 && flag5) {
                p_55516_ = p_55516_.setValue(WEST, RedstoneSide.SIDE);
            }

            if (!flag3 && flag5) {
                p_55516_ = p_55516_.setValue(EAST, RedstoneSide.SIDE);
            }

            if (!flag1 && flag6) {
                p_55516_ = p_55516_.setValue(NORTH, RedstoneSide.SIDE);
            }

            if (!flag2 && flag6) {
                p_55516_ = p_55516_.setValue(SOUTH, RedstoneSide.SIDE);
            }

            return p_55516_;
        }
    }

    private BlockState getMissingConnections(BlockGetter p_55609_, BlockState p_55610_, BlockPos p_55611_) {
        boolean flag = !p_55609_.getBlockState(p_55611_.above()).isRedstoneConductor(p_55609_, p_55611_);

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (!p_55610_.getValue(PROPERTY_BY_DIRECTION.get(direction)).isConnected()) {
                RedstoneSide redstoneside = this.getConnectingSide(p_55609_, p_55611_, direction, flag);
                p_55610_ = p_55610_.setValue(PROPERTY_BY_DIRECTION.get(direction), redstoneside);
            }
        }

        return p_55610_;
    }

    public Boolean isValidChalk(BlockState state){
        //ensure equivalent max charges on a line
        return state.getBlock() instanceof ChalkLineBlock && ((ChalkLineBlock)state.getBlock()).maxCharges == this.maxCharges;
    }


    @Override
    public boolean canSurvive(BlockState p_55585_, LevelReader p_55586_, BlockPos p_55587_) {
        BlockPos blockpos = p_55587_.below();
        BlockState blockstate = p_55586_.getBlockState(blockpos);
        return this.canSurviveOn(p_55586_, blockpos, blockstate);
    }

    private boolean canSurviveOn(BlockGetter p_55613_, BlockPos p_55614_, BlockState p_55615_) {
        return p_55615_.isFaceSturdy(p_55613_, p_55614_, Direction.UP);
    }

    public BlockState updateShape(BlockState p_55598_, Direction p_55599_, BlockState p_55600_, LevelAccessor p_55601_, BlockPos p_55602_, BlockPos p_55603_) {
        if (p_55599_ == Direction.DOWN) {
            return p_55598_;
        } else if (p_55599_ == Direction.UP) {
            return this.getConnectionState(p_55601_, p_55598_, p_55602_);
        } else {
            RedstoneSide redstoneside = this.getConnectingSide(p_55601_, p_55602_, p_55599_);
            return redstoneside.isConnected() == p_55598_.getValue(PROPERTY_BY_DIRECTION.get(p_55599_)).isConnected() && !isCross(p_55598_) ? p_55598_.setValue(PROPERTY_BY_DIRECTION.get(p_55599_), redstoneside) : this.getConnectionState(p_55601_, this.crossState.setValue(POWER, p_55598_.getValue(POWER)).setValue(PROPERTY_BY_DIRECTION.get(p_55599_), redstoneside), p_55602_);
        }
    }

    private static boolean isCross(BlockState p_55645_) {
        return p_55645_.getValue(NORTH).isConnected() && p_55645_.getValue(SOUTH).isConnected() && p_55645_.getValue(EAST).isConnected() && p_55645_.getValue(WEST).isConnected();
    }

    private static boolean isDot(BlockState p_55647_) {
        return !p_55647_.getValue(NORTH).isConnected() && !p_55647_.getValue(SOUTH).isConnected() && !p_55647_.getValue(EAST).isConnected() && !p_55647_.getValue(WEST).isConnected();
    }

    private RedstoneSide getConnectingSide(BlockGetter p_55519_, BlockPos p_55520_, Direction p_55521_) {
        return this.getConnectingSide(p_55519_, p_55520_, p_55521_, !p_55519_.getBlockState(p_55520_.above()).isRedstoneConductor(p_55519_, p_55520_));
    }

    private RedstoneSide getConnectingSide(BlockGetter p_55523_, BlockPos p_55524_, Direction p_55525_, boolean p_55526_) {
        BlockPos blockpos = p_55524_.relative(p_55525_);
        BlockState blockstate = p_55523_.getBlockState(blockpos);
        if (p_55526_) {
            boolean flag = this.canSurviveOn(p_55523_, blockpos, blockstate);
            if (flag && isValidChalk(p_55523_.getBlockState(blockpos.above()))) {
                if (blockstate.isFaceSturdy(p_55523_, blockpos, p_55525_.getOpposite())) {
                    return RedstoneSide.UP;
                }
                return RedstoneSide.SIDE;
            }
        }

        if (isValidChalk(blockstate)) {
            return RedstoneSide.SIDE;
        } else if (false) //idk what this was for tbh
        {
            return RedstoneSide.NONE;
        } else {
            BlockPos blockPosBelow = blockpos.below();
            return isValidChalk(p_55523_.getBlockState(blockPosBelow)) ? RedstoneSide.SIDE : RedstoneSide.NONE;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChalkTile(pos, state);
    }
}
