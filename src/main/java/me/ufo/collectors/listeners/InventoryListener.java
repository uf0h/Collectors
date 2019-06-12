package me.ufo.collectors.listeners;

import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent event) {
    if (event.getClickedInventory() != null) {
      if (event.getClickedInventory().getHolder() instanceof GUI) {
        event.setCancelled(true);
        ((GUI) event.getClickedInventory().getHolder()).getConsumer().accept(event);
      }
    }
  }

  @EventHandler
  public void onInventoryCloseEvent(InventoryCloseEvent event) {
    if (event.getInventory().getHolder() instanceof GUI) {
      Collector.removeViewer(event.getPlayer().getUniqueId());
    }
  }

}
