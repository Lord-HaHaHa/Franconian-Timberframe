package de.lordhahaha.timberframemod.menu;

import com.google.common.collect.Lists;
import de.lordhahaha.timberframemod.Timberframemod;
import de.lordhahaha.timberframemod.block.ModBlocks;
import de.lordhahaha.timberframemod.recipe.WoodworkingBenchRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class WoodworkingBenchMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<WoodworkingBenchRecipe> recipes = Lists.newArrayList();
    private ItemStack input = ItemStack.EMPTY;
    long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener = () -> {
    };
    public final Container container = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            WoodworkingBenchMenu.this.slotsChanged(this);
            WoodworkingBenchMenu.this.slotUpdateListener.run();
        }
    };
    final ResultContainer resultContainer = new ResultContainer();

    public WoodworkingBenchMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public WoodworkingBenchMenu(int id, Inventory inventory, final ContainerLevelAccess containerLevelAccess) {
        super(ModMenuTypes.WOODWORKING_MENU.get(), id);
        this.access = containerLevelAccess;
        this.level = inventory.player.level;
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            public boolean mayPlace(ItemStack p_40362_) {
                return false;
            }

            public void onTake(Player player, ItemStack itemStack) {
                itemStack.onCraftedBy(player.level, player, itemStack.getCount());
                WoodworkingBenchMenu.this.resultContainer.awardUsedRecipes(player);
                ItemStack itemstack = WoodworkingBenchMenu.this.inputSlot.remove(1);
                if (!itemstack.isEmpty()) {
                    WoodworkingBenchMenu.this.setupResultSlot();
                }

                containerLevelAccess.execute((p_40364_, p_40365_) -> {
                    long l = p_40364_.getGameTime();
                    if (WoodworkingBenchMenu.this.lastSoundTime != l) {
                        p_40364_.playSound((Player)null, p_40365_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        WoodworkingBenchMenu.this.lastSoundTime = l;
                    }

                });
                super.onTake(player, itemStack);
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.BLOCK_WOODWORKING_BENCH.get());
    }

    public boolean clickMenuButton(Player player, int p_40310_) {
        if (this.isValidRecipeIndex(p_40310_)) {
            this.selectedRecipeIndex.set(p_40310_);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int p_40335_) {
        return p_40335_ >= 0 && p_40335_ < this.recipes.size();
    }

    public void slotsChanged(Container p_40302_) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.setupRecipeList(p_40302_, itemstack);
        }

    }

    private void setupRecipeList(Container p_40304_, ItemStack p_40305_) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!p_40305_.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(WoodworkingBenchRecipe.Type.INSTANCE, p_40304_, this.level);
            System.out.println(this.recipes.size());
        }
    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            WoodworkingBenchRecipe woodworkingBenchRecipe = this.recipes.get(this.selectedRecipeIndex.get());
            this.resultContainer.setRecipeUsed(woodworkingBenchRecipe);
            this.resultSlot.set(woodworkingBenchRecipe.assemble(this.container));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    public boolean canTakeItemForPickAll(ItemStack p_40321_, Slot p_40322_) {
        return p_40322_.container != this.resultContainer && super.canTakeItemForPickAll(p_40321_, p_40322_);
    }

    public ItemStack quickMoveStack(Player player, int p_40329_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_40329_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (p_40329_ == 1) {
                item.onCraftedBy(itemstack1, player.level, player);
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (p_40329_ == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(WoodworkingBenchRecipe.Type.INSTANCE, new SimpleContainer(itemstack1), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_40329_ >= 2 && p_40329_ < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_40329_ >= 29 && p_40329_ < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((p_40313_, p_40314_) -> {
            this.clearContainer(player, this.container);
        });
    }

    public int getNumRecipes() {return this.recipes.size();}
    public boolean hasInputItem() {return this.inputSlot.hasItem() && !this.recipes.isEmpty();}
    public List<WoodworkingBenchRecipe> getRecipes() {return this.recipes;}
    public int getSelectedRecipeIndex() {return this.selectedRecipeIndex.get();}
    public void registerUpdateListener(Runnable runnable) {this.slotUpdateListener = runnable;}

}
