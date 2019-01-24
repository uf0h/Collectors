package me.ufo.collectors.collector;

import lombok.Getter;
import lombok.Setter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.util.NBTItem;
import me.ufo.collectors.util.Skulls;
import me.ufo.collectors.util.Style;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public enum CollectionType {

    CACTUS(Material.CACTUS),
    SUGAR_CANE(Material.SUGAR_CANE),

    CREEPER(EntityType.CREEPER),
    PIG(EntityType.PIG),
    COW(EntityType.COW),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
    ENDERMAN(EntityType.ENDERMAN),
    IRON_GOLEM(EntityType.IRON_GOLEM),
    VILLAGER(EntityType.VILLAGER);

    @Getter private Material material;
    @Getter private EntityType entityType;
    @Getter @Setter private double sellPrice;

    CollectionType(Material material) {
        this.material = material;
    }

    CollectionType(EntityType entityType) {
        this.entityType = entityType;
    }

    // ... store sell prices
    public static boolean initialize(CollectorsPlugin plugin) {
        for (CollectionType collectionType : CollectionType.values()) {
            if (collectionType == CollectionType.CREEPER) continue;
            final String PATH = "collection-types." + collectionType + ".sell-price";

            if (plugin.getConfig().get(PATH) != null) {
                collectionType.setSellPrice(plugin.getConfig().getDouble(PATH));
            } else {
                return false;
            }
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

    private final FileConfiguration config = CollectorsPlugin.getInstance().getConfig();
    private final String PATH = "collection-types." + this + ".";

    public ItemStack getItemStack(Collector collector) {
        String name = Style.translate(config.getString(PATH + "name"));
        Material material = Material.getMaterial(config.getString(PATH + "material"));
        int durability = config.getInt(PATH + "durability");

        ItemStack itemStack;

        switch (this) {
            case CACTUS:
                itemStack = Skulls.Skull.CACTUS.get();
                break;
            case SUGAR_CANE:
                itemStack = Skulls.Skull.SUGARCANE.get();
                break;
            case CREEPER:
                itemStack = Skulls.Skull.TNT.get();
                break;
            case PIG:
                itemStack = Skulls.Skull.PIG.get();
                break;
            case COW:
                itemStack = Skulls.Skull.COW.get();
                break;
            case PIG_ZOMBIE:
                itemStack = Skulls.Skull.ZOMBIE_PIGMAN.get();
                break;
            case ENDERMAN:
                itemStack = Skulls.Skull.ENDERMAN.get();
                break;
            case IRON_GOLEM:
                itemStack = Skulls.Skull.GOLEM.get();
                break;
            case VILLAGER:
                itemStack = Skulls.Skull.VILLAGER.get();
                break;
            default:
                itemStack = new ItemStack(material, 1, (short) durability);
                break;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(this.getLore(collector));
        itemStack.setItemMeta(itemMeta);

        return new NBTItem(itemStack).set("CollectionItem", this.toString()).buildItemStack();
    }

    public List<String> getLore(Collector collector) {
        return Style.translateLines(this.config.getStringList(PATH + "lore").stream().map(s ->
            s.replace("%amount%", String.valueOf(collector.getAmountOfCollectionType(this))))
                .collect(Collectors.toList()));
    }

    public int getSlot() {
        return this.config.getInt(PATH + "gui-slot");
    }

}
