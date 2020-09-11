package me.ufo.collectors.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

  private final ItemMeta meta;
  private final ItemStack item;
  private List<String> lore;

  public ItemBuilder(final ItemStack item) {
    this.item = item;
    this.meta = item.getItemMeta();
  }

  public ItemBuilder(final Material mat, final int amount, final short damage) {
    this(new ItemStack(mat, amount, damage));
  }

  public ItemBuilder(final Material mat, final int amount) {
    this(mat, amount, (short) 0);
  }

  public ItemBuilder(final Material mat) {
    this(mat, 1);
  }

  public String getName() {
    return meta != null ? meta.getDisplayName() : null;
  }

  public ItemBuilder name(final String name) {
    if (meta != null) {
      meta.setDisplayName(name);
    }
    return this;
  }

  public int getAmount() {
    return this.item.getAmount();
  }

  public ItemBuilder setAmount(final int amount) {
    this.item.setAmount(amount);
    return this;
  }

//    public int getDamage() {
//        return this.item.getData().getData();
//    }

  public ItemStack build() {
    if (this.meta != null) {
      if (this.lore != null) {
        this.meta.setLore(this.lore);
      }
      this.item.setItemMeta(this.meta);
    }
    return this.item;
  }

  public ItemStack buildCloned() {
    return this.build().clone();
  }

  public ItemBuilder lore(final String... lore) {
    return addLore(new ArrayList<>(Arrays.asList(lore)));
  }

  public ItemBuilder lore(final List<String> lore) {
    this.lore = new ArrayList<>(lore);
    return this;
  }

  public ItemBuilder addLore(final String... lore) {
    return addLore(Arrays.asList(lore));
  }

  public ItemBuilder addLore(final List<String> lore) {
    if (this.lore == null) {
      if (!meta.hasLore()) {
        this.lore = new ArrayList<>();
      } else {
        this.lore = new ArrayList<>(meta.getLore());
      }
    }
    this.lore.addAll(lore);
    return this;
  }

  public ItemBuilder data(final short data) {
    item.setDurability(data);
    return this;
  }

  public ItemBuilder addEnchantment(final Enchantment ench, final int level) {
    if (ench != null) {
      meta.addEnchant(ench, level, false);
      return this;
    } else {
      return null;
    }
  }

}
