package de.lordhahaha.timberframemod.item.custom.timberframeToolItemActions;

import de.lordhahaha.timberframemod.block.custom.RoofBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class TimerframeToolItemActionRotate implements TimerframeToolItemAction {
    @Override
    public InteractionResult useOnAction(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        BlockState blockState = level.getBlockState(blockPos);

        // Rotate RoofBlock
        if (blockState.getBlock() instanceof RoofBlock) {
            blockState = rotateBlockState(blockState, player.isCrouching());
            blockState = blockState.setValue(RoofBlock.MANUAL, true);
            level.setBlockAndUpdate(blockPos, blockState);

            return InteractionResult.SUCCESS;
        }

        // Return success without rotation if the clicked block is not a RoofBlock
        return InteractionResult.PASS;
    }

    private BlockState rotateBlockState(BlockState originalState, boolean invertDirection) {
        // Get the current direction property
        DirectionProperty facingProperty = null;
        for (Property<?> property : originalState.getProperties()) {
            if (property instanceof DirectionProperty) {
                facingProperty = (DirectionProperty) property;
                break;
            }
        }

        if (facingProperty == null) {
            return originalState;
        }

        // Get the current facing direction
        Direction currentDirection = originalState.getValue(facingProperty);
        Direction rotatedDirection = currentDirection;
        if(!invertDirection)
            rotatedDirection = currentDirection.getClockWise();
        else
            rotatedDirection = currentDirection.getCounterClockWise();

        return originalState.setValue(facingProperty, rotatedDirection);
    }
}
