package de.lordhahaha.timberframemod.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public class WoodworkingBenchRecipe implements Recipe<Container>{
    private final ResourceLocation id;
    private final ItemStack result;
    private final NonNullList<Ingredient> recipeItems;

    public WoodworkingBenchRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.result = result;
        this.recipeItems = recipeItems;
    }
    @Override
    public boolean matches(Container container, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        return recipeItems.get(0).test(container.getItem(1));
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }
    @Override
    public ItemStack assemble(Container p_44001_) {
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return result.copy();
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.BLOCK_WOODWORKING_BENCH.get());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<WoodworkingBenchRecipe>{
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "wood_working";
    }

    public static class Serializer implements RecipeSerializer<WoodworkingBenchRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Timberframemod.MOD_ID, "wood_working");

        @Override
        public WoodworkingBenchRecipe fromJson(ResourceLocation id, JsonObject recipe) {
            JsonArray ingredientArray;
            if(GsonHelper.isArrayNode(recipe, "ingredient")){
                System.out.println("Array");
                ingredientArray = GsonHelper.getAsJsonArray(recipe, "ingredient");
                System.out.println(ingredientArray);
            } else {
                System.out.println("Obj");
                ingredientArray = Ingredient.fromJson(GsonHelper.getAsJsonObject(recipe, "ingredient")).toJson().getAsJsonArray();
            }

            NonNullList<Ingredient> ingredientSet = NonNullList.withSize(1, Ingredient.EMPTY);
            for (int i = 0; i < ingredientSet.size(); i++) {
                ingredientSet.set(i, Ingredient.fromJson(ingredientArray.get(i)));
            }

            String resultLocation = GsonHelper.getAsString(recipe, "result");
            int resultCount = GsonHelper.getAsInt(recipe, "count");

            ItemStack resultItem = new ItemStack(Registry.ITEM.get(new ResourceLocation(resultLocation)), resultCount);
            return new WoodworkingBenchRecipe(id, resultItem, ingredientSet);
        }

        @Override
        public @Nullable WoodworkingBenchRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buff) {
            NonNullList<Ingredient> ingredient = NonNullList.withSize(1, Ingredient.EMPTY);

            ItemStack result = buff.readItem();
            for (int i = 0; i < ingredient.size(); i++) {
                ingredient.set(i, Ingredient.fromNetwork(buff));
            }

            return new WoodworkingBenchRecipe(id, result, ingredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, WoodworkingBenchRecipe recipe) {
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buff);
            }
            buff.writeItem(recipe.result);
        }
    }

}
