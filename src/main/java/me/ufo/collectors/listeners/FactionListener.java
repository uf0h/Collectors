package me.ufo.collectors.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.event.FactionDisbandEvent;
import me.ufo.collectors.collector.Collector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public enum FactionListener {

  FACTIONSUUID(new Listener() {

    @EventHandler
    public void onFactionDisbandEvent(final FactionDisbandEvent event) {
      for (final FLocation claim : Board.getInstance().getAllClaims(event.getFaction())) {
        if (Collector.chunkHasCollector(claim.getWorld().getName(), (int) claim.getX(), (int) claim.getZ())) {
          Collector.get(claim.getWorld().getName(), (int) claim.getX(), (int) claim.getZ()).drop();
        }
      }
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
