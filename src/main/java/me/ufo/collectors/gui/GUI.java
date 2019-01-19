package me.ufo.collectors.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Consumer;

public abstract class GUI implements InventoryHolder {

    @Getter @Setter private Consumer<InventoryClickEvent> consumer;

    public abstract Inventory getInventory();

}
