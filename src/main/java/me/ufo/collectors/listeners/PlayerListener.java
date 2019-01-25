package me.ufo.collectors.listeners;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.massivecore.ps.PS;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.integration.Factions;
import me.ufo.collectors.integration.Worldguard;
import me.ufo.collectors.item.CollectorItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlockPlaced().getType() == Material.BEACON) {
            if (event.getItemInHand() != null) {
                if (this.isCollectorItem(event.getItemInHand())) {

                    if (!Factions.playerCanPlaceHere(event.getPlayer(), event.getBlock()) ||
                            !Worldguard.playerCanPlaceHere(event.getPlayer(), event.getBlock())) {
                        event.setCancelled(true);
                        return;
                    }

                    if (BoardColl.get().getFactionAt(PS.valueOf(event.getBlock())).isNone()) {
                        event.getPlayer().sendMessage(ChatColor.RED.toString() + "Collectors must be placed in your claimed faction land.");
                        event.setCancelled(true);
                        return;
                    }

                    if (Collector.chunkHasCollector(event.getBlockPlaced().getLocation())) {
                        event.setCancelled(true);

                        final Collector collector = Collector.get(event.getBlockPlaced().getLocation());

                        event.getPlayer().sendMessage(new String[]{
                                ChatColor.RED.toString() + "There is already a collector in this chunk.",
                                ChatColor.RED.toString() + "You can find it at x: " + collector.getLocation().getBlockX() + ", y: " + collector.getLocation().getBlockY() + ", z: " + collector.getLocation().getBlockZ() + "."
                        });
                        return;
                    }

                    Collector.add(event.getBlockPlaced().getLocation());
                    event.getPlayer().sendMessage(ChatColor.GREEN.toString() + "You have placed a collector in this chunk.");
                } else {
                    // ... Prevent placing regular beacons.
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock().getType() == Material.BEACON) {
            if (Collector.chunkHasCollector(event.getBlock().getLocation())) {
                if (Collector.isCollector(event.getBlock().getLocation())) {

                    if (!Factions.playerCanPlaceHere(event.getPlayer(), event.getBlock()) ||
                            !Worldguard.playerCanPlaceHere(event.getPlayer(), event.getBlock())) {
                        event.setCancelled(true);
                        return;
                    }

                    Collector.get(event.getBlock().getLocation()).remove(true);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), CollectorItem.get());

                    event.getPlayer().sendMessage(ChatColor.RED.toString() + "You have removed a collector from this chunk.");
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        event.blockList().stream().filter(block -> block.getType() == Material.BEACON).forEach(block -> {
            if (Collector.chunkHasCollector(block.getLocation())) {
                if (Collector.isCollector(block.getLocation())) {
                    Collector.get(block.getLocation()).remove(true);
                    block.getWorld().dropItem(block.getLocation(), CollectorItem.get());
                }
            }
        });
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.BEACON) {
                if (Collector.isCollector(event.getClickedBlock().getLocation())) {
                    final Collector collector = Collector.get(event.getClickedBlock().getLocation());

                    switch (event.getAction()) {
                        case LEFT_CLICK_BLOCK:
                            if (event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() == Material.AIR) {
                                double totalValue = collector.getAmounts().entrySet().stream()
                                        .filter(entry -> entry.getKey() != CollectionType.CREEPER && entry.getValue() > 0)
                                        .mapToDouble(entry -> (entry.getValue() * entry.getKey().getSellPrice())).sum();

                                event.getPlayer().sendMessage(ChatColor.RED.toString() + "This collector has a total value of " + ChatColor.GREEN.toString() + "$" + totalValue + ChatColor.RED.toString() + ".");
                            }
                            break;
                        case RIGHT_CLICK_BLOCK:
                            event.setCancelled(true);

                            collector.openInventory(event.getPlayer());
                            break;
                    }
                }
            }
        }
    }

    private boolean isCollectorItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return false;
        ItemStack collectorItem = CollectorItem.get();
        if (itemStack.getType() != collectorItem.getType()) return false;

        return itemStack.isSimilar(collectorItem);
    }

}
