package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import org.bukkit.command.CommandSender;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;

public class Reload extends Cmd {
    private final MCMarket plugin;

    public Reload(MCMarket plugin) {
        super("reload", "Reloads the plugin");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_reloading")));
        plugin.reloadConfigs(authenticated -> {
            if (!authenticated) {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("api_auth_failed")));
            }
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_reload_done")));
        });
    }
}