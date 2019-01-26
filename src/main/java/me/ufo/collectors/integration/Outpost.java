package me.ufo.collectors.integration;

import com.massivecraft.factions.entity.Faction;
import me.aceix8.outposts.AceOutposts;
import me.aceix8.outposts.api.OutpostAPI;
import me.ufo.collectors.CollectorsPlugin;
import org.bukkit.Bukkit;

public class Outpost {

    private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

    private static OutpostAPI outpostAPI = null;

    public static boolean isFactionControllingOutpost(Faction faction) {
        return outpostAPI.isFactionControllingAnOutpost(faction);
    }

    public void setup() {
        if (!setupOutposts()) {
            this.plugin.getLogger().info("ACEOUTPOSTS DEPENDENCY NOT FOUND.");
            this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
        } else {
            this.plugin.getLogger().info("ACEOUTPOSTS DEPENDENCY FOUND.");
        }
    }

    private boolean setupOutposts() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Ace-Outpost") == null) {
            return false;
        }

        outpostAPI = AceOutposts.getInstance().getApi();
        return true;
    }
}
