package me.ufo.collectors.listeners;

import java.util.Set;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.Collector;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public enum FactionListener {

  FACTIONSUUID(new Listener() {

    @EventHandler
    public void onFactionDisbandEvent(final FactionDisbandEvent event) {
      final Set<FLocation> claims = Board.getInstance().getAllClaims(event.getFaction());

      Bukkit.getScheduler().runTaskAsynchronously(CollectorsPlugin.getInstance(), () -> {
        for (final FLocation claim : claims) {
          final Collector collector =
            Collector.get(claim.getWorld().getName(), (int) claim.getX(), (int) claim.getZ());

          if (collector != null) {
            Bukkit.getScheduler().runTask(CollectorsPlugin.getInstance(), collector::drop);
          }
        }
      });
    }

    @EventHandler
    public void onFactionUnclaimEvent(final LandUnclaimEvent event) {
      final Chunk chunk = event.getLocation().getChunk();
      Bukkit.getScheduler().runTaskAsynchronously(CollectorsPlugin.getInstance(), () -> {
          final Collector collector =
            Collector.get(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());

          if (collector != null) {
            Bukkit.getScheduler().runTask(CollectorsPlugin.getInstance(), collector::drop);
          }
      });
    }

    @EventHandler
    public void onFactionUnclaimEvent(final LandUnclaimAllEvent event) {
      final Set<FLocation> claims = Board.getInstance().getAllClaims(event.getFaction());

      Bukkit.getScheduler().runTaskAsynchronously(CollectorsPlugin.getInstance(), () -> {
        for (final FLocation claim : claims) {
          final Collector collector =
            Collector.get(claim.getWorld().getName(), (int) claim.getX(), (int) claim.getZ());

          if (collector != null) {
            Bukkit.getScheduler().runTask(CollectorsPlugin.getInstance(), collector::drop);
          }
        }
      });
    }

  });/*,

  MASSIVEFACTIONS(new Listener() {

    @EventHandler
    public void onFactionDisbandEvent(EventFactionsDisband event) {
      for (PS chunk : BoardColl.get().getChunks(event.getFaction())) {
        if (Collector.chunkHasCollector(chunk.asBukkitLocation())) {
          Collector.get(chunk.asBukkitLocation()).drop();
        }
      }
    }

  });*/

  private final Listener listener;

  FactionListener(final Listener listener) {
    this.listener = listener;
  }

  public Listener get() {
    return this.listener;
  }

}
