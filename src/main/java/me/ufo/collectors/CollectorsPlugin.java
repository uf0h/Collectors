package me.ufo.collectors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.ufo.collectors.adapters.LocationTypeAdapter;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.commands.CollectorsCommand;
import me.ufo.collectors.listeners.EntityListener;
import me.ufo.collectors.listeners.InventoryListener;
import me.ufo.collectors.listeners.PlayerListener;
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

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                    .disableHtmlEscaping().setPrettyPrinting().create();

        if (!Collector.initialize()) {
            this.getLogger().warning("Collectors have failed to be loaded into memory.");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.initializeUtilities();

        this.registerCommands(new PaperCommandManager(this),
                new CollectorsCommand());

        this.registerListeners(new PlayerListener(), new InventoryListener(), new EntityListener());

        this.getLogger().info("Successfully loaded. Took (" + (System.currentTimeMillis() - startTime) + "ms).");

        this.getServer().getScheduler().runTaskLater(this, () -> new CollectorSaveThread().start(), 100L);
    }

    @Override
    public void onDisable() {
        Collector.saveall();
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

}
