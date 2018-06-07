package com.minecraftmarket.minecraftmarket.bungee.configs;

import com.minecraftmarket.minecraftmarket.bungee.utils.config.ConfigFile;

import net.md_5.bungee.api.plugin.Plugin;

public class MainConfig extends ConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final boolean statistics;
    private final String lang;
    private final boolean debug;

    public MainConfig(Plugin plugin) {
        super(plugin, "bungeeConfig");

        apiKey = config.getString("APIKey", "");
        checkInterval = config.getInt("CheckInterval", 1);
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