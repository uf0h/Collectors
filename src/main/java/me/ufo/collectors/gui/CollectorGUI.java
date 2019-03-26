package me.ufo.collectors.gui;

import com.massivecraft.factions.entity.MPlayer;
import lombok.Getter;
import lombok.Setter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.integration.Econ;
import me.ufo.collectors.integration.Outpost;
import me.ufo.collectors.util.NBTItem;
import me.ufo.collectors.util.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CollectorGUI extends GUI {

    @Getter @Setter private Inventory inventory;
    private Collector collector;

    public CollectorGUI(Collector collector) {
        final FileConfiguration config = CollectorsPlugin.getInstance().getConfig();

        this.collector = collector;

        int size = config.getInt("gui-size");

        this.inventory = Bukkit.createInventory(this, size, ChatColor.DARK_GRAY.toString() + "Collector Menu");

        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemStack.setItemMeta(itemMeta);

        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, itemStack);
        }

        final String PATH = "info-item.";

        ItemStack book = new ItemStack(Material.getMaterial(config.getString(PATH + "material")));
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(Style.translate(config.getString(PATH + "name")));
        bookMeta.setLore(Style.translateLines(config.getStringList(PATH + "lore")));
        book.setItemMeta(bookMeta);

        this.inventory.setItem(4, book);

        CollectorsPlugin.getInstance().getServer().getScheduler().runTask(CollectorsPlugin.getInstance(), () -> {
            for (CollectionType collectionType : CollectionType.values()) {
                if (collectionType == CollectionType.CAVE_SPIDER) continue;

                inventory.setItem(collectionType.getSlot(), collectionType.getItemStack(this.collector));
            }

            this.collector.getViewers().forEach(viewer -> CollectorsPlugin.getInstance().getServer().getPlayer(viewer).updateInventory());
        });

        this.setConsumer(event -> {
            final ItemStack item = event.getCurrentItem();

            if (item == null || !item.hasItemMeta()) {
                return;
            }

            if (item.getType() == Material.getMaterial(config.getString(PATH + "material"))) {
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

            if (collectionType == CollectionType.CREEPER) {
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
            } else {
                if (this.collector.getAmountOfCollectionType(collectionType) < 100) {
                    player.sendMessage(ChatColor.RED.toString() + "There must be at least 100 " + ChatColor.YELLOW.toString() + collectionType.toString() + ChatColor.RED.toString() + " to sell.");
                    return;
                }

                double sellPrice = collectionType.getSellPrice();

                if (Outpost.isFactionControllingOutpost(MPlayer.get(player).getFaction())) {
                    sellPrice *= 2;
                }

                if (Econ.depositAmountToPlayer(player, (100 * sellPrice))) {
                    this.collector.decrement(collectionType, 100);
                    player.sendMessage(ChatColor.GREEN.toString() + "+$" + (100 * sellPrice) + ChatColor.RED.toString() + " from selling 100 " + ChatColor.YELLOW.toString() + collectionType.toString() + ChatColor.RED.toString() + ".");
                } else {
                    player.sendMessage(ChatColor.RED.toString() + "Error: Unable to sell 100 " + ChatColor.YELLOW.toString() + collectionType.toString() + ChatColor.RED.toString() + ".");
                    return;
                }
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
