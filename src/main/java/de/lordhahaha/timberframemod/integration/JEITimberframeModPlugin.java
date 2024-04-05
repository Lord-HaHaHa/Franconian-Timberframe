package de.lordhahaha.timberframemod.integration;

import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.recipe.WoodworkingBenchRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEITimberframeModPlugin implements IModPlugin {

    public static RecipeType<WoodworkingBenchRecipe> WOODWORKING_TYPE = new RecipeType<>(WoodworkingBenchRecipeCategory.UID,WoodworkingBenchRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Timberframemod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new WoodworkingBenchRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<WoodworkingBenchRecipe> recipes = rm.getAllRecipesFor(WoodworkingBenchRecipe.Type.INSTANCE);
        registration.addRecipes(WOODWORKING_TYPE, recipes);
    }

    public void registerGuiHandler(IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
    }
}
