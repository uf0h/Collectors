package me.ufo.collectors.integration;

import me.ufo.collectors.CollectorsPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {

    private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

    public static Economy econ = null;

    public static boolean withdrawAmountFromPlayer(Player player, double cost) {
        EconomyResponse er = econ.withdrawPlayer(player, cost);
        return er.transactionSuccess();
    }

    public static boolean depositAmountToPlayer(Player player, double amount) {
        EconomyResponse er = econ.depositPlayer(player, amount);
        return er.transactionSuccess();
    }

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
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}