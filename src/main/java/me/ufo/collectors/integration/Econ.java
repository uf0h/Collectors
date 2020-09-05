package me.ufo.collectors.integration;

import me.ufo.collectors.CollectorsPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {

  public static Economy econ = null;
  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  public void setup() {
    if (!setupEconomy()) {
      this.plugin.getLogger().info("VAULT DEPENDENCY NOT FOUND.");
      this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
    } else {
      this.plugin.getLogger().info("VAULT DEPENDENCY FOUND.");
    }
  }

  private boolean setupEconomy() {
    if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    final RegisteredServiceProvider<Economy> rsp =
      Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return econ != null;
  }

  public static boolean withdrawAmountFromPlayer(final Player player, final double cost) {
    final EconomyResponse er = econ.withdrawPlayer(player, cost);
    return er.transactionSuccess();
  }

  public static boolean depositAmountToPlayer(final Player player, final double amount) {
    final EconomyResponse er = econ.depositPlayer(player, amount);
    return er.transactionSuccess();
  }

}
