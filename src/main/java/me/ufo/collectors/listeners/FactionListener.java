package me.ufo.collectors.listeners;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.Collector;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionListener implements Listener {

  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  @EventHandler
  public void onFactionDisbandEvent(EventFactionsDisband event) {
    BoardColl.get().getChunks(event.getFaction()).stream().filter(ps ->
        Collector.chunkHasCollector(ps.asBukkitChunk().getWorld().getName(), ps.asBukkitChunk().getX(), ps.asBukkitChunk().getZ())).findAny().ifPresent(ps -> {
      event.setCancelled(true);
      event.getMPlayer().msg(ChatColor.RED.toString() + "You must remove all collectors in your claims before disbanding.");

            /*StringBuilder stringBuilder = new StringBuilder();
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {

                BoardColl.get().getChunks(event.getFaction()).stream().filter(entry ->
                        Collector.chunkHasCollector(entry.asBukkitChunk().getWorld().getName(), entry.asBukkitChunk().getX(), entry.asBukkitChunk().getZ())).forEach(chunk -> {
                            final Collector collector = Collector.get(chunk.getWorld(), chunk.getChunkX(), chunk.getChunkZ());
                            if (collector != null) {
                                stringBuilder
                                        .append(ChatColor.RED.toString())
                                        .append("[")
                                        .append(ChatColor.YELLOW.toString())
                                        .append(collector.getLocation().getWorld().getName())
                                        .append(ChatColor.RED.toString())
                                        .append(" x: ")
                                        .append(ChatColor.YELLOW.toString())
                                        .append(collector.getLocation().getBlockX())
                                        .append(ChatColor.RED.toString())
                                        .append(", y: ")
                                        .append(ChatColor.YELLOW.toString())
                                        .append(collector.getLocation().getBlockY())
                                        .append(ChatColor.RED.toString())
                                        .append(", z: ")
                                        .append(ChatColor.YELLOW.toString())
                                        .append(collector.getLocation().getBlockZ())
                                        .append(ChatColor.RED.toString())
                                        .append("] ");
                            }
                });

                event.getMPlayer().msg(stringBuilder.toString());
            });*/
    });
  }

  @EventHandler
  public void onFactionsChunksChangeEvent(EventFactionsChunksChange event) {
    event.getChunkType().entrySet().stream().filter(entry -> entry.getValue() == EventFactionsChunkChangeType.SELL &&
        Collector.chunkHasCollector(entry.getKey().asBukkitChunk().getWorld().getName(), entry.getKey().asBukkitChunk().getX(), entry.getKey().asBukkitChunk().getZ())).findAny().ifPresent(entry -> {
      event.setCancelled(true);
      event.getMPlayer().msg(ChatColor.RED.toString() + "You must remove all collectors in these claims before unclaiming.");
    });
  }

  @EventHandler
  public void onFactionsMembershipChangeEvent(EventFactionsMembershipChange event) {
    if (event.getReason() == EventFactionsMembershipChange.MembershipChangeReason.LEAVE && event.getMPlayer().getFaction().getMPlayers().size() == 1) {
      BoardColl.get().getChunks(event.getMPlayer().getFaction()).stream().filter(ps ->
          Collector.chunkHasCollector(ps.asBukkitChunk().getWorld().getName(), ps.asBukkitChunk().getX(), ps.asBukkitChunk().getZ())).findAny().ifPresent(ps -> {
        event.setCancelled(true);
        event.getMPlayer().msg(ChatColor.RED.toString() + "You must remove all collectors in your claims before disbanding.");
      });
    }
  }

}
