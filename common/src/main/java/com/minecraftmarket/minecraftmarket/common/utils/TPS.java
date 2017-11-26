package com.minecraftmarket.minecraftmarket.common.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class TPS {
    private static Object serverInstance;
    private static Field tpsField;

    public static double getTPS(int time) {
        try {
            if (serverInstance == null) {
                String version = Bukkit.getServer().getClass().getPackage().getName();
                version = version.substring(version.lastIndexOf('.') + 1);
                serverInstance = Class.forName("net.minecraft.server." + version + ".MinecraftServer").getMethod("getServer").invoke(null);
            }
            if (tpsField == null) {
                tpsField = serverInstance.getClass().getField("recentTps");
                tpsField.setAccessible(true);
            }
            double tps = ((double[]) tpsField.get(serverInstance))[time];
            return Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
        } catch (Exception e) {
            return -1;
        }
    }

}