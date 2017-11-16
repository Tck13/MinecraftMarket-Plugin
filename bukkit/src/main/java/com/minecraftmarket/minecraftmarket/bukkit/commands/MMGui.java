package com.minecraftmarket.minecraftmarket.bukkit.commands;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MMGui implements CommandExecutor {
    private MCMarket plugin;

    public MMGui(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (MCMarket.isAuthenticated()) {
                if (plugin.getMainConfig().isUseGUI()) {
                    plugin.getInventoryManager().open((Player) sender);
                } else {
                    sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_gui_disabled")));
                }
            } else {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
            }
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_invalid_sender")));
        }
        return true;
    }
}