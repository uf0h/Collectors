package me.ufo.collectors.collector;

import lombok.Getter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.util.NBTItem;
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

    CREEPER(EntityType.CREEPER),
    PIG(EntityType.PIG);

    @Getter private Material material;
    @Getter private EntityType entityType;

    CollectionType(Material material) {
        this.material = material;
    }

    CollectionType(EntityType entityType) {
        this.entityType = entityType;
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

        ItemStack itemStack = new ItemStack(material, 1, (short) durability);
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
