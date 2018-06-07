package com.minecraftmarket.minecraftmarket.nukkit.commands;

import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.nukkit.utils.chat.Colors;

import cn.nukkit.command.CommandSender;

public class UpdateSigns extends Cmd {
    private final MCMarket plugin;

    public UpdateSigns(MCMarket plugin) {
        super("updateSigns", "Updates plugin signs");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (plugin.getMainConfig().isUseSigns()) {
            if (MCMarket.isAuthenticated()) {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_sign_update")));
                plugin.getSignsTask().updateSigns();
            } else {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
            }
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_sign_disabled")));
        }
    }
}