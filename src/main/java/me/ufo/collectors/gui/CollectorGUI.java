package me.ufo.collectors.gui;

import lombok.Getter;
import lombok.Setter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.util.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CollectorGUI extends GUI {

    @Getter @Setter private Inventory inventory;
    private Collector collector;

    public CollectorGUI(Collector collector) {
        this.collector = collector;
        this.inventory = Bukkit.createInventory(this, 45, ChatColor.DARK_GRAY.toString() + "Collector Menu");

        for (CollectionType collectionType : CollectionType.values()) {
            inventory.setItem(collectionType.getSlot(), collectionType.getItemStack(this.collector));
        }

        this.collector.getViewers().forEach(viewer -> CollectorsPlugin.getInstance().getServer().getPlayer(viewer).updateInventory());

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
            final Player player = (Player) event.getWhoClicked();

            switch (collectionType) {
                case CREEPER:
                    if (this.collector.getAmountOfCollectionType(collectionType) == 0) {
                        player.sendMessage(ChatColor.RED.toString() + "There is no TNT to withdraw.");
                        return;
                    }

                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.RED.toString() + "You cannot withdraw anymore TNT with a full inventory.");
                        return;
                    }

                    final int tntToBeGiven = (this.collector.getAmountOfCollectionType(collectionType) > 64 ? 64 : this.collector.getAmountOfCollectionType(collectionType));
                    player.getInventory().addItem(new ItemStack(Material.TNT, tntToBeGiven));

                    this.collector.decrement(collectionType, tntToBeGiven);
                    break;
                default:
                    if (this.collector.getAmountOfCollectionType(collectionType) < 100) {
                        player.sendMessage(ChatColor.RED.toString() + "There must be at least 100 " + ChatColor.YELLOW.toString() + collectionType.toString() + ChatColor.RED.toString() + " to sell.");
                        return;
                    }

                    this.collector.decrement(collectionType, 100);
                    break;
            }

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
            this.collector.getViewers().forEach(viewer -> CollectorsPlugin.getInstance().getServer().getPlayer(viewer).updateInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
