package me.ufo.collectors.integration;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.ufo.collectors.CollectorsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Worldguard {

  public static WorldGuardPlugin worldguard = null;
  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  public static boolean playerCanPlaceHere(Player player, Block block) {
    return worldguard.canBuild(player, block);
  }

  public void setup() {
    if (!setupWorldguard()) {
      this.plugin.getLogger().info("WORLDGUARD DEPENDENCY NOT FOUND.");
      this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
    } else {
      worldguard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
      this.plugin.getLogger().info("WORLDGUARD DEPENDENCY FOUND.");
    }
  }

  private boolean setupWorldguard() {
    return Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null;
  }

}