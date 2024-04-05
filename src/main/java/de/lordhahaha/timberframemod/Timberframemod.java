package de.lordhahaha.timberframemod;

import com.mojang.logging.LogUtils;
import de.lordhahaha.timberframemod.block.ModBlocks;
import de.lordhahaha.timberframemod.item.ModItems;
import de.lordhahaha.timberframemod.menu.ModMenuTypes;
import de.lordhahaha.timberframemod.menu.WoodworkingBenchScreen;
import de.lordhahaha.timberframemod.recipe.ModRecipes;
import de.lordhahaha.timberframemod.tab.ModCreativeModeTab;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Timberframemod.MOD_ID)
public class Timberframemod
{
    public static final String MOD_ID = "timberframemod";
    private static final Logger LOGGER = LogUtils.getLogger();
    public Timberframemod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTab.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModRecipes.register(modEventBus);

        ModMenuTypes.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTab() == ModCreativeModeTab.TIMBERFRAME_TAB.get()){
            // Register Items
            event.accept(ModItems.WATTLE_DAUB);
            event.accept(ModItems.FRAME_BASIC);
            event.accept(ModItems.RAW_TILE);
            event.accept(ModItems.ROOF_TILE);
            event.accept(ModItems.CARPENTERS_HAMMER);
            event.accept(ModItems.CARPENTERS_PLANE);
            event.accept(ModItems.CARPENTERS_SAW);

            // Register Blockitems
            event.accept(ModBlocks.BLOCK_WOODWORKING_BENCH);
            event.accept(ModBlocks.BLOCK_SHUTTER);
            event.accept(ModBlocks.BLOCK_PLASTER);
            event.accept(ModBlocks.BLOCK_BASIC);
            event.accept(ModBlocks.BLOCK_DOWN);
            event.accept(ModBlocks.BLOCK_UP);
            event.accept(ModBlocks.BLOCK_CROSS);
            event.accept(ModBlocks.BLOCK_CROSS_SMALL_STRAIGHT);
            event.accept(ModBlocks.BLOCK_CROSS_SMALL_CURVED);
            event.accept(ModBlocks.BLOCK_CROSS_RING);
            event.accept(ModBlocks.BLOCK_STRAIGHT_SINGLE);
            event.accept(ModBlocks.BLOCK_STRAIGHT_DOUBLE);
            event.accept(ModBlocks.BLOCK_DOUBLE_DOWN);
            event.accept(ModBlocks.BLOCK_DOUBLE_UP);
            event.accept(ModBlocks.BLOCK_EDGE);
            event.accept(ModBlocks.BLOCK_CEILING_EDGE);
            event.accept(ModBlocks.BLOCK_CEILING);
            event.accept(ModBlocks.BLOCK_HEXA);
            event.accept(ModBlocks.BLOCK_MAN_LEFT);
            event.accept(ModBlocks.BLOCK_MAN_RIGHT);
            event.accept(ModBlocks.BLOCK_PEDIMENT_LEFT);
            event.accept(ModBlocks.BLOCK_PEDIMENT_RIGHT);
            event.accept(ModBlocks.BLOCK_OVERHANG);
            event.accept(ModBlocks.BLOCK_OVERHANG_RIGHTSTRUT);
            event.accept(ModBlocks.BLOCK_OVERHANG_LEFTSTRUT);
            event.accept(ModBlocks.BLOCK_ROOF);
            event.accept(ModBlocks.BLOCK_ROOF_INNER);
            event.accept(ModBlocks.BLOCK_ROOF_OUTER);
            event.accept(ModBlocks.BLOCK_ROOF_GABLE);
            event.accept(ModBlocks.BLOCK_ROOF_TOP);
            event.accept(ModBlocks.BLOCK_ROOF_TOP_2WAY);
            event.accept(ModBlocks.BLOCK_ROOF_TOP_3WAY);
            event.accept(ModBlocks.BLOCK_ROOF_TOP_CENTER);
            event.accept(ModBlocks.BLOCK_ROOF_TOP_CROSS);
            event.accept(ModBlocks.BLOCK_ROOF_TOP_END);
            event.accept(ModBlocks.BLOCK_ROOF_MAIN);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_2WAY);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_3WAY);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_4WAY);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_EDGE);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_STAND_3WAY);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_STAND_2WAY);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_STAND_SOLO);
            event.accept(ModBlocks.BLOCK_ROOF_TRUSS_STAND_EDGE);
            event.accept(ModBlocks.BLOCK_WINDOW);

        }
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents{
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            MenuScreens.register(ModMenuTypes.WOODWORKING_MENU.get(), WoodworkingBenchScreen::new);
        }
    }
}
