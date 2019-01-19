package me.ufo.collectors.gui;

import lombok.Getter;
import lombok.Setter;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.util.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CollectorGUI extends GUI {

    @Getter @Setter private Inventory inventory;
    private Collector collector;

    public CollectorGUI(Collector collector) {
        this.collector = collector;
        this.inventory = Bukkit.createInventory(this, 45, ChatColor.DARK_GRAY.toString() + "Collector Sell Menu");

        for (CollectionType collectionType : CollectionType.values()) {
            inventory.setItem(collectionType.getSlot(), collectionType.getItemStack(this.collector));
        }

        this.setConsumer(event -> {
            final ItemStack item = event.getCurrentItem();

            if (item == null || !item.hasItemMeta()) {
                return;
            }

            if (item.getType() == Material.AIR || item.getType() == Material.STAINED_GLASS_PANE || item.getType() == Material.THIN_GLASS) {
                return;
            }

            final NBTItem nbtItem = new NBTItem(item);
            if (nbtItem.getString("CollectionItem") == null && nbtItem.getString("CollectionItem").isEmpty()) {
                return;
            }

            final CollectionType collectionType = CollectionType.valueOf(nbtItem.getString("CollectionItem"));

            collector.decrement(collectionType, 1);
            this.update(collectionType);
        });
    }

    public void update(CollectionType collectionType) {
        if (!this.collector.getViewers().isEmpty()) {
            ItemStack itemStack = new ItemStack(this.inventory.getItem(collectionType.getSlot()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(collectionType.getLore(this.collector));
            itemStack.setItemMeta(itemMeta);

            this.inventory.setItem(collectionType.getSlot(), itemStack);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
