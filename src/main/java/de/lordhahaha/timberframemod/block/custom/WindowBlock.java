package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

public class WindowBlock extends RotationalBlock {

    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 15);

    public WindowBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, 0));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacement(context);
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        //BlockState blockStateBehind = context.getLevel().getBlockState(context.getClickedPos().relative(context.getHorizontalDirection()));

        if (blockState.getBlock().equals(ModBlocks.BLOCK_WINDOW.get())) {

            BlockState clockwise=level.getBlockState(blockPos.relative(blockState.getValue(FACING).getClockWise()));
            BlockState counterclockwise=level.getBlockState(blockPos.relative(blockState.getValue(FACING).getCounterClockWise()));
            BlockState above=level.getBlockState(blockPos.above());
            BlockState below=level.getBlockState(blockPos.below());

            blockState = setBlockStateFromNeighbors(blockState, clockwise, counterclockwise, above, below);
        }
        return blockState;
    }

    @NotNull
    private static BlockState setBlockStateFromNeighbors(BlockState blockState, BlockState clockwise, BlockState counterclockwise, BlockState below, BlockState above) {
        int state=0;
        if ( clockwise.getBlock().equals(ModBlocks.BLOCK_WINDOW.get()) && clockwise.getValue(FACING).equals(blockState.getValue(FACING)) )
        {
            state |= 8;
        }
        if ( counterclockwise.getBlock().equals(ModBlocks.BLOCK_WINDOW.get()) && counterclockwise.getValue(FACING).equals(blockState.getValue(FACING)) )
        {
            state |= 2;
        }
        if ( below.getBlock().equals(ModBlocks.BLOCK_WINDOW.get()) && below.getValue(FACING).equals(blockState.getValue(FACING)) )
        {
            state |= 1;
        }
        if ( above.getBlock().equals(ModBlocks.BLOCK_WINDOW.get()) && above.getValue(FACING).equals(blockState.getValue(FACING)) )
        {
            state |= 4;
        }
        blockState = blockState.setValue(STATE, state);
        return blockState;
    }

    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPosNeighbor, boolean bool) {
        System.out.println("Neighbor Changed");
        System.out.println(MessageFormat.format("OWNPOS: {0} Neighbor: {1}", blockPos, blockPosNeighbor));

        super.neighborChanged(blockState, level, blockPos, block, blockPosNeighbor, bool);

        if (blockState.getBlock().equals(ModBlocks.BLOCK_WINDOW.get())) {
            BlockState clockwise = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getClockWise()));
            BlockState counterclockwise = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getCounterClockWise()));
            BlockState above = level.getBlockState(blockPos.above());
            BlockState below = level.getBlockState(blockPos.below());

            blockState = setBlockStateFromNeighbors(blockState, clockwise, counterclockwise, above, below);
            level.setBlockAndUpdate(blockPos, blockState);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STATE);
    }

}
