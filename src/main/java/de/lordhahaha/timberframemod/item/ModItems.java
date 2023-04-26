package de.lordhahaha.timberframemod.item;

import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.tab.ModCreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Timberframemod.MOD_ID);

    // Register all items of the Mod
    public static final RegistryObject<Item> WATTLE_DAUB = ITEMS.register("wattle_daub", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));

    // Frames
    public static final RegistryObject<Item> FRAME_BASIC = ITEMS.register("frame_basic", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_DOWN = ITEMS.register("frame_down", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_UP = ITEMS.register("frame_up", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> CROSS_FRAME = ITEMS.register("frame_cross", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_CROSS_SMALL_STRAIGHT = ITEMS.register("frame_cross_small_straight", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_CROSS_SMALL_CURVED = ITEMS.register("frame_cross_small_curved", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_CROSS_RING = ITEMS.register("frame_cross_ring", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_STRAIGHT_SINGLE = ITEMS.register("frame_straight_single", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_STRAIGHT_DOUBLE = ITEMS.register("frame_straight_double", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_TOP_LEFT = ITEMS.register("frame_top_left", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_TOP_RIGHT = ITEMS.register("frame_top_right", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_BOTTOM_LEFT = ITEMS.register("frame_bottom_left", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_BOTTOM_RIGHT = ITEMS.register("frame_bottom_right", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_EDGE = ITEMS.register("frame_edge", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_CEILING_EDGE = ITEMS.register("frame_ceiling_edge", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_CEILING = ITEMS.register("frame_ceiling", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_HEXA = ITEMS.register("frame_hexa", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_MAN_LEFT = ITEMS.register("frame_man_left", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_MAN_RIGHT = ITEMS.register("frame_man_right", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_PEDIMENT_LEFT = ITEMS.register("frame_pediment_left", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_PEDIMENT_RIGHT = ITEMS.register("frame_pediment_right", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_SHUTTER_LEFT = ITEMS.register("frame_shutter_left", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_SHUTTER_RIGHT = ITEMS.register("frame_shutter_right", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_DOUBLE_UP = ITEMS.register("frame_double_up", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> FRAME_DOUBLE_DOWN = ITEMS.register("frame_double_down", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> RAW_TILE = ITEMS.register("raw_tile", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> ROOF_TILE = ITEMS.register("roof_tile", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> CARPENTERS_HAMMER = ITEMS.register("carpenters_hammer", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> CARPENTERS_PLANE = ITEMS.register("carpenters_plane", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));
    public static final RegistryObject<Item> CARPENTERS_SAW = ITEMS.register("carpenters_saw", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.TIMBERFRAME_TAB)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
