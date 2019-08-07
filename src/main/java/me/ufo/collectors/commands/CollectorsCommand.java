package me.ufo.collectors.commands;

import java.util.Arrays;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.item.CollectorItem;
import me.ufo.collectors.util.Style;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class CollectorsCommand implements CommandExecutor {

  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!sender.hasPermission("collectors.admin")) return false;

    if (args.length == 0) {
      return Style.message(sender,
          "&eCollectors " + this.plugin.getDescription().getVersion() + " help:",
          "&e/collector give <target> <amount>: &fgive collector to player",
          "&e/colector set <type> <amount>: &fset amount of type in collector",
          "&e/collector remove: &fremove collector from chunk");
    }

    switch (args[0].toLowerCase()) {
      case "remove": {
        if (sender instanceof Player) {
          final Location location = ((Player) sender).getLocation();
          if (Collector.chunkHasCollector(location)) {
            Collector.get(location).drop();
            return Style.message(sender,
                ChatColor.RED.toString() + "Collector removed from x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ() + ".");
          }
          return Style.message(sender, ChatColor.RED.toString() + "There is no collector in this chunk.");
        }
        break;
      }

      case "give": {
        final Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
          return Style.message(sender, ChatColor.RED.toString() + "That player cannot be found.");
        }

        int amount;
        try {
          amount = NumberUtils.createInteger(args[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
          amount = 1;
        }

        target.getInventory().addItem(new ItemStack(CollectorItem.get(amount)));
        return Style.message(target, "&dYou have been given " + amount + " collector(s).");
      }

      case "set": {
        final CollectionType collectionType;
        try {
          collectionType = CollectionType.valueOf(args[1]);
        } catch (Exception e) {
          return Style.message(sender,
              ChatColor.RED.toString() + "That is not a valid collection type.",
              ChatColor.YELLOW.toString() + Arrays.toString(CollectionType.values()));
        }

        final int amount;
        try {
          amount = NumberUtils.createInteger(args[2]);
        } catch (NumberFormatException e) {
          return Style.message(sender, ChatColor.RED.toString() + "That is not a amount.");
        }

        final Location location = ((Player) sender).getLocation();
        if (Collector.chunkHasCollector(location)) {
          if (Collector.isCollector(location.subtract(new Vector(0, 0.5, 0)).getBlock().getLocation())) {
            final Collector collector = Collector.get(location.subtract(new Vector(0, 0.5, 0)).getBlock().getLocation());

            collector.getAmounts().entrySet().stream().filter(entry -> collectionType == entry.getKey()).forEach(entry -> entry.setValue(amount));
            return Style.message(sender, ChatColor.RED.toString() + "Collection type " + ChatColor.YELLOW.toString() + collectionType.toString() + ChatColor.RED.toString() + " has been set to " + ChatColor.YELLOW.toString() + amount + ChatColor.RED.toString() + ".");
          } else {
            return Style.message(sender, ChatColor.RED.toString() + "This is not this chunks collector.");
          }
        } else {
          return Style.message(sender, ChatColor.RED.toString() + "There is no collector in this chunk.");
        }
      }

      default:
        return Style.message(sender,
            "&eCollectors " + this.plugin.getDescription().getVersion() + " help:",
            "&e/collector give <target> <amount>: &fgive collector to player",
            "&e/colector set <type> <amount>: &fset amount of type in collector",
            "&e/collector remove: &fremove collector from chunk");
    }

    return false;
  }

}