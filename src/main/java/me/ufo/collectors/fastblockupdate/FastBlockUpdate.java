package me.ufo.collectors.fastblockupdate;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author LilProteinShake
 * @github https://github.com/ProteinDev/
 * @mcmarket https://www.mc-market.org/members/130554/
 */
public interface FastBlockUpdate {

    void run(Location location, Material material, boolean triggerUpdate);

}