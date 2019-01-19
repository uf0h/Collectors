package me.ufo.collectors.listeners;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.Collector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerListener implements Listener {

    private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.BEACON) {
            if (Collector.chunkHasCollector(event.getBlockPlaced().getLocation())) {
                event.setCancelled(true);
                Collector collector = Collector.get(event.getBlockPlaced().getLocation());
                event.getPlayer().sendMessage(new String[] {
                        ChatColor.RED.toString() + "There is already a collector in this chunk.",
                        ChatColor.RED.toString() + "You can find it at x: " + collector.getLocation().getBlockX() + ", y: " + collector.getLocation().getBlockY() + ", z: " + collector.getLocation().getBlockZ() + "."
                });
                return;
            }

            Collector.add(event.getBlockPlaced().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN.toString() + "You have placed a collector in this chunk.");
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.BEACON) {
            if (Collector.chunkHasCollector(event.getBlock().getLocation())) {
                if (Collector.isCollector(event.getBlock().getLocation())) {
                    Collector.get(event.getBlock().getLocation()).remove();
                    event.getPlayer().sendMessage(ChatColor.RED.toString() + "You have removed a collector from this chunk.");
                }
            }
        }
    }

}
