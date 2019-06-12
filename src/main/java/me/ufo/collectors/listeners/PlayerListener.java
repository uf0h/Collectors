package me.ufo.collectors.listeners;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.integration.Econ;
import me.ufo.collectors.integration.Factions;
import me.ufo.collectors.integration.Outpost;
import me.ufo.collectors.integration.Worldguard;
import me.ufo.collectors.item.CollectorItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
    //if (event.isCancelled()) return;

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

      return;
    }

    if (event.getBlock().getType() == Material.SUGAR_CANE_BLOCK) {
      if (!Factions.playerCanPlaceHere(event.getPlayer(), event.getBlock()) ||
          !Worldguard.playerCanPlaceHere(event.getPlayer(), event.getBlock())) {
        event.setCancelled(true);
        return;
      }

      if (Collector.chunkHasCollector(event.getBlock().getLocation())) {
        event.setCancelled(true);

        final Collector collector = Collector.get(event.getBlock().getLocation());
        int amountOfCane = 0;

        Block next = event.getBlock();
        while (next != null && next.getType() == Material.SUGAR_CANE_BLOCK) {
          CollectorsPlugin.getInstance().getFastBlockUpdate().run(next.getLocation(), Material.AIR, false);
          amountOfCane += 1;
          next = next.getRelative(BlockFace.UP);
        }

        collector.increment(CollectionType.SUGAR_CANE, amountOfCane);
      }
    }
  }

  @EventHandler
  public void onEntityExplodeEvent(EntityExplodeEvent event) {
    for (int i = 0; i < event.blockList().size(); i++) {
      if (event.blockList().get(i).getType() != Material.BEACON) continue;

      final Location location = event.blockList().get(i).getLocation();
      if (Collector.chunkHasCollector(location)) {
        if (Collector.isCollector(location)) {
          Collector.get(location).remove(true);
          location.getWorld().dropItem(location, CollectorItem.get());
        }
      }
    }
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

              if (event.getPlayer().isSneaking() && event.getPlayer().hasPermission("venom.anaconda")) {
                double totalValue = collector.getAmounts().entrySet().stream()
                    .filter(entry -> entry.getKey() != CollectionType.CREEPER && entry.getValue() > 0)
                    .mapToDouble(entry -> (entry.getValue() * entry.getKey().getSellPrice())).sum();

                if (totalValue == 0) {
                  event.getPlayer().sendMessage(ChatColor.RED.toString() + "This collector has a total value of " + ChatColor.GREEN.toString() + "$" + totalValue + ChatColor.RED.toString() + ".");
                  return;
                }

                if (Outpost.isFactionControllingOutpost(MPlayer.get(event.getPlayer()).getFaction())) {
                  totalValue *= 2;
                  event.getPlayer().sendMessage(ChatColor.RED.toString() + "You will receive " + ChatColor.GREEN.toString() + "x2" + ChatColor.RED.toString() + " value as you are controlling outpost.");
                }

                collector.getAmounts().entrySet().stream()
                    .filter(entry -> entry.getKey() != CollectionType.CREEPER && entry.getValue() > 0)
                    .forEach(entry -> entry.setValue(0));

                if (Econ.depositAmountToPlayer(event.getPlayer(), totalValue)) {
                  event.getPlayer().sendMessage(ChatColor.GREEN.toString() + "+$" + totalValue + ChatColor.RED.toString() + " from selling everything in this collector.");
                }

                return;
              }

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
