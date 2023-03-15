package de.lordhahaha.timberframemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.text.MessageFormat;

public class ShutterBlock extends Block{
    public static final EnumProperty<Neighbour> NEIGHBOUR = EnumProperty.create("neighbour", Neighbour.class);
    public static final EnumProperty<Neighbour> CONNECTED_BLOCK = EnumProperty.create("connected_block", Neighbour.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public ShutterBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_EAST = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 0, 0, 2, 16, 16);

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        Direction direction = blockState.getValue(FACING);
        switch (direction){
            case NORTH:
            default:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_WEST;
            case SOUTH:
                return SHAPE_NORTH;
            case WEST:
                return SHAPE_EAST;
        }
    }

    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity p_52752_, ItemStack p_52753_) {
        if(blockState != null) {
            System.out.println("234");
            Direction facing = blockState.getValue(FACING);
            level.setBlock(getNeighbourShutter(blockPos, blockState, facing), blockState
                            .setValue(FACING, blockState.getValue(FACING))
                            .setValue(ACTIVE, !blockState.getValue(ACTIVE))
                            .setValue(OPEN, blockState.getValue(OPEN))
                            .setValue(CONNECTED_BLOCK,(blockState.getValue(CONNECTED_BLOCK) == Neighbour.LEFT ? Neighbour.RIGHT : Neighbour.LEFT))
                            .setValue(NEIGHBOUR, blockState.getValue(NEIGHBOUR)),
                    3);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        BlockPos blockPos = placeContext.getClickedPos();
        Level level = placeContext.getLevel();
        Neighbour connecedBlock = placeContext.getPlayer().isCrouching() ? Neighbour.LEFT : Neighbour.RIGHT;
        System.out.println(connecedBlock);
        BlockState neighbourBlockState = level.getBlockState(getNeighbourShutter(blockPos, connecedBlock, placeContext.getHorizontalDirection().getOpposite()));
        if(neighbourBlockState.canBeReplaced(placeContext)) {
            BlockState newBlock = this.defaultBlockState();
            newBlock = newBlock
                    .setValue(FACING, placeContext.getHorizontalDirection().getOpposite())
                    .setValue(ACTIVE, Boolean.TRUE)
                    .setValue(NEIGHBOUR, linkToNeighbourShutter(newBlock, blockPos, level))
                    .setValue(OPEN, Boolean.TRUE)
                    .setValue(CONNECTED_BLOCK, connecedBlock);

            return newBlock;
        }

        else
            return null;
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        updateShutter(blockState, level, blockPos, !blockState.getValue(ACTIVE), blockState.getValue(FACING));
        updateNeighbourShutter(blockState, level, blockPos); // Try to change another Shutter boundle
        level.levelEvent(player, 1006, blockPos, 0);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    private void updateShutter(BlockState blockState, Level level, BlockPos pos, boolean open, Direction dir) {
        // Update the clicked Block
        level.setBlockAndUpdate(pos, blockState.setValue(ACTIVE, open).setValue(OPEN, !blockState.getValue(OPEN)));
        // Update the connected Block
        pos = getNeighbourShutter(pos, blockState, dir);
        blockState = level.getBlockState(pos);
        level.setBlockAndUpdate(pos, blockState.setValue(ACTIVE, !open).setValue(OPEN, !blockState.getValue(OPEN)));
    }

    private Neighbour linkToNeighbourShutter(BlockState blockState, BlockPos blockPos, Level level){
        Direction facing = blockState.getValue(FACING);
        // Right
        BlockPos neighbourPos = getNeighbourShutterPosition(getNeighbourShutterPosition(blockPos, facing),facing);
        BlockState neighbourState = level.getBlockState(neighbourPos);
        System.out.println(MessageFormat.format("Own: {0}, Next: {1}", blockState.getBlock(), neighbourState.getBlock()));
        if(neighbourState.getBlock() == blockState.getBlock()){
            if(neighbourState.getValue(NEIGHBOUR) == Neighbour.NULL){
                level.setBlockAndUpdate(neighbourPos, neighbourState.setValue(NEIGHBOUR, Neighbour.LEFT));
                level.setBlockAndUpdate(blockPos, blockState.setValue(NEIGHBOUR, Neighbour.RIGHT));
                return Neighbour.RIGHT;
            }
        }

        // Left
        facing = facing.getOpposite();
        neighbourPos = getNeighbourShutterPosition(blockPos, facing);
        neighbourState = level.getBlockState(neighbourPos);
        if(neighbourState.getBlock() == blockState.getBlock()){
            if(neighbourState.getValue(NEIGHBOUR) == Neighbour.NULL){
                level.setBlockAndUpdate(neighbourPos, neighbourState.setValue(NEIGHBOUR, Neighbour.RIGHT));
                level.setBlockAndUpdate(blockPos, blockState.setValue(NEIGHBOUR, Neighbour.LEFT));
                return Neighbour.LEFT;
            }
        }


        return Neighbour.NULL;
    }
    private void updateNeighbourShutter(BlockState blockState, Level level, BlockPos blockPos){
        BlockPos neighbourShutterPos = null;
        if(blockState.getValue(NEIGHBOUR) == Neighbour.NULL)
            return;

        Direction facing = blockState.getValue(FACING);

        // Test Left / Right neighbour
        if(blockState.getValue(NEIGHBOUR) == Neighbour.LEFT) {
            neighbourShutterPos = getNeighbourShutterPosition(blockPos, facing);
            BlockState neighbourShutter = level.getBlockState(neighbourShutterPos);
            if(neighbourShutter.getBlock() == blockState.getBlock() &&
                blockState.getValue(NEIGHBOUR) != neighbourShutter.getValue(NEIGHBOUR)){
                updateShutter(neighbourShutter, level, neighbourShutterPos, !neighbourShutter.getValue(OPEN), blockState.getValue(FACING));
                return;
            }
        }

        if(blockState.getValue(NEIGHBOUR) == Neighbour.RIGHT){
            neighbourShutterPos = getNeighbourShutterPosition(getNeighbourShutterPosition(blockPos, facing),facing);

        BlockState neighbourShutter = level.getBlockState(neighbourShutterPos);
        if(neighbourShutter.getBlock() == blockState.getBlock()){
            updateShutter(neighbourShutter, level, neighbourShutterPos, !neighbourShutter.getValue(ACTIVE), blockState.getValue(FACING));;
        }

        facing = facing.getOpposite();
        if(blockState.getValue(NEIGHBOUR) == Neighbour.LEFT) {
            neighbourShutterPos = getNeighbourShutterPosition(getNeighbourShutterPosition(blockPos, facing), facing);
            neighbourShutterPos = getNeighbourShutterPosition(blockPos, facing);
            BlockState neighbourShutter = level.getBlockState(neighbourShutterPos);
            if(neighbourShutter.getBlock() == blockState.getBlock() &&
                    blockState.getValue(NEIGHBOUR) == neighbourShutter.getValue(NEIGHBOUR)){
                updateShutter(neighbourShutter, level, neighbourShutterPos, !neighbourShutter.getValue(OPEN), blockState.getValue(FACING));
                return;
            }
        }

        neighbourShutter = level.getBlockState(neighbourShutterPos);
        if(neighbourShutter.getBlock() == blockState.getBlock()){
            updateShutter(neighbourShutter, level, neighbourShutterPos, !neighbourShutter.getValue(ACTIVE), blockState.getValue(FACING));
        }

    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player p_49855_) {
        BlockPos toDelete = getNeighbourShutter(blockPos, blockState, blockState.getValue(FACING));
        level.destroyBlock(toDelete, false);
        super.playerWillDestroy(level, blockPos, blockState, p_49855_);
    }

    private BlockPos getNeighbourShutter(BlockPos blockPos, BlockState blockState, Direction facing){
        return getNeighbourShutter(blockPos, blockState.getValue(CONNECTED_BLOCK), facing);
    }
    private BlockPos getNeighbourShutter(BlockPos blockPos, Neighbour neighbour, Direction facing){
        if(neighbour == Neighbour.LEFT)
            facing = facing.getOpposite();

        return getNeighbourShutterPosition(blockPos, facing);
    }

    private BlockPos getNeighbourShutterPosition(BlockPos blockPos, Direction facing){
        switch (facing){
            default:
            case NORTH:
                return blockPos.west();
            case EAST:
                return blockPos.north();
            case SOUTH:
                return blockPos.east();
            case WEST:
                return blockPos.south();
        }
    }


    @Override
    public BlockState rotate(BlockState p_48722_, Rotation rotation) {
        return p_48722_.setValue(FACING, rotation.rotate(p_48722_.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_48719_, Mirror mirror) {
        return p_48719_.rotate(mirror.getRotation(p_48719_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING, NEIGHBOUR, ACTIVE, CONNECTED_BLOCK);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    public enum Neighbour implements StringRepresentable{
        LEFT("left"), RIGHT("right"), NULL("null");

        private final String name;

        Neighbour(final String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
