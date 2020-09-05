package me.ufo.collectors.integration;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.listeners.FactionListener;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Factions {

  private static IFactions IFactions;
  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  public void setup() {
    if (!setupFactions()) {
      this.plugin.getLogger().info("FACTIONS DEPENDENCY NOT FOUND.");
      this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
    } else {
      IFactions = this.setupVersion();
      this.plugin.getLogger().info("FACTIONS DEPENDENCY FOUND.");
    }
  }

  private boolean setupFactions() {
    return Bukkit.getServer().getPluginManager().getPlugin("Factions") != null;
  }

  private IFactions setupVersion() {
    /*if (Bukkit.getServer().getPluginManager().getPlugin("MassiveCore") != null) {
      this.plugin.registerListeners(FactionListener.MASSIVEFACTIONS.get());

      return new IFactions() {
        @Override
        public boolean playerCanPlaceHere(Player player, Block block) {
          return EnginePermBuild.canPlayerBuildAt(player, PS.valueOf(block), true);
        }

        @Override
        public boolean isWilderness(Block block) {
          return BoardColl.get().getFactionAt(PS.valueOf(block)).isNone();
        }

        @Override
        public String getFactionTag(Player player) {
          return MPlayer.get(player).getFactionName();
        }
      };
    }*/

    this.plugin.registerListeners(FactionListener.FACTIONSUUID.get());
    return new IFactions() {
      @Override
      public boolean playerCanPlaceHere(final Player player, final Block block) {
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), "build", true);
      }

      @Override
      public boolean isWilderness(final Block block) {
        return Board.getInstance().getFactionAt(new FLocation(block)).isWilderness();
      }

      @Override
      public String getFactionTag(final Player player) {
        return FPlayers.getInstance().getByPlayer(player).getFaction().getTag();
      }
    };
  }

  public static boolean playerCanPlaceHere(final Player player, final Block block) {
    return IFactions.playerCanPlaceHere(player, block);
  }

  public static boolean isWilderness(final Block block) {
    return IFactions.isWilderness(block);
  }

  public static String getFactionTag(final Player player) {
    return IFactions.getFactionTag(player);
  }

  private interface IFactions {

    boolean playerCanPlaceHere(final Player player, final Block block);

    boolean isWilderness(final Block block);

    String getFactionTag(final Player player);

  }

}
