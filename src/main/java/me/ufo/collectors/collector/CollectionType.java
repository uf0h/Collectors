package me.ufo.collectors.collector;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@Getter
public enum CollectionType {

    CACTUS(Material.CACTUS),

    CREEPER(EntityType.CREEPER);

    private Material material;
    private EntityType entityType;

    CollectionType(Material material) {
        this.material = material;
    }

    CollectionType(EntityType entityType) {
        this.entityType = entityType;
    }

}
