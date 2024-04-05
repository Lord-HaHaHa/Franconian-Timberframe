package de.lordhahaha.timberframemod.block.custom;

import de.lordhahaha.timberframemod.menu.WoodworkingBenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;


public class WoodworkingBenchBlock extends Block {
    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 14, 0, 16, 16, 16),
            Block.box(14, 0, 0, 16, 14, 2),
            Block.box(14, 0, 14, 16, 14, 16),
            Block.box(0, 0, 14, 2, 14, 16),
            Block.box(0, 0, 0, 2, 14, 2));

    public WoodworkingBenchBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
        return new SimpleMenuProvider((i, inventory, player) -> {
            return new WoodworkingBenchMenu(i, inventory, ContainerLevelAccess.create(level, blockPos));
        }, CONTAINER_TITLE);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    public boolean useShapeForLightOcclusion(BlockState p_57109_) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection().getOpposite());
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
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

}
