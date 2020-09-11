package me.ufo.collectors;

import java.io.File;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.ufo.collectors.adapters.LocationTypeAdapter;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.commands.CollectorsCommand;
import me.ufo.collectors.integration.Econ;
import me.ufo.collectors.integration.Factions;
import me.ufo.collectors.integration.Worldguard;
import me.ufo.collectors.listeners.EntityListener;
import me.ufo.collectors.listeners.InventoryListener;
import me.ufo.collectors.listeners.PlayerListener;
import me.ufo.collectors.tasks.CollectorSaveTask;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CollectorsPlugin extends JavaPlugin {

  @Getter
  private static CollectorsPlugin instance;

  private Gson gson;

  private CollectorSaveTask collectorSaveTask;

  public CollectorsPlugin() {
    this.saveDefaultConfig();
    final File dataFile = new File(this.getDataFolder() + "/data.json");
    if (!dataFile.exists()) {
      try {
        dataFile.createNewFile();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onEnable() {
    final long startTime = System.currentTimeMillis();

    instance = this;

    this.registerDependencies();

    this.gson = new GsonBuilder()
      .registerTypeAdapter(Location.class, new LocationTypeAdapter())
      .disableHtmlEscaping().setPrettyPrinting().create();

    if (!Collector.initialize(this)) {
      this.getLogger().warning("Collectors have failed to be loaded into memory.");
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    if (!CollectionType.initialize(this)) {
      this.getLogger().warning("Collector prices have failed to register.");
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    this.getCommand("collectors").setExecutor(new CollectorsCommand());

    this.registerListeners(new PlayerListener(), new InventoryListener(), new EntityListener());

    this.getLogger().info("Successfully loaded. Took (" + (System.currentTimeMillis() - startTime) + "ms).");

    this.collectorSaveTask = new CollectorSaveTask();
    this.collectorSaveTask.runTaskTimerAsynchronously(this, 100L, 1200L);
  }

  @Override
  public void onDisable() {
    Collector.getCollectorCache().forEach((s, collector) -> collector.disable());
    Collector.saveall();
  }

  private void registerDependencies() {
    new Econ().setup();
    new Factions().setup();
    new Worldguard().setup();
  }

  public void registerListeners(final Listener... listeners) {
    for (final Listener listener : listeners) {
      this.getServer().getPluginManager().registerEvents(listener, this);
    }
  }

}
