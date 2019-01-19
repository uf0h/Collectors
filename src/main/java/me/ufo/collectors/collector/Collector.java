package me.ufo.collectors.collector;

import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.Getter;
import me.ufo.collectors.CollectorsPlugin;
import org.bukkit.Location;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Collector {

    @Getter public static ConcurrentHashMap<String, Collector> collectorCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<CollectionType, Integer> amounts = new ConcurrentHashMap<>();
    private Location location;

    private transient Set<UUID> viewers = new HashSet<>();

    public Collector(Location location) {
        this.location = location;

        for (CollectionType collectionType : CollectionType.values())
            this.amounts.putIfAbsent(collectionType, 0);
    }

    public static void add(Location location) {
        collectorCache.put(serialize(location), new Collector(location));
    }

    public static Collector get(Location location) {
        return collectorCache.get(serialize(location));
    }

    public void increment(CollectionType collectionType) {
        this.amounts.put(collectionType, this.amounts.get(collectionType) + 1);
    }

    public void increment(CollectionType collectionType, int amount) {
        this.amounts.put(collectionType, this.amounts.get(collectionType) + amount);
    }

    public void decrement(CollectionType collectionType, int amount) {
        this.amounts.put(collectionType, (this.amounts.get(collectionType) - amount) < 0 ? this.amounts.get(collectionType) - amount : 0);
    }

    public void remove() {
        this.amounts.clear();
        this.viewers.forEach(uuid -> CollectorsPlugin.getInstance().getServer().getPlayer(uuid).closeInventory());
        this.viewers.clear();

        collectorCache.remove(serialize(this.location));
    }

    public int getAmountOfCollectionType(CollectionType collectionType) {
        return this.amounts.get(collectionType);
    }

    public static boolean chunkHasCollector(Location location) {
        return collectorCache.containsKey(serialize(location));
    }

    public static boolean isCollector(Location location) {
        if (collectorCache.isEmpty()) return false;
        return collectorCache.get(serialize(location)).getLocation().toString().equals(location.toString());
    }

    public static String serialize(Location location) {
        return new StringBuilder()
                .append(location.getWorld().getName())
                .append("::")
                .append(location.getChunk().getX())
                .append("::")
                .append(location.getChunk().getZ()).toString();
    }

    public static boolean initialize() {
        CompletableFuture<ConcurrentHashMap<String, Collector>> collectors = load();

        try {
            if (collectors.get() != null && !collectors.get().isEmpty()) {
                collectorCache = collectors.get();

                collectorCache.forEach((k, v) -> {
                    v.viewers = new HashSet<>();
                });

                CollectorsPlugin.getInstance().getLogger().info("Collectors have been loaded into memory (" + collectorCache.size() + ").");
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
                return CollectorsPlugin.getInstance().getGson().fromJson(reader, new TypeToken<ConcurrentHashMap<String, Collector>>() {}.getType());
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
