package de.lordhahaha.timberframemod.recipe;

import de.lordhahaha.timberframemod.Timberframemod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.*;
import org.checkerframework.checker.signature.qual.Identifier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Timberframemod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<WoodworkingBenchRecipe>> WOODWORKING_SERIALIZER =
            SERIALIZERS.register("woodworking", () -> WoodworkingBenchRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
