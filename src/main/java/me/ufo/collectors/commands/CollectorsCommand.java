package me.ufo.collectors.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import org.bukkit.command.CommandSender;

@CommandAlias("collectors|collector")
@CommandPermission("venom.admin")
public class CollectorsCommand extends BaseCommand {

    @Dependency private CollectorsPlugin plugin;

    // TEMPORARY DEBUG COMMANDS

    @Subcommand("increment")
    public void onCollectorsIncrementCommand(CommandSender sender) {
        Collector.getCollectorCache().forEach((k, collector) -> {
            collector.increment(CollectionType.CREEPER);
            collector.increment(CollectionType.CACTUS);
        });
    }

    @Subcommand("decrement")
    public void onCollectorsDecrementCommand(CommandSender sender) {
        Collector.getCollectorCache().forEach((k, collector) -> {
            collector.decrement(CollectionType.CREEPER, 1);
            collector.decrement(CollectionType.CACTUS, 1);
        });
    }

    @Subcommand("check")
    public void onCollectorsCheckCommand(CommandSender sender) {
        Collector.getCollectorCache().forEach((k, collector) -> {
            sender.sendMessage(k + " " + collector.getAmounts().toString() + " " + collector.getLocation());
        });
    }

    @Subcommand("size")
    public void onCollectorsSizeCommand(CommandSender sender) {
        sender.sendMessage("size: " + Collector.getCollectorCache().size());
    }

}
