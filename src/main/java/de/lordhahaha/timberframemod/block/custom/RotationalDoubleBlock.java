package de.lordhahaha.timberframemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class RotationalDoubleBlock extends RotationalBlock{
    public static final EnumProperty<Position> POSITION = EnumProperty.create("position", Position.class);
    public RotationalDoubleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Position pos = context.getPlayer().isCrouching() ? Position.TOP : Position.BOTTOM;
        Level level = context.getLevel();

        BlockPos connectedBlockPos = pos == Position.BOTTOM ? blockPos.above() : blockPos.below();
        BlockState connectedBlockState = level.getBlockState(connectedBlockPos);
        if(connectedBlockState.canBeReplaced(context))
        {
            BlockState newBlock = this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection())
                    .setValue(POSITION, pos);

            return newBlock;
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity p_52752_, ItemStack p_52753_) {
        if(blockState != null){
            level.setBlock(getConnectedBlockPos(blockState, blockPos),
                    blockState.setValue(POSITION, blockState.getValue(POSITION) == Position.BOTTOM ? Position.TOP : Position.BOTTOM),
                    3);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockPos connectedBlockPos = getConnectedBlockPos(blockState, blockPos);
        level.destroyBlock(connectedBlockPos, false);
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    public BlockPos getConnectedBlockPos(BlockState blockState, BlockPos blockPos){
        if(blockState.getValue(POSITION) == Position.BOTTOM)
            return blockPos.above();
        else
            return blockPos.below();
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POSITION));
    }

    public enum Position implements StringRepresentable {
        TOP("top"), BOTTOM("bottom");

        private final String name;

        Position(final String name){
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
