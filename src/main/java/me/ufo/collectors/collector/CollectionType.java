package me.ufo.collectors.collector;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.util.NBTItem;
import me.ufo.collectors.util.Style;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CollectionType {

  CACTUS(Material.CACTUS),
  SUGAR_CANE(Material.SUGAR_CANE),

  CHICKEN(EntityType.CHICKEN),
  //BLAZE(EntityType.BLAZE),
  RABBIT(EntityType.RABBIT),
  SPIDER(EntityType.SPIDER),
  CAVE_SPIDER(EntityType.CAVE_SPIDER),
  ZOMBIE(EntityType.ZOMBIE),
  SKELETON(EntityType.SKELETON),

  CREEPER(EntityType.CREEPER),
  PIG(EntityType.PIG),
  COW(EntityType.COW),
  PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
  ENDERMAN(EntityType.ENDERMAN),
  IRON_GOLEM(EntityType.IRON_GOLEM),
  VILLAGER(EntityType.VILLAGER),
  WITCH(EntityType.WITCH);

  private final FileConfiguration config = CollectorsPlugin.getInstance().getConfig();
  private final String PATH = "collection-types." + this + ".";

  private Material material;
  private EntityType entityType;
  private List<String> lore;

  @Getter private double sellPrice;
  @Getter private int slot;

  CollectionType(Material material) {
    this.material = material;
  }

  CollectionType(EntityType entityType) {
    this.entityType = entityType;
  }

  // ... store sell prices
  public static boolean initialize(CollectorsPlugin plugin) {
    try {
      for (CollectionType collectionType : CollectionType.values()) {
        // ... cave spiders are included within the spider type
        if (collectionType == CollectionType.CAVE_SPIDER) continue;
        final String PATH = "collection-types." + collectionType + ".";

        if (plugin.getConfig().get(PATH) != null) {
          // ... creepers are not given a sell price as they deposit tnt into the players inventory
          if (collectionType != CollectionType.CREEPER)
            collectionType.sellPrice = plugin.getConfig().getDouble(PATH + "sell-price");
          collectionType.lore = Style.translate(plugin.getConfig().getStringList(PATH + "lore"));
          collectionType.slot = plugin.getConfig().getInt(PATH + "gui-slot");
        } else {
          return false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static CollectionType parse(EntityType entityType) {
    for (CollectionType collectionType : CollectionType.values()) {
      if (collectionType.entityType == entityType) return collectionType;
    }
    return null;
  }

  public static CollectionType parse(Material material) {
    for (CollectionType collectionType : CollectionType.values()) {
      if (collectionType.material == material) return collectionType;
    }
    return null;
  }

  public ItemStack getItemStack(Collector collector) {
    String name = Style.translate(config.getString(PATH + "name"));
    Material material = Material.getMaterial(config.getString(PATH + "material"));
    int durability = config.getInt(PATH + "durability");

    ItemStack itemStack = new ItemStack(material, 1, (short) durability);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(name);
    itemMeta.setLore(this.getLore(collector));
    itemStack.setItemMeta(itemMeta);

    return new NBTItem(itemStack).set("CollectionItem", this.toString()).buildItemStack();
  }

  public List<String> getLore(Collector collector) {
    return this.lore.stream().map(s ->
        StringUtils.replace(s, "%amount%", String.valueOf(collector.getAmountOfCollectionType(this)))).collect(Collectors.toList());
  }

}