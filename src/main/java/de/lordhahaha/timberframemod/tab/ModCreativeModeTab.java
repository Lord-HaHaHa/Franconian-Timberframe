package de.lordhahaha.timberframemod.tab;

import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Timberframemod.MOD_ID);
    public static RegistryObject<CreativeModeTab> TIMBERFRAME_TAB = CREATIVE_MODE_TABS.register("timberframe_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WATTLE_DAUB.get()))
            .title(Component.literal("timerframe")).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
