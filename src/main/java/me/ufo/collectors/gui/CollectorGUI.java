package me.ufo.collectors.gui;

import lombok.Setter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.integration.Econ;
import me.ufo.collectors.util.ItemBuilder;
import me.ufo.collectors.util.NBTItem;
import me.ufo.collectors.util.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CollectorGUI extends GUI {

  private final static CollectorsPlugin PLUGIN = CollectorsPlugin.getInstance();

  // Config values...
  private final static String GUI_NAME = ChatColor.DARK_GRAY.toString() + "Collector Menu";
  private final static int GUI_SIZE = PLUGIN.getConfig().getInt("gui-size");
  // Items...
  private final static ItemStack GUI_PANE =
    new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7).name("").build();
  private final static ItemStack GUI_BOOK =
    new ItemBuilder(Material.getMaterial(PLUGIN.getConfig().getString("info-item.material")))
      .name(Style.translate(PLUGIN.getConfig().getString("info-item.name")))
      .lore(Style.translate(PLUGIN.getConfig().getStringList("info-item.lore")))
      .build();

  // 5 seconds...
  private static final long MAX_NS_PER_TICK = (long) (3000 * 1E6);

  @Setter
  private Inventory inventory;
  private final Collector collector;
  private long lastUpdated;

  public CollectorGUI(final Collector collector) {
    this.collector = collector;
    this.inventory = Bukkit.createInventory(this, GUI_SIZE, GUI_NAME);
    this.lastUpdated = System.nanoTime();

    for (int i = 0; i < GUI_SIZE; i++) {
      this.inventory.setItem(i, GUI_PANE);
    }

    this.inventory.setItem(4, GUI_BOOK);

    /*Bukkit.getScheduler()
      .runTaskAsynchronously(PLUGIN, () -> {*/
        for (final CollectionType collectionType : CollectionType.cachedValues) {
          if (!collectionType.isEnabled()) {
            continue;
          }
          if (collectionType == CollectionType.CAVE_SPIDER) {
            continue;
          }

          inventory.setItem(collectionType.getSlot(), collectionType.getItemStack(this.collector));
        }
      //});

    this.setConsumer(event -> {
      final ItemStack item = event.getCurrentItem();

      if (item == null || !item.hasItemMeta()) {
        return;
      }

      if (item.getType() == GUI_BOOK.getType()) {
        return;
      }

      if (item.getType() == Material.AIR ||
          item.getType() == Material.STAINED_GLASS_PANE ||
          item.getType() == Material.THIN_GLASS) {
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
          player
            .sendMessage(ChatColor.RED.toString() + "You cannot withdraw anymore TNT with a full inventory.");
          return;
        }

        final int tntToBeGiven = Math.min(this.collector.getAmountOfCollectionType(collectionType), 64);
        player.getInventory().addItem(new ItemStack(Material.TNT, tntToBeGiven));

        this.collector.decrement(collectionType, tntToBeGiven);
      } else {
        if (this.collector.getAmountOfCollectionType(collectionType) < 100) {
          player.sendMessage(ChatColor.RED.toString() + "There must be at least 100 " + ChatColor.YELLOW
            .toString() + collectionType.toString() + ChatColor.RED.toString() + " to sell.");
          return;
        }

        final double sellPrice = collectionType.getSellPrice();

        /*if (Outpost.isFactionControllingOutpost(MPlayer.get(player).getFaction())) {
          sellPrice *= 2;
        }*/

        if (Econ.depositAmountToPlayer(player, (100 * sellPrice))) {
          this.collector.decrement(collectionType, 100);
          player.sendMessage(ChatColor.GREEN.toString() + "+$" + CollectorsPlugin.DF.format((100 * sellPrice)) + ChatColor.RED
            .toString() + " from selling 100 " + ChatColor.YELLOW.toString() + collectionType
                               .toString() + ChatColor.RED.toString() + ".");
        } else {
          player.sendMessage(ChatColor.RED.toString() + "Error: Unable to sell 100 " + ChatColor.YELLOW
            .toString() + collectionType.toString() + ChatColor.RED.toString() + ".");
          return;
        }
      }

      this.update(collectionType);
    });
  }

  public void update(final CollectionType collectionType) {
    if (!this.collector.getViewers().isEmpty()) {
      if ((System.nanoTime() - this.lastUpdated) >= MAX_NS_PER_TICK) {
        return;
      }

      final ItemStack itemStack = new ItemStack(this.inventory.getItem(collectionType.getSlot()));
      final ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setLore(collectionType.getLore(this.collector));
      itemStack.setItemMeta(itemMeta);

      this.inventory.setItem(collectionType.getSlot(), itemStack);
      this.collector.getViewers()
        .forEach(viewer -> CollectorsPlugin.getInstance().getServer().getPlayer(viewer).updateInventory());

      this.lastUpdated = System.nanoTime();
    }
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }

}
