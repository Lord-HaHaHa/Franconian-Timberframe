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
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 3);
    public Block ROOF_BLOCK;
    public RoofBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
                .setValue(STATE, state);



        // set Corner
        checkForCorner(blockState, level, blockPos);

        checkForCorner(blockState, level, blockPos);
        return blockState;
    }


    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPosNeighbor, boolean bool) {
        super.neighborChanged(blockState, level, blockPos, block, blockPosNeighbor, bool);

        if(block.equals(Blocks.AIR) || block.equals(ROOF_BLOCK)){
            checkForPediment(blockState, level, blockPos);
        }

        if(block.equals(Blocks.AIR)) { // Only check if a new block is placed
            checkForCorner(blockState, level, blockPos);
        }
    }

    public void checkForCorner(BlockState blockState, Level level, BlockPos blockPos){
        boolean update = false;

        ROOF_BLOCK = ModBlocks.BLOCK_ROOF_MAIN.get();

        BlockPos blockPosBehind = blockPos.relative(blockState.getValue(FACING));
        BlockPos blockPosInfront = blockPos.relative(blockState.getValue(FACING).getOpposite());
        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());

        if(level.getBlockState(blockPosInfront).getBlock().equals(ROOF_BLOCK) ||
                level.getBlockState(blockPosBehind).getBlock().equals(ROOF_BLOCK))
        {

            if(level.getBlockState(blockPosSideClockwise).getBlock().equals(ROOF_BLOCK) ||
                    level.getBlockState(blockPosSideCounterClockwise).getBlock().equals(ROOF_BLOCK)){

                // Checks if its an outer or inner coner
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

        if(update){
            level.setBlockAndUpdate(blockPos, blockState);
        }

    }

    public void checkForPediment(BlockState blockState, Level level, BlockPos blockPos){
        // Checks if a given Block is a Pediment
        boolean update = false;

        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());

        Block blockClockwise = level.getBlockState(blockPosSideClockwise).getBlock();
        Block blockCounterClockwise = level.getBlockState(blockPosSideCounterClockwise).getBlock();

        System.out.println(MessageFormat.format("Clockwise: POS: {0} BLOCK: {1}", blockPosSideClockwise, blockClockwise));
        System.out.println(MessageFormat.format("CounterClockwise: POS: {0} BLOCK: {1}", blockPosSideCounterClockwise, blockCounterClockwise));

        if(blockClockwise.equals(ROOF_BLOCK) &&
                blockCounterClockwise.equals(ROOF_BLOCK))
        {
            update = true;
            blockState = blockState.setValue(STATE,0);
        } else{
            update = true;
            blockState = blockState.setValue(STATE, 3);
        }

        if(update){
            level.setBlockAndUpdate(blockPos, blockState);
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(STATE);
    }
}
