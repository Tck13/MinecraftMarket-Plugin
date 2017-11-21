package com.minecraftmarket.minecraftmarket.bukkit.configs;

import com.minecraftmarket.minecraftmarket.bukkit.utils.config.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MainConfig extends ConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final List<String> shopCommands;
    private final boolean useGUI;
    private final boolean useSigns;
    private final String dateFormat;
    private final String defaultHeadSkin;
    private final boolean statistics;
    private final String lang;
    private final boolean debug;

    public MainConfig(JavaPlugin plugin) {
        super(plugin, "bukkitConfig");

        apiKey = config.getString("APIKey", "");
        checkInterval = config.getInt("CheckInterval", 1);
        shopCommands = config.getStringList("ShopCommands");
        useGUI = config.getBoolean("UseGUI", true);
        useSigns = config.getBoolean("UseSigns", true);
        dateFormat = config.getString("DateFormat", "yyyy-MM-dd");
        defaultHeadSkin = config.getString("DefaultHeadSkin", "https://textures.minecraft.net/texture/5163dafac1d91a8c91db576caac784336791a6e18d8f7f62778fc47bf146b6");
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

    public List<String> getShopCommands() {
        return shopCommands;
    }

    public boolean isUseGUI() {
        return useGUI;
    }

    public boolean isUseSigns() {
        return useSigns;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getDefaultHeadSkin() {
        return defaultHeadSkin;
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