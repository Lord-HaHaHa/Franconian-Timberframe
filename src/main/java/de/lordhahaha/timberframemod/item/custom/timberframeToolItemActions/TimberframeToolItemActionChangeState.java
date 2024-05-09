package de.lordhahaha.timberframemod.item.custom.timberframeToolItemActions;

import de.lordhahaha.timberframemod.block.custom.RoofBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class TimberframeToolItemActionChangeState implements TimerframeToolItemAction{
    @Override
    public InteractionResult useOnAction(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        BlockState blockState = level.getBlockState(blockPos);;

        if (blockState.getBlock() instanceof RoofBlock) {
            int min = -1;
            int max = -1;
            if(RoofBlock.ROOFS_TOPS.contains(blockState.getValue(RoofBlock.STATE))) {
                min = RoofBlock.ROOFS_TOPS.stream().min(Comparator.naturalOrder()).get();
                max = RoofBlock.ROOFS_TOPS.stream().max(Comparator.naturalOrder()).get();
            } else if (RoofBlock.ROOF_SIDES.contains(blockState.getValue(RoofBlock.STATE))){
                min = RoofBlock.ROOF_SIDES.stream().min(Comparator.naturalOrder()).get();
                max = RoofBlock.ROOF_SIDES.stream().max(Comparator.naturalOrder()).get();
            } else {
                return InteractionResult.PASS;
            }
            int i = blockState.getValue(RoofBlock.STATE);

            if (player.isCrouching()) {
                i = i - 1;
            } else {
                i = i + 1;
            }
            System.out.println("New BlockState number: " + i);
            System.out.println("Min: " + min + " Max: " + max);
            // Reset if State is over / underflowing
            if (i > max || i < min)
                i = min;
            System.out.println("After Overflow: "+ i);
            blockState = blockState.setValue(RoofBlock.STATE, i);
            blockState = blockState.setValue(RoofBlock.MANUAL, true);
            System.out.println("New BlockState: " + blockState.getValue(RoofBlock.STATE));
            level.setBlockAndUpdate(blockPos, blockState);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
