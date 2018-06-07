package com.minecraftmarket.minecraftmarket.nukkit.configs;

import com.minecraftmarket.minecraftmarket.nukkit.utils.config.ConfigFile;

import cn.nukkit.plugin.PluginBase;

public class MainConfig extends ConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final boolean useSigns;
    private final String dateFormat;
    private final boolean statistics;
    private final String lang;
    private final boolean debug;

    public MainConfig(PluginBase plugin) {
        super(plugin, "nukkitConfig");

        apiKey = config.getString("APIKey", "");
        checkInterval = config.getInt("CheckInterval", 1);
        useSigns = config.getBoolean("UseSigns", true);
        dateFormat = config.getString("DateFormat", "yyyy-MM-dd");
        statistics = config.getBoolean("Statistics", true);
        lang = config.getString("Lang", "en");
        debug = config.getBoolean("Debug", false);
    }

    public void setApiKey(String apiKey) {
        config.set("APIKey", apiKey);
        saveConfig();
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public boolean isUseSigns() {
        return useSigns;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public boolean isStatistics() {
        return statistics;
    }

    public String getLang() {
        return lang;
    }

    public boolean isDebug() {
        return debug;
    }
}