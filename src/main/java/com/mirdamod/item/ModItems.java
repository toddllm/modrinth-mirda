package com.mirdamod.item;

import com.mirdamod.MirdaMod;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, MirdaMod.MODID);

    // Bocow - Mirda's weapon (axe+sword hybrid)
    public static final DeferredHolder<Item, Item> BOCOW =
        ITEMS.register("bocow", () -> new BocowItem(new Item.Properties()
            .stacksTo(1)
            .fireResistant()));

    // Altar compass - shows players where Mirda's altar is
    public static final DeferredHolder<Item, Item> ALTAR_COMPASS =
        ITEMS.register("altar_compass", () -> new AltarCompassItem(new Item.Properties()
            .stacksTo(1)));

    // Crystal heart - dropped when Mirda is defeated
    public static final DeferredHolder<Item, Item> CRYSTAL_HEART =
        ITEMS.register("crystal_heart", () -> new Item(new Item.Properties()
            .stacksTo(1)
            .fireResistant()));
}
