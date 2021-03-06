package me.ufo.collectors.listeners;

import me.ufo.collectors.CollectorsPlugin;
import me.ufo.collectors.collector.CollectionType;
import me.ufo.collectors.collector.Collector;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityListener implements Listener {

  private final CollectorsPlugin plugin = CollectorsPlugin.getInstance();

  /*@EventHandler
  public void onBlockGrowEvent(BlockGrowEvent event) {
    try { // TEMP
      if (event.getBlock() != null) {
        if (!blockCannotGrow(event.getBlock().getWorld(), event.getBlock())) {
          if (event.getNewState().getType() == Material.CACTUS) {
            event.setCancelled(true);
            final Collector collector = Collector.get(event.getBlock().getLocation());
            if (collector != null) {
              collector.increment(CollectionType.CACTUS);
            }
          }
        }
      }
    } catch (Exception ignored) {
    }
  }*/

  @EventHandler
  public void onSpawnerPreSpawnEvent(final SpawnerPreSpawnEvent event) {
    try { // TEMP
      CollectionType collectionType = CollectionType.parse(event.getSpawnedType());
      if (collectionType != null) {
        event.setCancelled(true);
        final Collector collector = Collector.get(event.getLocation());
        if (collector != null) {
          if (collectionType == CollectionType.CAVE_SPIDER) {
            collectionType = CollectionType.SPIDER;
          }

          if (collectionType == CollectionType.CREEPER) {
            collector.increment(collectionType);
          }
        }
      }
    } catch (final Exception ignored) {
    }
  }

  /*private boolean blockCannotGrow(World world, Block block) {
    final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
    final net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) world).getHandle();

    for (EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
      if (nmsWorld.getType(blockPosition.shift(enumDirection)).getBlock().getMaterial().isBuildable()) {
        return false;
      }
    }

    final net.minecraft.server.v1_8_R3.Block NMSBlock = nmsWorld.getType(blockPosition.down()).getBlock();
    return NMSBlock == Blocks.CACTUS || NMSBlock == Blocks.SAND;
  }*/

}
