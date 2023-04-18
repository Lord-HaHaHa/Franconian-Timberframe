package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

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

        if(block.equals(Blocks.AIR)) { // Only check if ew block is placeda n
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
                blockBelow.equals(Blocks.AIR)
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

        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());
        BlockPos blockPosInfront = blockPos.relative(blockState.getValue(FACING));
        BlockPos blockPosBehind = blockPos.relative(blockState.getValue(FACING).getOpposite());

        int neighbor =
        // set on bit for each Neighbor ROOF_BLOCK
             getNeighbor(level, blockPosSideClockwise) |
             getNeighbor(level, blockPosInfront) << 1 |
             getNeighbor(level, blockPosSideCounterClockwise) << 2 |
             getNeighbor(level, blockPosBehind) << 3;

        // Test if a Block is a Top-Block
        if((blockBelowInfront.equals(ROOF_BLOCK) && blockBelowBehind.equals(ROOF_BLOCK) ||
                blockBelowClockwise.equals(ROOF_BLOCK) && blockBelowCounterClockwise.equals(ROOF_BLOCK)))
        {
            // Test if a Top Block has 2 Neighbors on one Axis
            if((neighbor == 5 || neighbor == 10))
            {
                System.out.println("Set as Top");
                // Set Block to a Top Block
                if(blockState.getValue(STATE) != STATE_TOP) {
                    // Rotate block if neihbors are not in line with the Block
                    if(neighbor == 10) {
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                    }
                    update = true;
                    blockState = blockState.setValue(STATE, STATE_TOP); // Set to Top
                }
            } else{
                // Set Block to a Top-Edge Block
                if(blockState.getValue(STATE) != 5 || neighbor == 1 || neighbor == 4){
                    // Rotate Edge block depending on neighbor
                    if(neighbor == 8)
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());
                    if(neighbor == 2)
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                    if(neighbor == 1)
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getOpposite()); // Rotate so the Block is facing in the right Direction

                    update = true;
                    blockState = blockState.setValue(STATE, STATE_TOP_EDGE); // Set to Top-End
                }
            }
        }else {
            // Check if Block is a Top-Center Block (all side are roofs)
            if(neighbor == 15){
                if(blockState.getValue(STATE) != STATE_TOP_CROSS){
                    update = true;
                    blockState = blockState.setValue(STATE, STATE_TOP_CROSS); // Set to Top-Center
                }
            }else{
                // Check if Block is a 3Way-Top Block
                if(neighbor == 7 || neighbor == 11 || neighbor == 13 || neighbor == 14){
                    // Set block to 3Way-Top Block
                    if(blockState.getValue(STATE) != STATE_TOP_T){
                        update = true;
                        blockState = blockState.setValue(STATE, STATE_TOP_T);
                    }
                } else {
                    // Check if Block is a 2 Way-Top Block
                    if(neighbor == 3 || neighbor == 6 || neighbor == 12 || neighbor == 9){
                        // Set block to 2Way-Top Block
                        {
                            if(blockState.getValue(STATE) != STATE_TOP_L){
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(FACING_ORG);
        builder.add(STATE);
    }
}
