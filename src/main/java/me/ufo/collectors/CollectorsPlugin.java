package me.ufo.collectors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
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
import me.ufo.collectors.listeners.*;
import me.ufo.collectors.tasks.CollectorSaveThread;
import me.ufo.collectors.util.Skulls;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public class CollectorsPlugin extends JavaPlugin {

    @Getter private static CollectorsPlugin instance;

    private Gson gson;

    private CollectorSaveThread collectorSaveThread;

    public CollectorsPlugin() {
        this.saveDefaultConfig();
        File dataFile = new File(this.getDataFolder() + "/data.json");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        instance = this;

        this.registerDependencies();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                    .disableHtmlEscaping().setPrettyPrinting().create();

        if (!Collector.initialize(this)) {
            this.getLogger().warning("Collectors have failed to be loaded into memory.");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        if (!CollectionType.initialize(this)) {
            this.getLogger().warning("Collector prices have failed to register.");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.initializeUtilities();

        this.registerCommands(new PaperCommandManager(this),
                new CollectorsCommand());

        this.registerListeners(new PlayerListener(), new InventoryListener(), new EntityListener(), new ShutdownListener(), new FactionListener());

        this.getLogger().info("Successfully loaded. Took (" + (System.currentTimeMillis() - startTime) + "ms).");

        this.collectorSaveThread = new CollectorSaveThread();

        this.getServer().getScheduler().runTaskLater(this, () -> this.collectorSaveThread.start(), 100L);
    }

    @Override
    public void onDisable() {
        Collector.getCollectorCache().forEach((s, collector) -> {
            collector.getViewers().forEach(uuid -> this.getServer().getPlayer(uuid).closeInventory());
        });

        this.disableCollectorSaveThread();

        Collector.saveall();
    }

    private void registerDependencies() {
        new Econ().setup();
        new Factions().setup();
        new Worldguard().setup();
    }

    private void registerCommands(PaperCommandManager paperCommandManager, BaseCommand... baseCommands) {
        for (BaseCommand baseCommand : baseCommands) {
            paperCommandManager.registerCommand(baseCommand);
        }
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void initializeUtilities() {
        new Skulls().loadSkullsIntoCache();
    }

    public void disableCollectorSaveThread() {
        if (this.collectorSaveThread != null) {
            this.collectorSaveThread.interrupt();
            this.collectorSaveThread.stop();
            this.collectorSaveThread = null;
        }
    }

}
