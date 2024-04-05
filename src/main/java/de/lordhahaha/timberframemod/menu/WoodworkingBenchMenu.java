package de.lordhahaha.timberframemod.menu;

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
            WoodworkingBenchMenu.this.slotsChanged(this);
            WoodworkingBenchMenu.this.slotUpdateListener.run();
        }
    };
    public final Container containerForExtra = new SimpleContainer(1) {
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
        this.level = inventory.player.level();

        // Add Inventory Slots
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }

        this.inputSlotBlock = this.addSlot(new Slot(this.containerForBlock, 0, 20, 33));
        this.inputSlotExtra = this.addSlot(new Slot(this.containerForExtra, 0, 20, 52));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            public void onTake(Player player, ItemStack itemStack) {
                itemStack.onCraftedBy(player.level(), player, itemStack.getCount());
                WoodworkingBenchRecipe recipe = recipes.get(getSelectedRecipeIndex());

                int[] amounts = recipe.getIngredientsAmount();
                ItemStack itemstack = WoodworkingBenchMenu.this.inputSlotBlock.remove(amounts[0]);
                ItemStack itemStackExtra = WoodworkingBenchMenu.this.inputSlotExtra.remove(amounts[1]);

                if (!itemstack.isEmpty() && !itemStackExtra.isEmpty()) {
                    WoodworkingBenchMenu.this.setupResultSlot();
                }

                containerLevelAccess.execute((p_40364_, p_40365_) -> {
                    long l = p_40364_.getGameTime();
                    if (WoodworkingBenchMenu.this.lastSoundTime != l) {
                        p_40364_.playSound((Player) null, p_40365_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        WoodworkingBenchMenu.this.lastSoundTime = l;
                    }

                });
                super.onTake(player, itemStack);
            }
        });



        this.addDataSlot(this.selectedRecipeIndex);
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.BLOCK_WOODWORKING_BENCH.get());
    }

    public boolean clickMenuButton(Player player, int index) {
        if (this.isValidRecipeIndex(index)) {
            this.selectedRecipeIndex.set(index);
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
            this.resultSlot.set(woodworkingBenchRecipe.assemble(getInputContainer(), this.level.registryAccess()));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(itemStack, slot);
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((level, blockPos) -> {
            this.clearContainer(player, this.containerForBlock);
            this.clearContainer(player, this.containerForExtra);
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
