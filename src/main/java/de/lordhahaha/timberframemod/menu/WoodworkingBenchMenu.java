package de.lordhahaha.timberframemod.menu;

import ca.weblite.objc.Message;
import com.google.common.collect.Lists;
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
import net.minecraft.world.level.Level;

import java.text.MessageFormat;
import java.util.List;

public class WoodworkingBenchMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<WoodworkingBenchRecipe> recipes = Lists.newArrayList();
    private ItemStack inputBlock = ItemStack.EMPTY;
    private ItemStack inputExtra = ItemStack.EMPTY;
    long lastSoundTime;
    final Slot resultSlot;
    final Slot inputSlotBlock;
    final Slot inputSlotExtra;
    Runnable slotUpdateListener = () -> {
    };
    public final Container containerForBlock = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            System.out.println("containerForBlock: Changed");
            WoodworkingBenchMenu.this.slotsChanged(this);
            WoodworkingBenchMenu.this.slotUpdateListener.run();
        }
    };
    public final Container containerForExtra = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            System.out.println("containerForExtra: Changed");
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
        this.inputSlotBlock = this.addSlot(new Slot(this.containerForBlock, 0, 20, 33));
        this.inputSlotExtra = this.addSlot(new Slot(this.containerForExtra, 0, 20, 52));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            public void onTake(Player player, ItemStack itemStack) {
                itemStack.onCraftedBy(player.level, player, itemStack.getCount());
                WoodworkingBenchMenu.this.resultContainer.awardUsedRecipes(player);

                WoodworkingBenchRecipe recipe = recipes.get(getSelectedRecipeIndex());

                ItemStack itemstack = WoodworkingBenchMenu.this.inputSlotBlock.remove(recipe.amountIngredient1);
                ItemStack itemStackExtra = WoodworkingBenchMenu.this.inputSlotExtra.remove(recipe.amountIngredient2);

                if (!itemstack.isEmpty() && !itemStackExtra.isEmpty()) {
                    System.out.println("onTake: SetupResultSlot");
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

    public boolean clickMenuButton(Player player, int index) {
        if (this.isValidRecipeIndex(index)) {
            this.selectedRecipeIndex.set(index);
            System.out.println(MessageFormat.format("Selected Index: {0} | {1}", index, getSelectedRecipeIndex()));
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int p_40335_) {
        return p_40335_ >= 0 && p_40335_ < this.recipes.size();
    }

    public void slotsChanged(Container containerChanged) {
        ItemStack itemStackBlock = this.inputSlotBlock.getItem();
        ItemStack itemStackExtra = this.inputSlotExtra.getItem();
        if (!itemStackBlock.is(this.inputBlock.getItem())) {
            this.inputBlock = itemStackBlock.copy();
            this.setupRecipeList(containerChanged, itemStackBlock);
        }
        if(!itemStackExtra.is(this.inputExtra.getItem())) {
            this.inputExtra = itemStackExtra.copy();
            this.setupRecipeList(containerChanged, itemStackExtra);
        }

    }

    private void setupRecipeList(Container container1, ItemStack itemStackBlock) {
        this.recipes.clear();
        System.out.println("setupRecipeList: Set RecipeIndex to -1");
        this.selectedRecipeIndex.set(-1);

        this.resultSlot.set(ItemStack.EMPTY);

        SimpleContainer container = getInputContainer();

        if (!itemStackBlock.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(WoodworkingBenchRecipe.Type.INSTANCE, container, this.level);
        }
    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            WoodworkingBenchRecipe woodworkingBenchRecipe = this.recipes.get(this.selectedRecipeIndex.get());
            this.resultContainer.setRecipeUsed(woodworkingBenchRecipe);
            this.resultSlot.set(woodworkingBenchRecipe.assemble(getInputContainer()));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        System.out.println(MessageFormat.format("canTakeItemForPickAll: ", itemStack.getItem().getName(itemStack)));
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(itemStack, slot);
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        System.out.println(MessageFormat.format("quickMoveStack: used slotIndex: {0}", slotIndex));
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (slotIndex == 3) {
                item.onCraftedBy(itemstack1, player.level, player);
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (slotIndex == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(WoodworkingBenchRecipe.Type.INSTANCE, new SimpleContainer(itemstack1), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 2 && slotIndex < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 29 && slotIndex < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
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
            this.clearContainer(player, this.containerForBlock);
        });
    }

    public int getNumRecipes() {return this.recipes.size();}
    public boolean hasInputItem() {return this.inputSlotBlock.hasItem() && !this.recipes.isEmpty();}
    public List<WoodworkingBenchRecipe> getRecipes() {return this.recipes;}
    public int getSelectedRecipeIndex() {return this.selectedRecipeIndex.get();}
    public void registerUpdateListener(Runnable runnable) {this.slotUpdateListener = runnable;}
    private SimpleContainer getInputContainer(){
        SimpleContainer container = new SimpleContainer(2);
        container.setItem(0, this.containerForBlock.getItem(0));
        container.setItem(1, this.containerForExtra.getItem(0));
        return container;
    }

}
