package me.ufo.collectors.collector;

import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.Getter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.gui.CollectorGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Collector {

    @Getter public static ConcurrentHashMap<String, Collector> collectorCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<CollectionType, Integer> amounts = new ConcurrentHashMap<>();
    private Location location;

    private transient CollectorGUI collectorGUI;
    private transient Set<UUID> viewers = new HashSet<>();

    public Collector(Location location) {
        this.location = location;

        for (CollectionType collectionType : CollectionType.values()) {
            if (collectionType == CollectionType.CAVE_SPIDER) continue;
            this.amounts.putIfAbsent(collectionType, 0);
        }
    }

    public static void add(Location location) {
        collectorCache.put(serialize(location), new Collector(location));
    }

    public static Collector get(Location location) {
        return collectorCache.get(serialize(location));
    }

    public static Collector get(String world, int chunkX, int chunkZ) {
        return collectorCache.get(serialize(world, chunkX, chunkZ));
    }

    public void increment(CollectionType collectionType) {
        this.amounts.put(collectionType, this.amounts.get(collectionType) + 1);
        if (this.collectorGUI != null) this.collectorGUI.update(collectionType);
    }

    public void increment(CollectionType collectionType, int amount) {
        this.amounts.put(collectionType, this.amounts.get(collectionType) + amount);
        if (this.collectorGUI != null) this.collectorGUI.update(collectionType);
    }

    public void decrement(CollectionType collectionType, int amount) {
        this.amounts.put(collectionType, (this.amounts.get(collectionType) - amount) < 0 ? 0 : this.amounts.get(collectionType) - amount);
        if (this.collectorGUI != null) this.collectorGUI.update(collectionType);
    }

    public void remove(boolean removeBlock) {
        this.amounts.clear();
        this.viewers.forEach(uuid -> CollectorsPlugin.getInstance().getServer().getPlayer(uuid).closeInventory());
        this.viewers.clear();

        if (removeBlock) this.location.getBlock().setType(Material.AIR);

        collectorCache.remove(serialize(this.location));
    }

    public void openInventory(Player player) {
        if (this.collectorGUI == null) {
            this.collectorGUI = new CollectorGUI(Collector.this);
        }

        player.openInventory(this.collectorGUI.getInventory());
        this.viewers.add(player.getUniqueId());
    }

    private void deleteInventory() {
        this.collectorGUI.getInventory().clear();
        this.collectorGUI.setInventory(null);
        this.collectorGUI.setConsumer(null);
        this.collectorGUI = null;
    }

    public static void removeViewer(UUID uuid) {
        CollectorsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CollectorsPlugin.getInstance(), () -> {
            for (Map.Entry<String, Collector> map : collectorCache.entrySet()) {
                if (map.getValue().getViewers().contains(uuid)) {
                    map.getValue().getViewers().remove(uuid);

                    if (map.getValue().getViewers().isEmpty()) map.getValue().deleteInventory();
                    break;
                }
            }
        });
    }

    public static boolean chunkHasCollector(String world, int chunkX, int chunkZ) {
        return collectorCache.containsKey(serialize(world, chunkX, chunkZ));
    }

    public static boolean chunkHasCollector(Location location) {
        return collectorCache.containsKey(serialize(location));
    }

    public static boolean isCollector(Location location) {
        if (collectorCache.isEmpty()) return false;
        return collectorCache.get(serialize(location)).getLocation().toString().equals(location.toString());
    }

    public int getAmountOfCollectionType(CollectionType collectionType) {
        return this.amounts.get(collectionType);
    }

    public static String serialize(String world, int chunkX, int chunkZ) {
        return new StringBuilder()
                .append(world)
                .append("::")
                .append(chunkX)
                .append("::")
                .append(chunkZ).toString();
    }

    public static String serialize(Location location) {
        return new StringBuilder()
                .append(location.getWorld().getName())
                .append("::")
                .append(location.getChunk().getX())
                .append("::")
                .append(location.getChunk().getZ()).toString();
    }

    public static boolean initialize(CollectorsPlugin plugin) {
        CompletableFuture<ConcurrentHashMap<String, Collector>> collectors = load();

        try {
            if (collectors.get() != null && !collectors.get().isEmpty()) {
                collectorCache = collectors.get();

                collectorCache.forEach((k, v) -> {
                    v.viewers = new HashSet<>();
                    for (CollectionType collectionType : CollectionType.values()) {
                        if (collectionType == CollectionType.CAVE_SPIDER) continue;
                        v.amounts.putIfAbsent(collectionType, 0);
                    }
                });

                plugin.getLogger().info("Collectors have been loaded into memory (" + collectorCache.size() + ").");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static CompletableFuture<ConcurrentHashMap<String, Collector>> load() {
        return CompletableFuture.supplyAsync(() -> {
            try (FileReader reader = new FileReader(CollectorsPlugin.getInstance().getDataFolder().toString() + "/data.json")) {
                return CollectorsPlugin.getInstance().getGson().fromJson(reader, new TypeToken<ConcurrentHashMap<String, Collector>>() {
                }.getType());
            } catch (IOException e) {
                CollectorsPlugin.getInstance().getLogger().warning("Failed to load collectors.");
                return null;
            }
        });
    }

    public static void saveall() {
        if (collectorCache.isEmpty()) return;

        CompletableFuture.supplyAsync(() -> {
            try (FileWriter writer = new FileWriter(CollectorsPlugin.getInstance().getDataFolder().toString() + "/data.json")) {
                CollectorsPlugin.getInstance().getGson().toJson(collectorCache, writer);
                return true;
            } catch (IOException e) {
                CollectorsPlugin.getInstance().getLogger().warning("Failed to save collectors.");
                return false;
            }
        });
    }

}
