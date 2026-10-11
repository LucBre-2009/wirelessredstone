package de.lucbre.wirelessredstone.block;

import de.lucbre.wirelessredstone.WirelessRedstone;
import de.lucbre.wirelessredstone.block.entity.WirelessNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WirelessSwitchBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    // Match the vanilla button model dimensions (6x4 pixels, 2 pixels deep).
    private static final VoxelShape FLOOR_SHAPE = Block.box(5, 0, 5, 11, 2, 11);
    private static final VoxelShape FLOOR_PRESSED_SHAPE = Block.box(5, 0, 5, 11, 1, 11);
    private static final VoxelShape CEILING_SHAPE = Block.box(5, 14, 5, 11, 16, 11);
    private static final VoxelShape CEILING_PRESSED_SHAPE = Block.box(5, 15, 5, 11, 16, 11);
    private static final VoxelShape NORTH_SHAPE = Block.box(5, 5, 0, 11, 11, 2);
    private static final VoxelShape NORTH_PRESSED_SHAPE = Block.box(5, 5, 0, 11, 11, 1);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5, 5, 14, 11, 11, 16);
    private static final VoxelShape SOUTH_PRESSED_SHAPE = Block.box(5, 5, 15, 11, 11, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 5, 5, 2, 11, 11);
    private static final VoxelShape WEST_PRESSED_SHAPE = Block.box(0, 5, 5, 1, 11, 11);
    private static final VoxelShape EAST_SHAPE = Block.box(14, 5, 5, 16, 11, 11);
    private static final VoxelShape EAST_PRESSED_SHAPE = Block.box(15, 5, 5, 16, 11, 11);

    public WirelessSwitchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DirectionalBlock.FACING, Direction.UP).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DirectionalBlock.FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (face == Direction.DOWN) face = Direction.UP;
        return defaultBlockState().setValue(DirectionalBlock.FACING, face);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction face = state.getValue(DirectionalBlock.FACING);
        BlockPos support = pos.relative(face.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, face);
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean pressed = state.getValue(POWERED);
        return switch (state.getValue(DirectionalBlock.FACING)) {
            case DOWN -> pressed ? CEILING_PRESSED_SHAPE : CEILING_SHAPE;
            case NORTH -> pressed ? NORTH_PRESSED_SHAPE : NORTH_SHAPE;
            case SOUTH -> pressed ? SOUTH_PRESSED_SHAPE : SOUTH_SHAPE;
            case WEST -> pressed ? WEST_PRESSED_SHAPE : WEST_SHAPE;
            case EAST -> pressed ? EAST_PRESSED_SHAPE : EAST_SHAPE;
            default -> pressed ? FLOOR_PRESSED_SHAPE : FLOOR_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WirelessNodeBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(POWERED, !state.getValue(POWERED)), 3);
            level.updateNeighborsAt(pos, this);
        }
        return InteractionResult.SUCCESS;
    }

    @Override public boolean isSignalSource(BlockState state) { return true; }
    @Override public int getSignal(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, Direction direction) { return state.getValue(POWERED) ? 15 : 0; }
    @Override public int getDirectSignal(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, Direction direction) { return getSignal(state, level, pos, direction); }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, WirelessRedstone.NODE_ENTITY, WirelessNodeBlockEntity::tick);
    }
}
