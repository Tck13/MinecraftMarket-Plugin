package com.minecraftmarket.minecraftmarket.nukkit.configs;

import cn.nukkit.plugin.PluginBase;
import com.minecraftmarket.minecraftmarket.nukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.nukkit.utils.config.ConfigFile;

import java.util.List;

public class SignsLayoutConfig extends ConfigFile {
    private final List<String> activeLayout;
    private final List<String> waitingLayout;

    public SignsLayoutConfig(PluginBase plugin) {
        super(plugin, "signsLayout");

        activeLayout = Colors.colorList(config.getStringList("Active"));
        waitingLayout = Colors.colorList(config.getStringList("Waiting"));
    }

    public List<String> getActiveLayout() {
        return activeLayout;
    }

    public List<String> getWaitingLayout() {
        return waitingLayout;
    }
}