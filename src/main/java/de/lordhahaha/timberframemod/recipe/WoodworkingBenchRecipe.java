package de.lordhahaha.timberframemod.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.block.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;

public class WoodworkingBenchRecipe extends SingleItemRecipe {
    private int amountIngredient1;
    private int amountIngredient2;
    private Ingredient ingredientExtra;
    public WoodworkingBenchRecipe(ResourceLocation id, String group, Ingredient ingredientMain, Ingredient ingredientExtra, ItemStack itemStack, int amount1, int amount2) {
        super(Type.INSTANCE, Serializer.INSTANCE, id, group, ingredientMain, itemStack);
        amountIngredient1 = amount1;
        amountIngredient2 = amount2;
        this.ingredientExtra = ingredientExtra;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        if (this.ingredientExtra != null)
        {
            nonnulllist.add(this.ingredientExtra);
        }
        return nonnulllist;
    }

    public boolean matches(Container container, Level level) {
        ItemStack itemStack1 = container.getItem(0);
        ItemStack itemStack2 = container.getItem(1);
        if (this.ingredient.test(itemStack1) && itemStack1.getCount() >= amountIngredient1)
        {
          if (this.ingredientExtra != null)
          {
              return (this.ingredientExtra.test(itemStack2) && itemStack2.getCount() >= amountIngredient2);
          } else {
              return true;
          }
        } else {
          return false;
        }
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
            // TODO: refactoring
            JsonObject ingredientOBJ1 = GsonHelper.getAsJsonObject(recipe, "slot1");
            JsonArray ingredients1 = GsonHelper.getAsJsonArray(ingredientOBJ1, "ingredients");
            int count1 = GsonHelper.getAsInt(ingredientOBJ1, "count");
            Ingredient ingredient1 = Ingredient.fromJson(ingredients1);

            JsonObject ingredientOBJ2 = GsonHelper.getAsJsonObject(recipe, "slot2");
            JsonArray ingredients2 = GsonHelper.getAsJsonArray(ingredientOBJ2, "ingredients");
            int count2 = GsonHelper.getAsInt(ingredientOBJ2, "count");
            Ingredient ingredient2 = null;
            if (ingredients2.size()>0)
              ingredient2 = Ingredient.fromJson(ingredients2);

            String s1 = GsonHelper.getAsString(recipe, "result");
            int resultCount = GsonHelper.getAsInt(recipe, "count", 1);

            String outputItemName = GsonHelper.getAsString(recipe, "result");
            ItemStack itemStack = new ItemStack(CraftingHelper.getItem(outputItemName, true), resultCount);
            return new WoodworkingBenchRecipe(id, "wood_working", ingredient1, ingredient2, itemStack, count1, count2);
        }
        @Override
        public @Nullable WoodworkingBenchRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buff) {

            Ingredient ingredient = Ingredient.fromNetwork(buff);
            int amountIngredient1 = buff.readInt();

            Ingredient ingredientExtra = null;
            int amountIngredient2 = 0;
            if (buff.readBoolean()) {
                ingredientExtra = Ingredient.fromNetwork(buff);
                amountIngredient2 = buff.readInt();
            }

            ItemStack itemStack = buff.readItem();

            return new WoodworkingBenchRecipe(id, "wood_working", ingredient, ingredientExtra, itemStack, amountIngredient1, amountIngredient2);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, WoodworkingBenchRecipe recipe) {
            recipe.ingredient.toNetwork(buff);
            buff.writeInt(recipe.amountIngredient1);
            buff.writeBoolean(recipe.ingredientExtra!=null);
            if (recipe.ingredientExtra!=null){
                recipe.ingredientExtra.toNetwork(buff);
                buff.writeInt(recipe.amountIngredient2);
            }
            buff.writeItem(recipe.result);
        }
    }

}
