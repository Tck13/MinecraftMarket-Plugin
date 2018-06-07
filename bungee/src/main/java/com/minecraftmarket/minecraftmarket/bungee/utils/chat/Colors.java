package com.minecraftmarket.minecraftmarket.bungee.utils.chat;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class Colors {
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public static List<String> colorList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String msg : list) {
            newList.add(color(msg));
        }
        return newList;
    }

    public static List<String> removeColorList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String msg : list) {
            newList.add(removeColor(msg));
        }
        return newList;
    }
}