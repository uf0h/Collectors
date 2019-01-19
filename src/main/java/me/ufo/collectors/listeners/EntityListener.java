package me.ufo.collectors.listeners;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class EntityListener implements Listener {

    private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

    @EventHandler
    public void onBlockGrowEvent(BlockGrowEvent event) {
        if (event.getNewState().getType() == Material.CACTUS) {
            event.setCancelled(true);
            final Collector collector = Collector.get(event.getBlock().getLocation());
            if (collector != null) {
                collector.increment(CollectionType.CACTUS, 1);
            }
        }
    }

    @EventHandler
    public void onSpawnerPreSpawnEvent(SpawnerPreSpawnEvent event) {
        final CollectionType collectionType = CollectionType.parse(event.getSpawnedType());
        if (collectionType != null) {
            event.setCancelled(true);
            final Collector collector = Collector.get(event.getLocation());
            if (collector != null) {
                collector.increment(collectionType);
            }
        }
    }

}