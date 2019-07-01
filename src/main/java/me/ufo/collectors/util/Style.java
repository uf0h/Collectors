package me.ufo.collectors.util;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Style {

  private Style() {
    throw new RuntimeException("Cannot instantiate utility class.");
  }

  public static String translate(String in) {
    return ChatColor.translateAlternateColorCodes('&', in);
  }

  public static List<String> translate(List<String> in) {
    final int size = in.size();
    final List<String> out = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      out.add(ChatColor.translateAlternateColorCodes('&', in.get(i)));
    }
    return out;
  }

  public static String[] translate(String[] in) {
    final int size = in.length;
    final String[] out = new String[size];
    for (int i = 0; i < size; i++) {
      out[i] = ChatColor.translateAlternateColorCodes('&', in[i]);
    }
    return out;
  }

  public static boolean message(CommandSender sender, String in) {
    if (sender != null) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', in));
      return true;
    }
    return false;
  }

  public static boolean message(CommandSender sender, String... in) {
    if (sender != null) {
      sender.sendMessage(translate(in));
      return true;
    }
    return false;
  }

}