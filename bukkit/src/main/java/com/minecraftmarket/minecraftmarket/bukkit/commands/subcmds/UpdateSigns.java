package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import java.util.concurrent.CompletableFuture;

import org.bukkit.command.CommandSender;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;

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
                CompletableFuture.runAsync(() -> plugin.getSignsTask().updateSigns()).thenRun(() -> {
                    sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_sign_update_done")));
                });
            } else {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
            }
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_sign_disabled")));
        }
    }
}