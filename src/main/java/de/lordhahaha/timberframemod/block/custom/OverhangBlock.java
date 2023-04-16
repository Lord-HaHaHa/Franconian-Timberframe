package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.swing.plaf.nimbus.State;

public class OverhangBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0,4);

    public OverhangBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, 0));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        int i = 0;
        if(blockState.getValue(STATE) == 4)
            i = 0;
        else
            i = blockState.getValue(STATE) + 1;

        System.out.println(i);
        blockState = blockState.setValue(STATE, i);
        level.setBlockAndUpdate(blockPos, blockState);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int state = this.defaultBlockState().getValue(STATE);
        BlockState blockStateBehind = context.getLevel().getBlockState(context.getClickedPos().relative(context.getHorizontalDirection()));

        // set edge overhang
        if(blockStateBehind.getBlock() == ModBlocks.BLOCK_EDGE.get()) {
            Direction DirBehind = blockStateBehind.getValue(FACING);
            Direction DirOwn = context.getHorizontalDirection();
            if (DirBehind == Direction.SOUTH && DirOwn == Direction.EAST ||
                    DirBehind == Direction.EAST && DirOwn == Direction.NORTH ||
                    DirBehind == Direction.NORTH && DirOwn == Direction.WEST ||
                    DirBehind == Direction.WEST && DirOwn == Direction.SOUTH) {
                state = 1;
            }
            if (DirBehind == DirOwn) {
                state = 2;
            }
        }
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(STATE, state);
    }


    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPosNeighbor, boolean bool) {
        super.neighborChanged(blockState, level, blockPos, block, blockPosNeighbor, bool);

        // Check if now a Corner
        if(block.equals(Blocks.AIR)){ // Only check if a new block is placed
            checkForCorner(blockState, level, blockPos, block, blockPosNeighbor, bool);
        }
    }

    public void checkForCorner(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPosNeighbor, boolean bool){
        boolean update = false;
        System.out.println("check For Corner");

        BlockPos blockPosBehind = blockPos.relative(blockState.getValue(FACING));
        BlockPos blockPosInfront = blockPos.relative(blockState.getValue(FACING).getOpposite());
        BlockPos blockPosSideClockwise = blockPos.relative(blockState.getValue(FACING).getClockWise());
        BlockPos blockPosSideCounterClockwise = blockPos.relative(blockState.getValue(FACING).getCounterClockWise());

        System.out.println(blockPosBehind);
        System.out.println(blockPosInfront);

        if(level.getBlockState(blockPosInfront).getBlock().equals(ModBlocks.BLOCK_OVERHANG.get()) ||
                level.getBlockState(blockPosBehind).getBlock().equals(ModBlocks.BLOCK_OVERHANG.get()))
        {
            System.out.println("infront is a Overhang block");

            if(level.getBlockState(blockPosSideClockwise).getBlock().equals(ModBlocks.BLOCK_OVERHANG.get()) ||
                    level.getBlockState(blockPosSideCounterClockwise).getBlock().equals(ModBlocks.BLOCK_OVERHANG.get())){

                System.out.println("Block is a corner now");

                // Checks if its an outer or inner coner
                if(level.getBlockState(blockPosBehind).getBlock().equals(ModBlocks.BLOCK_OVERHANG.get())) {
                    if (level.getBlockState(blockPosSideCounterClockwise).getBlock().equals(ModBlocks.BLOCK_OVERHANG.get()))
                    {
                        System.out.println("Rotate");
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());
                    }
                    blockState = blockState.setValue(STATE, 3);
                }
                else{
                    // rotate facing for the corner block
                    if (level.getBlockState(blockPosSideClockwise).getBlock().equals(ModBlocks.BLOCK_OVERHANG.get()))
                        blockState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());

                    blockState = blockState.setValue(STATE, 4);
                }
                update = true;
            }
        }

        if(update){
            System.out.println("Update to Coner");
            level.setBlockAndUpdate(blockPos, blockState);
        }

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(STATE);
    }
}
