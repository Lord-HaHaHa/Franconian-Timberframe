package de.lordhahaha.timberframemod.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;

public class WoodworkingBenchRecipe extends SingleItemRecipe {
    public int amountIngredient1;
    public int amountIngredient2;
    public Ingredient ingredientExtra;
    public WoodworkingBenchRecipe(ResourceLocation id, String group, Ingredient ingredientMain, Ingredient pingredientExtra, ItemStack itemStack, int amount1, int amount2) {
        super(Type.INSTANCE, Serializer.INSTANCE, id, group, ingredientMain, itemStack);
        amountIngredient1 = amount1;
        amountIngredient2 = amount2;
        ingredientExtra = pingredientExtra;
    }

    public boolean matches(Container container, Level p_44484_) {
        ItemStack itemStack1 = container.getItem(0);
        ItemStack itemStack2 = container.getItem(1);
        boolean flag = (this.ingredient.test(itemStack1) && itemStack1.getCount() >= amountIngredient1) &&
                (this.ingredientExtra.test(itemStack2) && itemStack2.getCount() >= amountIngredient2);
        return flag;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.BLOCK_WOODWORKING_BENCH.get());
    }

    public int[] getIngredientsAmount(){
        return new int[]{amountIngredient1, amountIngredient2};
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

            int amounts[] = {0,0};
            ArrayList<Ingredient> ingredients = new ArrayList<>();

            JsonObject ingredientOBJ1 = GsonHelper.getAsJsonObject(recipe, "slot1");
            JsonArray ingredients1 = GsonHelper.getAsJsonArray(ingredientOBJ1, "ingredients");
            int count1 = GsonHelper.getAsInt(ingredientOBJ1, "count");
            Ingredient ingredient1 = Ingredient.fromJson(ingredients1);
            ingredients.add(ingredient1);
            amounts[0] = count1;

            JsonObject ingredientOBJ2 = GsonHelper.getAsJsonObject(recipe, "slot2");
            JsonArray ingredients2 = GsonHelper.getAsJsonArray(ingredientOBJ2, "ingredients");
            int count2 = GsonHelper.getAsInt(ingredientOBJ2, "count");
            Ingredient ingredient2 = Ingredient.fromJson(ingredients2);
            ingredients.add(ingredient2);
            amounts[1] = count2;

            String s1 = GsonHelper.getAsString(recipe, "result");
            int resultCount = GsonHelper.getAsInt(recipe, "count");
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)) , resultCount);
            return new WoodworkingBenchRecipe(id, "wood_working", ingredients.get(0), ingredients.get(1), itemStack, amounts[0], amounts[1]);
        }

        @Override
        public @Nullable WoodworkingBenchRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buff) {
            Ingredient ingredient = Ingredient.fromNetwork(buff);
            Ingredient ingredientExtra = Ingredient.fromNetwork(buff);
            int amountIngredient1 = buff.readInt();
            int amountIngredient2 = buff.readInt();
            ItemStack itemStack = buff.readItem();
            return new WoodworkingBenchRecipe(id, "wood_working", ingredient, ingredientExtra, itemStack, amountIngredient1, amountIngredient2);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, WoodworkingBenchRecipe recipe) {
            recipe.ingredient.toNetwork(buff);
            recipe.ingredientExtra.toNetwork(buff);
            buff.writeInt(recipe.amountIngredient1);
            buff.writeInt(recipe.amountIngredient2);
            buff.writeItem(recipe.result);
        }
    }

}
