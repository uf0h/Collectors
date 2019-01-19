package me.ufo.collectors.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

public class Skulls {

    public void loadSkullsIntoCache() {
        Skull.skullCache = new HashMap<>();
        for (Skull skull : Skull.values()) {
            if (skull == Skull.SUGARCANE) {
                Skull.skullCache.put(skull, skull.getPlayerSkull("Sugar_Cane_"));
            } else {
                Skull.skullCache.put(skull, skull.getSkull());
            }
        }
    }

    public enum Skull {
        ARROW_LEFT("MHF_ArrowLeft"),
        ARROW_RIGHT("MHF_ArrowRight"),
        ARROW_UP("MHF_ArrowUp"),
        ARROW_DOWN("MHF_ArrowDown"),
        QUESTION("MHF_Question"),
        EXCLAMATION("MHF_Exclamation"),
        CAMERA("FHG_Cam"),

        ZOMBIE_PIGMAN("MHF_PigZombie"),
        PIG("MHF_Pig"),
        SHEEP("MHF_Sheep"),
        BLAZE("MHF_Blaze"),
        CHICKEN("MHF_Chicken"),
        COW("MHF_Cow"),
        SLIME("MHF_Slime"),
        SPIDER("MHF_Spider"),
        SQUID("MHF_Squid"),
        VILLAGER("MHF_Villager"),
        OCELOT("MHF_Ocelot"),
        HEROBRINE("MHF_Herobrine"),
        LAVA_SLIME("MHF_LavaSlime"),
        MOOSHROOM("MHF_MushroomCow"),
        GOLEM("MHF_Golem"),
        GHAST("MHF_Ghast"),
        ENDERMAN("MHF_Enderman"),
        CAVE_SPIDER("MHF_CaveSpider"),

        SUGARCANE(""),
        CACTUS("MHF_Cactus"),
        CAKE("MHF_Cake"),
        CHEST("MHF_Chest"),
        MELON("MHF_Melon"),
        LOG("MHF_OakLog"),
        PUMPKIN("MHF_Pumpkin"),
        TNT("MHF_TNT"),
        DYNAMITE("MHF_TNT2");

        public static HashMap<Skull, ItemStack> skullCache;

        private String id;
        Skull(String id) {
            this.id = id;
        }

        public ItemStack getPlayerSkull(String name) {
            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwner(name);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        public ItemStack getSkull() {
            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwner(id);
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        public ItemStack get() {
            return skullCache.get(this);
        }
    }

}
