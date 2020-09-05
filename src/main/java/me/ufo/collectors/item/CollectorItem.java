package me.ufo.collectors.item;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.util.Style;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CollectorItem {

  private static final FileConfiguration config = CollectorsPlugin.getInstance().getConfig();
  private static final String PATH = "collector-item.";

  public static ItemStack get() {
    final ItemStack item = new ItemStack(Material.BEACON);
    final ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName(Style.translate(config.getString(PATH + "name")));
    itemMeta.setLore(Style.translate(config.getStringList(PATH + "lore")));
    item.setItemMeta(itemMeta);

    return item;
  }

  public static ItemStack get(final int amount) {
    final ItemStack item = new ItemStack(Material.BEACON, amount);
    final ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName(Style.translate(config.getString(PATH + "name")));
    itemMeta.setLore(Style.translate(config.getStringList(PATH + "lore")));
    item.setItemMeta(itemMeta);

    return item;
  }

  public static boolean is(final ItemStack itemStack) {
    if (itemStack == null || !itemStack.hasItemMeta()) {
      return false;
    }
    final ItemStack collectorItem = CollectorItem.get();
    if (itemStack.getType() != collectorItem.getType()) {
      return false;
    }

    return itemStack.isSimilar(collectorItem);
  }

}
