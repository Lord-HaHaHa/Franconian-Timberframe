package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.stream.IntStream;

public class RoofBlock extends Block{
    static final int STATE_ROOF = 0;
    static final int STATE_CORNER_OUTER = 1;
    static final int STATE_CORNER_INNER = 2;
    static final int STATE_GABLE = 3;
    static final int STATE_TOP = 4;
    static final int STATE_TOP_EDGE = 5;
    static final int STATE_TOP_CROSS = 6;
    static final int STATE_TOP_T = 7;
    static final int STATE_TOP_L = 8;

    private static final VoxelShape TOP_SHAPE = Block.box(0,0,0,16,5,16);
    private static final VoxelShape BASE_SHAPE = Block.box(0,0,0,16,8,16);
    protected static final VoxelShape OCTET_NNN = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
    protected static final VoxelShape OCTET_NNP = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape OCTET_NPN = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape OCTET_NPP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape OCTET_PNN = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape OCTET_PNP = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape OCTET_PPN = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape OCTET_PPP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    //protected static final VoxelShape[] TOP_SHAPES = makeShapes(BASE_SHAPE, OCTET_NNN, OCTET_PNN, OCTET_NNP, OCTET_PNP);
    protected static final VoxelShape[] BOTTOM_SHAPES = makeShapes(BASE_SHAPE, OCTET_NPN, OCTET_PPN, OCTET_NPP, OCTET_PPP);
    private static final int[] SHAPE_BY_STATE = new int[]{
            0b1100, 0b0101, 0b0011, 0b1010,  //ROOF
            0b0100, 0b0001, 0b0010, 0b1000,  //CORNER_OUTER
            0b1101, 0b0111, 0b1011, 0b1110,  //CORNER_INNER
            0b1100, 0b0101, 0b0011, 0b1010   //GABLE
            };

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final DirectionProperty FACING_ORG = DirectionProperty.create("facing_org", Direction.Plane.HORIZONTAL);
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 8);
    public Block ROOF_BLOCK;
    public RoofBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING_ORG, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, STATE_GABLE));
    }
    private static VoxelShape[] makeShapes(VoxelShape shape1, VoxelShape shape2, VoxelShape shape3, VoxelShape shape4, VoxelShape shape5) {
        return IntStream.range(0, 16).mapToObj((state) -> {
            return makeStairShape(state, shape1, shape2, shape3, shape4, shape5);
        }).toArray((p_56949_) -> {
            return new VoxelShape[p_56949_];
        });
    }

    private static VoxelShape makeStairShape(int state, VoxelShape shape1, VoxelShape shape2, VoxelShape shape3, VoxelShape shape4, VoxelShape shape5) {
        VoxelShape voxelshape = shape1;
        if ((state & 1) != 0) {
            voxelshape = Shapes.or(shape1, shape2);
        }

        if ((state & 2) != 0) {
            voxelshape = Shapes.or(voxelshape, shape3);
        }

        if ((state & 4) != 0) {
            voxelshape = Shapes.or(voxelshape, shape4);
        }

        if ((state & 8) != 0) {
            voxelshape = Shapes.or(voxelshape, shape5);
        }

        return voxelshape;
    }

    private int getShapeIndex(BlockState state) {
        return state.getValue(STATE) * 4 + state.getValue(FACING).get2DDataValue();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if ( blockState.getValue(STATE) < STATE_TOP) {
            return (BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(blockState)]];
        }
        else{
            return TOP_SHAPE;
        }
        //return super.getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        //BlockState blockStateBehind = context.getLevel().getBlockState(context.getClickedPos().relative(context.getHorizontalDirection()));

        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(FACING_ORG, context.getHorizontalDirection())
                .setValue(STATE, this.defaultBlockState().getValue(STATE));

        blockState = checkForGable(blockState, level, blockPos, false);
        blockState = checkForTop(blockState, level, blockPos, false);
        blockState = checkForCorner(blockState, level, blockPos, false);
        return blockState;
    }


    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPosNeighbor, boolean bool) {
        super.neighborChanged(blockState, level, blockPos, block, blockPosNeighbor, bool);

        System.out.println("Neighbor Changed");
        System.out.println(MessageFormat.format("OWNPOS: {0} Neighbor: {1}", blockPos, blockPosNeighbor));

        if(block.equals(Blocks.AIR) || block.equals(ROOF_BLOCK)){
            if(blockState.getValue(STATE) < STATE_TOP && level.getBlockState(blockPosNeighbor.below()).getBlock().equals(Blocks.AIR))
                checkForGable(blockState, level, blockPos, true);
            checkForTop(blockState, level, blockPos, true);
        }

        if(block.equals(Blocks.AIR)) { // Only check if a new block is placed
            checkForCorner(blockState, level, blockPos, true);
        }
    }
    //TODO: remove corner when in line
    public BlockState checkForCorner(BlockState blockState, Level level, BlockPos blockPos, boolean place){
        boolean update = false;

        ROOF_BLOCK = ModBlocks.BLOCK_ROOF_MAIN.get();

        BlockPos blockPosBehind = blockPos.relative(blockState.getValue(FACING));
        BlockPos blockPosInfront = blockPos.relative(blockState.getValue(FACING).getOpposite());
        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());

        if(level.getBlockState(blockPosInfront).getBlock().equals(ROOF_BLOCK) ||
                level.getBlockState(blockPosBehind).getBlock().equals(ROOF_BLOCK))
        {

            if((level.getBlockState(blockPosSideClockwise).getBlock().equals(ROOF_BLOCK) ||
                    level.getBlockState(blockPosSideCounterClockwise).getBlock().equals(ROOF_BLOCK)) &&
                    !level.getBlockState(blockPosSideClockwise).getBlock().equals(level.getBlockState(blockPosSideCounterClockwise).getBlock())){
                if(blockState.getValue(STATE) == STATE_ROOF || blockState.getValue(STATE) == STATE_GABLE)
                {
                    // Checks if it´s an outer or inner coner
                    if(level.getBlockState(blockPosBehind).getBlock().equals(ROOF_BLOCK)) {

                        // rotate facing for the corner block
                        if (level.getBlockState(blockPosSideCounterClockwise).getBlock().equals(ROOF_BLOCK))
                        {
                            blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());
                        }
                        blockState = blockState.setValue(STATE, STATE_CORNER_OUTER);
                    }
                    else{
                        // rotate facing for the corner block
                        if (level.getBlockState(blockPosSideClockwise).getBlock().equals(ROOF_BLOCK))
                            blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());

                        blockState = blockState.setValue(STATE, STATE_CORNER_INNER);
                    }
                    update = true;
                }
            }
        }

        if(update && place){
            level.setBlockAndUpdate(blockPos, blockState);
        }
        return blockState;
    }

    /**
     * @param blockState State of the Roof Block
     * @param level
     * @param blockPos Position of the Block
     * @param place true if the Method place the Block
     * @return adjusted BlockState for the RoofBlock
     */
    public BlockState checkForGable(BlockState blockState, Level level, BlockPos blockPos, boolean place){
        // Checks if a given Block is a Gable
        boolean update = false;

        //do not change if Corner already set
        if( (blockState.getValue(STATE) == STATE_CORNER_OUTER || blockState.getValue(STATE) == STATE_CORNER_INNER))
            return blockState;

        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());

        Block blockBelow = level.getBlockState(blockPos.below()).getBlock();
        //Block blockClockwise = level.getBlockState(blockPosSideClockwise).getBlock();
        //Block blockCounterClockwise = level.getBlockState(blockPosSideCounterClockwise).getBlock();
        //Block blockInfront = level.getBlockState(blockPos.relative(blockState.getValue(FACING))).getBlock();
        //Block blockBehind = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getOpposite())).getBlock();
        if(
//                (blockClockwise.equals(ROOF_BLOCK) && blockCounterClockwise.equals(ROOF_BLOCK)) ||
//                (blockInfront.equals(ROOF_BLOCK) && blockBehind.equals(ROOF_BLOCK) ) ||
                blockBelow.equals(Blocks.AIR) ||
                        blockBelow.equals(ModBlocks.BLOCK_ROOF_MAIN.get()) ||
                        blockBelow.equals(ModBlocks.BLOCK_ROOF_INNER.get()) ||
                        blockBelow.equals(ModBlocks.BLOCK_ROOF_OUTER.get()) ||
                        blockBelow.equals(ModBlocks.BLOCK_OVERHANG.get()) ||
                        blockBelow.equals(ModBlocks.BLOCK_OVERHANG_LEFTSTRUT.get()) ||
                        blockBelow.equals(ModBlocks.BLOCK_OVERHANG_RIGHTSTRUT.get()) ||
                blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS.get()) ||
                blockBelow.equals(ModBlocks.BLOCK_ROOF_TRUSS_STAND.get())
        )
        {
            if(blockState.getValue(STATE) != STATE_ROOF){
                    update = true;
                    blockState = blockState.setValue(FACING, blockState.getValue(FACING_ORG));
                    blockState = blockState.setValue(STATE, STATE_ROOF);
            }
        } else{
                if(blockState.getValue(STATE) != STATE_GABLE){
                    update = true;
                    blockState = blockState.setValue(STATE, STATE_GABLE);
                }
        }

        if(update && place){
            level.setBlockAndUpdate(blockPos, blockState);
        }
        return blockState;
    }

    public BlockState checkForTop(BlockState blockState, Level level, BlockPos blockPos, boolean place){
        boolean update = false;

        BlockPos blockPosBelow = blockPos.below();
        BlockPos blockPosBelowInfrontPos = blockPosBelow.relative(blockState.getValue(FACING));
        BlockPos blockPosBelowBehindPos = blockPosBelow.relative(blockState.getValue(FACING).getOpposite());
        BlockPos blockPosBelowClockwisePos = blockPosBelow.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosBelowCounterClockwisePos = blockPosBelow.relative(blockState.getValue(FACING).getCounterClockWise());

        Block blockBelowInfront = level.getBlockState(blockPosBelowInfrontPos).getBlock();
        Block blockBelowBehind = level.getBlockState(blockPosBelowBehindPos).getBlock();
        Block blockBelowClockwise = level.getBlockState(blockPosBelowClockwisePos).getBlock();
        Block blockBelowCounterClockwise = level.getBlockState(blockPosBelowCounterClockwisePos).getBlock();

        int neighbor = getNeighbor(level, blockState, blockPos);

        // Test if a Block is a Top-Block
        if((blockBelowInfront.equals(ROOF_BLOCK) && blockBelowBehind.equals(ROOF_BLOCK) ||
                blockBelowClockwise.equals(ROOF_BLOCK) && blockBelowCounterClockwise.equals(ROOF_BLOCK)))
        {
            // Test if a Top Block has 2 Neighbors on one Axis
            if(neighbor == 5 || neighbor == 10)
            {
                // Set Block to a Top Block
                if(blockState.getValue(STATE) != STATE_TOP) {
                    // Rotate block if neihbors are not in line with the Block
                    if(neighbor == 5) {
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                    }
                    update = true;
                    blockState = blockState.setValue(STATE, STATE_TOP);
                }
            } else {
                if(neighbor == 0 || neighbor == 1 || neighbor == 2 || neighbor == 4 || neighbor == 8) // Check if it has none / only 1 Neighbor
                {
                    BlockState backupBlockState = blockState;
                    // Rotate Block 90deg if placed vertical on rooftop
                    if(blockBelowInfront.equals(ROOF_BLOCK) && blockBelowBehind.equals(ROOF_BLOCK))
                    {
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                    }

                    //Update neighbor after Rotation for inline Placement to determan if edge needs to be fliped
                    neighbor = getNeighbor(level, blockState, blockPos);

                    // Rotate 180deg if Edge block is facing to a anthoer EdgeBlock
                    if(neighbor == 8)
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getOpposite()); // Rotate so the Block is facing in the right Direction

                    if(!blockState.equals(backupBlockState)) {
                        update = true;
                        blockState = blockState.setValue(STATE, STATE_TOP_EDGE);
                    }
                }
            }
        }else {
            // Check if Block is a Top-Center Block (all side are roofs)
            if(neighbor == 15){
                if(blockState.getValue(STATE) != STATE_TOP_CROSS){
                    update = true;
                    blockState = blockState.setValue(STATE, STATE_TOP_CROSS);
                }
            } else {
                // Check if Block is a 3Way-Top Block
                if(neighbor == 7 || neighbor == 11 || neighbor == 13 || neighbor == 14){ // Check if it has 3 neighbors
                    // Set block to 3Way-Top Block
                    if(blockState.getValue(STATE) != STATE_TOP_T){
                        //TODO Correct the rotation depending on neighbors

                        update = true;
                        blockState = blockState.setValue(STATE, STATE_TOP_T);
                    }
                } else {
                    // Check if Block is a 2 Way-Top Block
                    if(neighbor == 3 || neighbor == 6 || neighbor == 12 || neighbor == 9){ // Check if Block has 2 neighbors
                        // Set block to 2Way-Top Block
                        {
                            if(blockState.getValue(STATE) != STATE_TOP_L){
                                //TODO Correct the rotation depending on neighbors

                                update = true;
                                blockState = blockState.setValue(STATE, STATE_TOP_L);
                            }
                        }
                    }
                }
            }
        }

        // Update Block in world
        if(update && place){
            level.setBlockAndUpdate(blockPos, blockState);
        }
        return blockState;
    }

    /**
     * @param level
     * @param pos
     * @return 1 if neighbor is a roof_top type block, otherwise 0
     */
    private int getNeighbor(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        if(block.equals(ROOF_BLOCK))
            if(level.getBlockState(pos).getValue(STATE) >= STATE_TOP)
                return 1;
        return 0;
    }

    private int getNeighbor(Level level, BlockState blockState, BlockPos blockPos){

        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());
        BlockPos blockPosInfront = blockPos.relative(blockState.getValue(FACING));
        BlockPos blockPosBehind = blockPos.relative(blockState.getValue(FACING).getOpposite());

        // set on bit for each Neighbor ROOF_BLOCK
        int neighbor =
                getNeighbor(level, blockPosSideClockwise) |
                        getNeighbor(level, blockPosInfront) << 1 |
                        getNeighbor(level, blockPosSideCounterClockwise) << 2 |
                        getNeighbor(level, blockPosBehind) << 3;
        return neighbor;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(FACING_ORG);
        builder.add(STATE);
    }
}
