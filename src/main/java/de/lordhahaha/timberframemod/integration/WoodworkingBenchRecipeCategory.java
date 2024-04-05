package de.lordhahaha.timberframemod.integration;

import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.block.ModBlocks;
import de.lordhahaha.timberframemod.recipe.WoodworkingBenchRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class WoodworkingBenchRecipeCategory implements IRecipeCategory<WoodworkingBenchRecipe> {

    public final static ResourceLocation UID = new ResourceLocation(Timberframemod.MOD_ID,"wood_working");
    public final static ResourceLocation TEXTURE = new ResourceLocation(Timberframemod.MOD_ID, "textures/gui/woodworking_jei.png");

    // 1.20.1 Addions
    public static final RecipeType<WoodworkingBenchRecipe> WOODWORKING_TYPE =
            new RecipeType<>(UID, WoodworkingBenchRecipe.class);
    //---

    private final IDrawable background;
    private final IDrawable icon;

    public WoodworkingBenchRecipeCategory(IGuiHelper helper)
    {
        this.background = helper.createDrawable(TEXTURE,0,0,176,85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,new ItemStack(ModBlocks.BLOCK_WOODWORKING_BENCH.get()));
    }
    @Override
    public RecipeType<WoodworkingBenchRecipe> getRecipeType() {
        return JEITimberframeModPlugin.WOODWORKING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Woodworking");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WoodworkingBenchRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients=recipe.getIngredients();
        ItemStack items1 = new ItemStack(ingredients.get(0).getItems()[0].getItem());
        int[] amount=recipe.getIngredientsAmount();
        items1.setCount(amount[0]);
        builder.addSlot(RecipeIngredientRole.INPUT,20,33).addItemStack(items1);
        if (ingredients.size()>1) {
            ItemStack items2 = new ItemStack(ingredients.get(1).getItems()[0].getItem());
            items2.setCount(amount[1]);
            builder.addSlot(RecipeIngredientRole.INPUT,20,52).addItemStack(items2);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT,143,33).addItemStack(recipe.getResultItem(null));
    }
}
