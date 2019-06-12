package me.ufo.collectors.fastblockupdate;

import org.bukkit.Location;
import org.bukkit.Material;

public interface FastBlockUpdate {

  void run(Location location, Material material, boolean triggerUpdate);

}