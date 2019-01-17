package me.ufo.collectors.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import me.ufo.collectors.CollectorsPlugin;

@CommandAlias("collectors|collector")
public class CollectorsCommand extends BaseCommand {

    @Dependency private CollectorsPlugin plugin;

}
