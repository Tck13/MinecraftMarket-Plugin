package com.minecraftmarket.minecraftmarket.sponge.commands.subcmds;


import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import com.minecraftmarket.minecraftmarket.sponge.utils.chat.Colors;

public class Check extends Cmd {
    private final MCMarket plugin;

    public Check(MCMarket plugin) {
        super("check", "Manually check for new purchases");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (MCMarket.isAuthenticated()) {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_check_purchases")));
            Sponge.getScheduler().createTaskBuilder().async().execute(() -> plugin.getPurchasesTask().updatePurchases()).submit(plugin);
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
        }
    }
}