package me.ufo.collectors.integration;

import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.massivecore.ps.PS;
import me.ufo.collectors.CollectorsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Factions {

  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  public static boolean playerCanPlaceHere(Player player, Block block) {
    return EnginePermBuild.canPlayerBuildAt(player, PS.valueOf(block), true);
  }

  public void setup() {
    if (!setupFactions()) {
      this.plugin.getLogger().info("FACTIONS DEPENDENCY NOT FOUND.");
      this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
    } else {
      this.plugin.getLogger().info("FACTIONS DEPENDENCY FOUND.");
    }
  }

  private boolean setupFactions() {
    return Bukkit.getServer().getPluginManager().getPlugin("Factions") != null;
  }

}