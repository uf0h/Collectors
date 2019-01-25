package me.ufo.collectors.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.item.CollectorItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandAlias("collectors|collector")
@CommandPermission("venom.admin")
public class CollectorsCommand extends BaseCommand {

    @Dependency private CollectorsPlugin plugin;

    // TEMPORARY DEBUG COMMANDS

    @Subcommand("increment")
    public void onCollectorsIncrementCommand(CommandSender sender) {
        Collector.getCollectorCache().forEach((k, collector) -> {
            collector.increment(CollectionType.CREEPER, 100);
            collector.increment(CollectionType.CACTUS, 100);
        });
    }

    @Subcommand("decrement")
    public void onCollectorsDecrementCommand(CommandSender sender) {
        Collector.getCollectorCache().forEach((k, collector) -> {
            collector.decrement(CollectionType.CREEPER, 100);
            collector.decrement(CollectionType.CACTUS, 100);
        });
    }

    @Subcommand("check")
    public void onCollectorsCheckCommand(CommandSender sender) {
        Collector.getCollectorCache().forEach((k, collector) -> {
            sender.sendMessage(k + " " + collector.getAmounts().toString() + " " + collector.getLocation()) ;
            sender.sendMessage("inv null: " + (collector.getCollectorGUI() == null) + " viewers: " + collector.getViewers().size());
        });
    }

    @Subcommand("size")
    public void onCollectorsSizeCommand(CommandSender sender) {
        sender.sendMessage("size: " + Collector.getCollectorCache().size());
    }

    @Subcommand("give")
    @CommandCompletion("@players")
    public void onCollectorsGiveCommand(CommandSender sender, OnlinePlayer target) {
        target.getPlayer().getInventory().addItem(CollectorItem.get());
    }

    @Subcommand("set")
    public void onCollectorsSetCommand(Player player) {
        if (Collector.chunkHasCollector(player.getLocation())) {
            if (Collector.isCollector(player.getLocation().subtract(new Vector(0, 0.5, 0)).getBlock().getLocation())) {
                player.sendMessage("this is a collector");
            } else {
                player.sendMessage("this is not the collector");
            }
        } else {
            player.sendMessage("there is no collector in this chunk.");
        }
    }

    @Subcommand("remove")
    public void onCollectorsRemoveCommand(Player player) {
        if (Collector.chunkHasCollector(player.getLocation())) {
            final Location location = Collector.get(player.getLocation()).getLocation();
            Collector.get(player.getLocation()).remove(true);
            player.sendMessage(ChatColor.RED.toString() + "Collector removed from x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ() + ".");

        } else {
            player.sendMessage(ChatColor.RED.toString() + "There is no collector in this chunk.");
        }
    }

}
