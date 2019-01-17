package me.ufo.collectors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.ufo.collectors.commands.CollectorsCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CollectorsPlugin extends JavaPlugin {

    @Getter private static CollectorsPlugin instance;

    private Gson gson;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        instance = this;

        this.gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        this.registerCommands(new PaperCommandManager(this),
                new CollectorsCommand());

        this.registerListeners();

        this.getLogger().info("Successfully loaded. Took (" + (System.currentTimeMillis() - startTime) + "ms).");
    }

    @Override
    public void onDisable() {
        instance = null;
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

}
