package me.ufo.collectors.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import me.ufo.collectors.item.CollectorItem;
import me.ufo.collectors.util.NBTItem;
import me.ufo.collectors.util.Style;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

}
