package com.minecraftmarket.minecraftmarket.common.utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;

public class TPS {
    private static Object minecraftServer;
    private static Field recentTps;

    public static double getTPS(int time) {
        try {
            if (minecraftServer == null) {
                String version = Bukkit.getServer().getClass().getPackage().getName();
                version = version.substring(version.lastIndexOf('.') + 1);
                minecraftServer = Class.forName("net.minecraft.server." + version + ".MinecraftServer").getMethod("getServer").invoke(null);
            }
            if (recentTps == null) {
                recentTps = minecraftServer.getClass().getField("recentTps");
                recentTps.setAccessible(true);
            }
            double tps = ((double[]) recentTps.get(minecraftServer))[time];
            return Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
        } catch (Exception e) {
            return -1;
        }
    }

}