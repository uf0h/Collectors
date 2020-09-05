package me.ufo.collectors.tasks;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.Collector;
import org.bukkit.scheduler.BukkitRunnable;

public class CollectorSaveTask extends BukkitRunnable {

  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  @Override
  public void run() {
    if (!Collector.getCollectorCache().isEmpty()) {
      final long startTime = System.currentTimeMillis();
      plugin.getLogger()
        .info("========== STARTING SAVE TASK (" + Collector.getCollectorCache().size() + ") ==========");
      Collector.saveall();
      plugin.getLogger().warning(
        "========== SAVE TASK COMPLETED IN (" + (System.currentTimeMillis() - startTime) + "ms) ==========");
    }
  }

}
