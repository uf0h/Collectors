package me.ufo.collectors.commands;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.item.CollectorItem;
import me.ufo.collectors.util.Style;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CollectorsCommand implements CommandExecutor {

    private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("collectors.admin")) return false;

        if (args.length == 0) {
            sender.sendMessage(new String[] {
                    Style.translate("&e/collector give <target> <amount> &7- &dGive collector to player.")
            });
        }

        switch (args.length) {
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
                }
                break;
        }

        return false;
    }

    // TEMPORARY DEBUG COMMANDS

    /*@Subcommand("increment")
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
    public void onCollectorsGiveCommand(CommandSender sender, OnlinePlayer target, String amount) {
        int amountI;
        try {
            amountI = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            amountI = 1;
        }

        target.getPlayer().getInventory().addItem(CollectorItem.get(amountI));
    }

    @Subcommand("set")
    public void onCollectorsSetCommand(Player player, int amount) {
        if (Collector.chunkHasCollector(player.getLocation())) {
            if (Collector.isCollector(player.getLocation().subtract(new Vector(0, 0.5, 0)).getBlock().getLocation())) {
                player.sendMessage("this is a collector");
                final Collector collector = Collector.get(player.getLocation().subtract(new Vector(0, 0.5, 0)).getBlock().getLocation());

                collector.increment(CollectionType.CREEPER, amount);
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
    }*/

}
