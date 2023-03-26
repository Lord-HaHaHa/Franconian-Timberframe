package de.lordhahaha.timberframemod;

import com.mojang.logging.LogUtils;
import de.lordhahaha.timberframemod.block.ModBlocks;
import de.lordhahaha.timberframemod.item.ModItems;
import de.lordhahaha.timberframemod.menu.ModMenuTypes;
import de.lordhahaha.timberframemod.menu.WoodworkingBenchScreen;
import de.lordhahaha.timberframemod.recipe.ModRecipes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModRecipes.register(modEventBus);

        ModMenuTypes.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents{
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            MenuScreens.register(ModMenuTypes.WOODWORKING_MENU.get(), WoodworkingBenchScreen::new);
        }
    }
}
