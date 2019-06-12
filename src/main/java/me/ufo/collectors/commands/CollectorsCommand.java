package me.ufo.collectors.commands;

import java.util.Arrays;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.item.CollectorItem;
import me.ufo.collectors.util.Style;
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
          "&e/collector give <target> <amount> &7- &dGive collector to player.",
          "&e/collector set <collectionType> <amount> &7- &dSet amounts of collection type in a collector you are stood above.",
          "&e/collector remove &7- &dRemoves a collector from the chunk you are standing in.");
    }

    switch (args.length) {
      case 1:
        if (args[0].equalsIgnoreCase("remove") && sender instanceof Player) {
          final Player player = (Player) sender;
          if (Collector.chunkHasCollector(player.getLocation())) {
            final Location location = Collector.get(player.getLocation()).getLocation();
            Collector.get(player.getLocation()).remove(true);
            player.sendMessage(ChatColor.RED.toString() + "Collector removed from x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ() + ".");
          } else {
            player.sendMessage(ChatColor.RED.toString() + "There is no collector in this chunk.");
          }
          return false;
        }
        break;
      case 2:
      case 3:
        if (args[0].equalsIgnoreCase("give")) {
          final Player target = plugin.getServer().getPlayer(args[1]);
          if (target == null) {
            sender.sendMessage(ChatColor.RED.toString() + "That player cannot be found.");
            return false;
          }

          int amount;
          try {
            amount = Integer.parseInt(args[2]);
          } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            amount = 1;
          }

          target.getInventory().addItem(new ItemStack(CollectorItem.get(amount)));

          target.sendMessage(Style.translate("&dYou have been given " + amount + " collector(s)."));
          return false;
        }

        if (args[0].equalsIgnoreCase("set") && sender instanceof Player) {
          final Player player = (Player) sender;

          final CollectionType collectionType;
          try {
            collectionType = CollectionType.valueOf(args[1]);
          } catch (Exception e) {
            player.sendMessage(ChatColor.RED.toString() + "That is not a valid collection type.");
            player.sendMessage(ChatColor.YELLOW.toString() + Arrays.toString(CollectionType.values()));
            return false;
          }

          final int amount;
          try {
            amount = Integer.parseInt(args[2]);
          } catch (Exception e) {
            player.sendMessage(ChatColor.RED.toString() + "That is not a amount.");
            return false;
          }

          if (Collector.chunkHasCollector(player.getLocation())) {
            if (Collector.isCollector(player.getLocation().subtract(new Vector(0, 0.5, 0)).getBlock().getLocation())) {
              final Collector collector = Collector.get(player.getLocation().subtract(new Vector(0, 0.5, 0)).getBlock().getLocation());
              collector.getAmounts().entrySet().stream().filter(entry -> collectionType == entry.getKey()).forEach(entry -> entry.setValue(amount));
              player.sendMessage(ChatColor.RED.toString() + "Collection type " + ChatColor.YELLOW.toString() + collectionType.toString() + ChatColor.RED.toString() + " has been set to " + ChatColor.YELLOW.toString() + amount + ChatColor.RED.toString() + ".");
            } else {
              player.sendMessage(ChatColor.RED.toString() + "This is not this chunks collector.");
            }
          } else {
            player.sendMessage(ChatColor.RED.toString() + "There is no collector in this chunk.");
          }
          return false;
        }

        break;
    }

    return false;
  }

}
