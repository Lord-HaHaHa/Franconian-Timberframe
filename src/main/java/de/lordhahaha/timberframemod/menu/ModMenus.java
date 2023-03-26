package de.lordhahaha.timberframemod.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.core.Registry;

public class ModMenus <T extends AbstractContainerMenu> implements net.minecraftforge.common.extensions.IForgeMenuType<T>{

    public static final MenuType<WoodworkingBenchMenu> WOODWORKING_BENCH_MENU = register("woodworkingbenchmenu", WoodworkingBenchMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String p_39989_, MenuType.MenuSupplier<T> p_39990_) {
        return Registry.register(Registry.MENU, p_39989_, new MenuType<>(p_39990_));
    }

    @Override
    public T create(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        return null;
    }
}
