package com.minecraftmarket.minecraftmarket.sponge.config;

import com.minecraftmarket.minecraftmarket.sponge.utils.config.ConfigFile;

import java.io.File;

public class MainConfig extends ConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final boolean useSigns;
    private final String dateFormat;
    private final boolean statistics;
    private final String lang;
    private final boolean debug;

    public MainConfig(File baseDir) {
        super(baseDir, "spongeConfig");

        apiKey = config.getNode("APIKey").getString("");
        checkInterval = config.getNode("CheckInterval").getInt(1);
        useSigns = config.getNode("UseSigns").getBoolean(true);
        dateFormat = config.getNode("DateFormat").getString("yyyy-MM-dd");
        statistics = config.getNode("Statistics").getBoolean(true);
        lang = config.getNode("Lang").getString("en");
        debug = config.getNode("Debug").getBoolean(false);
    }

    public void setApiKey(String apiKey) {
        config.getNode("APIKey").setValue(apiKey);
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