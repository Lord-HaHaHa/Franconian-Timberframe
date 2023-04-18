package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

public class RoofBlock extends Block{

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final DirectionProperty FACING_ORG = DirectionProperty.create("facing_org", Direction.Plane.HORIZONTAL);
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 8);
    public Block ROOF_BLOCK;
    public RoofBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING_ORG, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, 3));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        int state = this.defaultBlockState().getValue(STATE);
        BlockState blockStateBehind = context.getLevel().getBlockState(context.getClickedPos().relative(context.getHorizontalDirection()));

        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(FACING_ORG, context.getHorizontalDirection())
                .setValue(STATE, state);

        blockState = checkForPediment(blockState, level, blockPos, false);
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
            if(blockState.getValue(STATE) < 4)
                checkForPediment(blockState, level, blockPos, true);
            checkForTop(blockState, level, blockPos, true);
        }

        if(block.equals(Blocks.AIR)) { // Only check if ew block is placeda n
            checkForCorner(blockState, level, blockPos, true);
        }
    }

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
                if(blockState.getValue(STATE) == 0 || blockState.getValue(STATE) == 3)
                {
                    // Checks if it´s an outer or inner coner
                    if(level.getBlockState(blockPosBehind).getBlock().equals(ROOF_BLOCK)) {

                        // rotate facing for the corner block
                        if (level.getBlockState(blockPosSideCounterClockwise).getBlock().equals(ROOF_BLOCK))
                        {
                            blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());
                        }
                        blockState = blockState.setValue(STATE, 1);
                    }
                    else{
                        // rotate facing for the corner block
                        if (level.getBlockState(blockPosSideClockwise).getBlock().equals(ROOF_BLOCK))
                            blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());

                        blockState = blockState.setValue(STATE, 2);
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

    public BlockState checkForPediment(BlockState blockState, Level level, BlockPos blockPos, boolean place){
        // Checks if a given Block is a Pediment
        boolean update = false;

        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());

        Block blockClockwise = level.getBlockState(blockPosSideClockwise).getBlock();
        Block blockCounterClockwise = level.getBlockState(blockPosSideCounterClockwise).getBlock();
        Block blockInfront = level.getBlockState(blockPos.relative(blockState.getValue(FACING))).getBlock();
        Block blockBehind = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getOpposite())).getBlock();
        if((blockClockwise.equals(ROOF_BLOCK) && blockCounterClockwise.equals(ROOF_BLOCK)) || (
                blockInfront.equals(ROOF_BLOCK) && blockBehind.equals(ROOF_BLOCK))
        )
        {
            if(blockState.getValue(STATE) != 0){
                    update = true;
                    blockState = blockState.setValue(FACING, blockState.getValue(FACING_ORG));
                    blockState = blockState.setValue(STATE,0);
            }
        } else{
            if(!(blockState.getValue(STATE) == 1 || blockState.getValue(STATE) == 2))
                if(blockState.getValue(STATE) != 3){
                    update = true;
                    blockState = blockState.setValue(STATE, 3);
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

        Block blockInfront = level.getBlockState(blockPosInfront).getBlock();
        Block blockBehind = level.getBlockState(blockPosBehind).getBlock();
        Block blockClockwise = level.getBlockState(blockPosSideClockwise).getBlock();
        Block blockCounterClockwise = level.getBlockState(blockPosSideCounterClockwise).getBlock();

        int neighbor = 0;
        // set on bit for each Neighbor ROOF_BLOCK
        if(blockClockwise.equals(ROOF_BLOCK))
            if(level.getBlockState(blockPosSideClockwise).getValue(STATE) >= 4)
                neighbor += 1;

        if(blockInfront.equals(ROOF_BLOCK))
            if(level.getBlockState(blockPosInfront).getValue(STATE) >= 4)
                neighbor += 2;

        if(blockCounterClockwise.equals(ROOF_BLOCK))
            if(level.getBlockState(blockPosSideCounterClockwise).getValue(STATE) >= 4)
                neighbor += 4;

        if(blockBehind.equals(ROOF_BLOCK))
            if(level.getBlockState(blockPosBehind).getValue(STATE) >= 4)
                neighbor += 8;

        // Test if a Block is a Top-Block
        if((blockBelowInfront.equals(ROOF_BLOCK) && blockBelowBehind.equals(ROOF_BLOCK) ||
                blockBelowClockwise.equals(ROOF_BLOCK) && blockBelowCounterClockwise.equals(ROOF_BLOCK)))
        {
            // Test if a Top Block has 2 Neighbors on one Axis
            if((neighbor == 5 || neighbor == 10))
            {
                System.out.println("Set as Top");
                // Set Block to a Top Block
                if(blockState.getValue(STATE) != 4) {
                    // Rotate block if neihbors are not in line with the Block
                    if(neighbor == 10) {
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getClockWise());
                    }
                    update = true;
                    blockState = blockState.setValue(STATE,4); // Set to Top
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
                    blockState = blockState.setValue(STATE, 5); // Set to Top-End
                }
            }
        }else {
            // Check if Block is a Top-Center Block (all side are roofs)
            if(neighbor == 15){
                if(blockState.getValue(STATE) != 6){
                    update = true;
                    blockState = blockState.setValue(STATE, 6); // Set to Top-Center
                }
            }else{
                // Check if Block is a 3Way-Top Block
                if(neighbor == 7 || neighbor == 11 || neighbor == 13 || neighbor == 14){
                    // Set block to 3Way-Top Block
                    if(blockState.getValue(STATE) != 7){
                        update = true;
                        blockState = blockState.setValue(STATE, 7);
                    }
                } else {
                    // Check if Block is a 2 Way-Top Block
                    if(neighbor == 3 || neighbor == 6 || neighbor == 12 || neighbor == 9){
                        // Set block to 2Way-Top Block
                        {
                            if(blockState.getValue(STATE) != 8){
                                update = true;
                                blockState = blockState.setValue(STATE, 8);
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
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(FACING_ORG);
        builder.add(STATE);
    }
}
