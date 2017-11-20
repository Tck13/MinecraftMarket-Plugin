package com.minecraftmarket.minecraftmarket.nukkit.configs;

import cn.nukkit.plugin.PluginBase;
import com.minecraftmarket.minecraftmarket.nukkit.utils.config.ConfigFile;

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

        apiKey = config.getString("APIKey");
        checkInterval = config.getInt("CheckInterval");
        useSigns = config.getBoolean("UseSigns");
        dateFormat = config.getString("DateFormat");
        statistics = config.getBoolean("Statistics");
        lang = config.getString("Lang");
        debug = config.getBoolean("Debug");
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