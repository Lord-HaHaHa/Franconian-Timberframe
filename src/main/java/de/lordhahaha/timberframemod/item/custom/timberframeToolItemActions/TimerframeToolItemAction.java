package de.lordhahaha.timberframemod.item.custom.timberframeToolItemActions;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public interface TimerframeToolItemAction {
    InteractionResult useOnAction(UseOnContext useOnContext);
}
