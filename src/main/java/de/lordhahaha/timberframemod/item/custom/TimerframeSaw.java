package de.lordhahaha.timberframemod.item.custom;

import de.lordhahaha.timberframemod.block.custom.RoofBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TimerframeSaw extends DiggerItem {
    public TimerframeSaw(Properties properties) {
        super(1, -3f, Tiers.IRON, BlockTags.MINEABLE_WITH_AXE, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flags) {
        components.add(Component.translatable("tooltip.timberframemod.carpenters_hammer.tooltip"));
        super.appendHoverText(stack, level, components, flags);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockpos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        // Rotate RoofBlock
        if (blockstate.getBlock() instanceof RoofBlock) {
            BlockState rotatedState = rotateBlockState(blockstate, player.isCrouching());
            level.setBlockAndUpdate(blockpos, rotatedState);

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
