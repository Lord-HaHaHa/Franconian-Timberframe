package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

public class RoofTrussBlock extends Block{

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public RoofTrussBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        //BlockState blockStateBehind = context.getLevel().getBlockState(context.getClickedPos().relative(context.getHorizontalDirection()));

        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());

        return blockState;
    }

    private static final VoxelShape TOP_SHAPE = Block.box(0,0,0,16,3,16);
    private static final VoxelShape A_SHAPE = Block.box(0,0,7,7,3,9);
    private static final VoxelShape B_SHAPE = Block.box(9,0,7,16,3,9);
    private static final VoxelShape C_SHAPE = Block.box(7,0,0,9,3,7);
    private static final VoxelShape D_SHAPE = Block.box(7,0,9,9,3,16);
    private static final VoxelShape MIDDLE_SHAPE = Block.box(7,0,7,9,3,9);
    private static final VoxelShape STAND_SHAPE = Block.box(7,0,7,9,16,9);

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {

        //these blocks do not need rotated shapes

        if (blockState.getBlock().equals(ModBlocks.BLOCK_ROOF_TRUSS_STAND_SOLO.get())) {
            return STAND_SHAPE;
        }
        if (blockState.getBlock().equals(ModBlocks.BLOCK_ROOF_TRUSS_4WAY.get())) {
            VoxelShape voxelshape = Shapes.or(MIDDLE_SHAPE, A_SHAPE);
            voxelshape = Shapes.or(voxelshape, B_SHAPE);
            voxelshape = Shapes.or(voxelshape, C_SHAPE);
            voxelshape = Shapes.or(voxelshape, D_SHAPE);
            return voxelshape;
        }

        //the following blocks need to have shapes that are rotated

        //TODO: remove TOP_SHAPE when all rotational shapes are done

        if (blockState.getBlock().equals(ModBlocks.BLOCK_ROOF_TRUSS_3WAY.get())) {
            //TODO: add rotational shape
            return TOP_SHAPE;
        }
        if (blockState.getBlock().equals(ModBlocks.BLOCK_ROOF_TRUSS_3WAY.get())) {
            //TODO: add rotational shape
            return TOP_SHAPE;
        }
        if (blockState.getBlock().equals(ModBlocks.BLOCK_ROOF_TRUSS_2WAY.get())) {
            //TODO: add rotational shape
            return TOP_SHAPE;
        }
        if (blockState.getBlock().equals(ModBlocks.BLOCK_ROOF_TRUSS_EDGE.get())) {
            //TODO: add rotational shape
            return TOP_SHAPE;
        }

        //this shape is common for all other blocks

        //TODO: add rotational shape
        VoxelShape voxelshape = Shapes.or(TOP_SHAPE, STAND_SHAPE);
        return voxelshape;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
