package me.ufo.collectors.tasks;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.Collector;

public class CollectorSaveThread extends Thread {

    private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

    @Override
    public void run() {
        while(true) {
            if (!Collector.getCollectorCache().isEmpty()) {
                long startTime = System.currentTimeMillis();
                plugin.getLogger().info("========== STARTING SAVE TASK (" + Collector.getCollectorCache().size() + ") ==========");
                Collector.saveall();
                plugin.getLogger().warning("========== SAVE TASK COMPLETED IN (" + (System.currentTimeMillis() - startTime) + "ms) ==========");
            }

            try {
                Thread.sleep(1 * 30000L); // every 30 seconds for testing purposes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
