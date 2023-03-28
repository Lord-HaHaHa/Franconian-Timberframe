package de.lordhahaha.timberframemod.recipe;

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

public class WoodworkingBenchRecipe extends SingleItemRecipe {
    public WoodworkingBenchRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack itemStack) {
        super(Type.INSTANCE, Serializer.INSTANCE, id, group, ingredient, itemStack);
    }

    public boolean matches(Container p_44483_, Level p_44484_) {
        return this.ingredient.test(p_44483_.getItem(0));
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.BLOCK_WOODWORKING_BENCH.get());
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
            Ingredient ingredient = null;

            if(GsonHelper.isArrayNode(recipe, "ingredient")){
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(recipe, "ingredient"));
            }

            String s1 = GsonHelper.getAsString(recipe, "result");
            int resultCount = GsonHelper.getAsInt(recipe, "count");
            ItemStack itemStack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)) , resultCount);
            return new WoodworkingBenchRecipe(id, "wood_working", ingredient, itemStack);
        }

        @Override
        public @Nullable WoodworkingBenchRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buff) {
            Ingredient ingredient = Ingredient.fromNetwork(buff);
            ItemStack itemStack = buff.readItem();
            return new WoodworkingBenchRecipe(id, "wood_working", ingredient, itemStack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buff, WoodworkingBenchRecipe recipe) {
            recipe.ingredient.toNetwork(buff);
            buff.writeItem(recipe.result);
        }
    }

}
