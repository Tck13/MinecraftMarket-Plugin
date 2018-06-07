package com.minecraftmarket.minecraftmarket.bukkit.configs;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.bukkit.utils.config.ConfigFile;

public class SignsLayoutConfig extends ConfigFile {
    private final List<String> activeLayout;
    private final List<String> waitingLayout;
    private final List<String> brokenLayout;
    private final List<String> buyLayout;

    public SignsLayoutConfig(JavaPlugin plugin) {
        super(plugin, "signsLayout");
        activeLayout = Colors.colorList(config.getStringList("Active"));
        waitingLayout = Colors.colorList(config.getStringList("Waiting"));
        buyLayout = Colors.colorList(config.getStringList("Buy"));
        brokenLayout = Colors.colorList(config.getStringList("Broken"));
    }

    public List<String> getActiveDonorLayout() {
        return activeLayout;
    }

    public List<String> getWaitingLayout() {
        return waitingLayout;
    }

	public List<String> getBokenLayout() {
		return brokenLayout;
	}

	public List<String> getActiveBuyLayout() {
		return buyLayout;
	}
}