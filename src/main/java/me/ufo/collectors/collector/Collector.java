package me.ufo.collectors.collector;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Data;
import lombok.Getter;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.gui.CollectorGUI;
import me.ufo.collectors.item.CollectorItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Data
public class Collector {

  @Getter
  public static Object2ObjectOpenHashMap<String, Collector> collectorCache =
    new Object2ObjectOpenHashMap<>(1000);
  public final static String SEPERATOR = "::";

  private final Object2IntOpenHashMap<CollectionType> amounts =
    new Object2IntOpenHashMap<>(CollectionType.cachedValues.length);
  private final Location location;

  private transient CollectorGUI collectorGUI;
  private transient Set<UUID> viewers = new HashSet<>();

  public Collector(final Location location) {
    this.location = location;

    this.populate();
  }

  private void populate() {
    for (final CollectionType collectionType : CollectionType.cachedValues) {
      if (!collectionType.isEnabled()) {
        continue;
      }
      if (collectionType == CollectionType.CAVE_SPIDER) {
        continue;
      }
      this.amounts.putIfAbsent(collectionType, 0);
    }
  }

  public void increment(final CollectionType collectionType) {
    this.amounts.addTo(collectionType, 1);
    if (this.collectorGUI != null) {
      this.collectorGUI.update(collectionType);
    }
  }

  public void increment(final CollectionType collectionType, final int amount) {
    this.amounts.computeInt(collectionType, (type, i) -> i += amount);
    if (this.collectorGUI != null) {
      this.collectorGUI.update(collectionType);
    }
  }

  public void decrement(final CollectionType collectionType, final int amount) {
    this.amounts.computeInt(collectionType, (type, i) -> Math.max(i - amount, 0));
    if (this.collectorGUI != null) {
      this.collectorGUI.update(collectionType);
    }
  }

  public void drop() {
    amounts.clear();
    this.viewers.forEach(uuid -> CollectorsPlugin.getInstance().getServer().getPlayer(uuid).closeInventory());
    this.viewers.clear();

    this.location.getBlock().setType(Material.AIR);
    this.location.getWorld().dropItem(this.location, CollectorItem.get());

    collectorCache.remove(serialize(this.location));
  }

  public void openInventory(final Player player) {
    if (this.collectorGUI == null) {
      this.collectorGUI = new CollectorGUI(Collector.this);
    }

    player.openInventory(this.collectorGUI.getInventory());
    this.viewers.add(player.getUniqueId());
  }

  private void deleteInventory() {
    this.collectorGUI.setInventory(null);
    this.collectorGUI.setConsumer(null);
    this.collectorGUI = null;
  }

  public int getAmountOfCollectionType(final CollectionType collectionType) {
    return this.amounts.getInt(collectionType);
  }

  public void disable() {
    this.getViewers()
      .forEach(viewer -> CollectorsPlugin.getInstance().getServer().getPlayer(viewer).closeInventory());
  }

  public static void add(final Location location) {
    collectorCache.put(serialize(location), new Collector(location));
  }

  public static Collector get(final Location location) {
    return collectorCache.get(serialize(location));
  }

  public static Collector get(final String world, final int chunkX, final int chunkZ) {
    return collectorCache.get(serialize(world, chunkX, chunkZ));
  }

  public static void removeViewer(final UUID uuid) {
    CollectorsPlugin.getInstance().getServer().getScheduler()
      .runTaskAsynchronously(CollectorsPlugin.getInstance(), () -> {
        collectorCache.object2ObjectEntrySet().fastForEach(entry -> {
          final Collector collector = entry.getValue();
          if (collector.getViewers().contains(uuid)) {
            collector.getViewers().remove(uuid);
            if (collector.getViewers().isEmpty()) {
              collector.deleteInventory();
            }
          }
        });
      });
  }

  public static boolean chunkHasCollector(final String world, final int chunkX, final int chunkZ) {
    return collectorCache.containsKey(serialize(world, chunkX, chunkZ));
  }

  public static boolean chunkHasCollector(final Location location) {
    return collectorCache.containsKey(serialize(location));
  }

  public static boolean isCollector(final Location location) {
    if (collectorCache.isEmpty()) {
      return false;
    }
    return collectorCache.get(serialize(location)).getLocation().toString().equals(location.toString());
  }

  private static String serialize(final String world, final int chunkX, final int chunkZ) {
    return world + SEPERATOR + chunkX + SEPERATOR + chunkZ;
  }

  private static String serialize(final Location location) {
    return location.getWorld().getName() + SEPERATOR + location.getChunk().getX() + SEPERATOR + location
      .getChunk()
      .getZ();
  }

  public static boolean initialize(final CollectorsPlugin plugin) {
    final CompletableFuture<Object2ObjectOpenHashMap<String, Collector>> collectors = load();

    try {
      if (collectors.get() != null && !collectors.get().isEmpty()) {
        collectorCache = collectors.get();

        collectorCache.forEach((k, v) -> {
          v.viewers = new HashSet<>();
          v.populate();
        });

        plugin.getLogger().info("Collectors have been loaded into memory (" + collectorCache.size() + ").");
      }
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private static CompletableFuture<Object2ObjectOpenHashMap<String, Collector>> load() {
    return CompletableFuture.supplyAsync(() -> {
      try (final FileReader reader = new FileReader(
        CollectorsPlugin.getInstance().getDataFolder().toString() + "/data.json")) {
        final Object2ObjectOpenHashMap<String, Collector> out = new Object2ObjectOpenHashMap<>();
        final Map<String, Collector> collectorMap =
          CollectorsPlugin.getInstance().getGson().fromJson(reader, new TypeToken<Map<String, Collector>>() {}.getType());
        if (collectorMap != null) {
          collectorMap.forEach((location, collector) -> {
            if (location != null && collector != null) {
              out.put(location, collector);
            }
          });
        }
        return out;
      } catch (final IOException e) {
        CollectorsPlugin.getInstance().getLogger().warning("Failed to load collectors.");
        return null;
      }
    });
  }

  public static void saveall() {
    if (collectorCache.isEmpty()) {
      return;
    }
    try (final FileWriter writer = new FileWriter(
      CollectorsPlugin.getInstance().getDataFolder().toString() + "/data.json")) {
      CollectorsPlugin.getInstance().getGson().toJson(collectorCache, writer);
    } catch (final IOException e) {
      CollectorsPlugin.getInstance().getLogger().warning("Failed to save collectors.");
    }
  }

}
