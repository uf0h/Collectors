package me.ufo.collectors.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public final class Style {

    private Style() {
        throw new RuntimeException("Cannot instantiate utility class.");
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translateLines(List<String> in) {
        return in.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

}