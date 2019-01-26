package me.ufo.collectors.listeners;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.Collector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class ShutdownListener implements Listener {

    private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

    @EventHandler
    public void onPluginDisableEvent(PluginDisableEvent event) {
        if (event.getPlugin().getName().equalsIgnoreCase("Factions")
                || event.getPlugin().getName().equalsIgnoreCase("Vault")
                    || event.getPlugin().getName().equalsIgnoreCase("MassiveCore")
                        || event.getPlugin().getName().equalsIgnoreCase("Essentials")) {

            // ... Due to certain plugins (mcMMO) holding up the disabling process ...
            // ... we must disable the collector save thread manually.
            this.plugin.disableCollectorSaveThread();

            Collector.getCollectorCache().forEach((s, collector) -> {
                collector.getViewers().forEach(uuid -> this.plugin.getServer().getPlayer(uuid).closeInventory());
            });
        }
    }

}
