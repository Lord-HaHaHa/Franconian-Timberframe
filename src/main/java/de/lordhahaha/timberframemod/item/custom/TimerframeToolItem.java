package de.lordhahaha.timberframemod.item.custom;

import de.lordhahaha.timberframemod.block.custom.RoofBlock;
import de.lordhahaha.timberframemod.item.custom.timberframeToolItemActions.TimerframeToolItemAction;
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

public class TimerframeToolItem extends DiggerItem {
    private String tooltipPath;
    private TimerframeToolItemAction action;
    public TimerframeToolItem(Properties properties, String tooltipPath, TimerframeToolItemAction action) {
        super(1, -3f, Tiers.IRON, BlockTags.MINEABLE_WITH_AXE, properties);
        this.tooltipPath = tooltipPath;
        this.action = action;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flags) {
        components.add(Component.translatable(this.tooltipPath)); // "tooltip.timberframemod.carpenters_hammer.tooltip"
        super.appendHoverText(stack, level, components, flags);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        // Only perform the Rotation on the Server
        if (level.isClientSide())
            return InteractionResult.sidedSuccess(level.isClientSide);

        return this.action.useOnAction(useOnContext);
    }
}
