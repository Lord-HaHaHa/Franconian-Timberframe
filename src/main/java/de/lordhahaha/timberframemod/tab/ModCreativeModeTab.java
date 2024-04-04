package de.lordhahaha.timberframemod.tab;

import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Timberframemod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTab {
    public static CreativeModeTab TIMBERFRAME_TAB;

    @SubscribeEvent
    public static void registerCrativeModeTab(CreativeModeTabEvent.Register event){
        TIMBERFRAME_TAB = event.registerCreativeModeTab(new ResourceLocation(Timberframemod.MOD_ID, "timberframe_tab"),
                builder -> builder.icon(() -> new ItemStack(ModItems.WATTLE_DAUB.get()))
                        .title(Component.literal("timerframe")).build());
    }
}
