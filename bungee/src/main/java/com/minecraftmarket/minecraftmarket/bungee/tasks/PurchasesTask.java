package com.minecraftmarket.minecraftmarket.bungee.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.bungee.utils.BungeeRunnable;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.Command;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PurchasesTask implements Runnable {
    private final MCMarket plugin;
    private final List<MCMarketApi.CommandType> commandTypes = Arrays.asList(
            MCMarketApi.CommandType.EXPIRY,
            MCMarketApi.CommandType.CHARGEBACK,
            MCMarketApi.CommandType.REFUND,
            MCMarketApi.CommandType.INITIAL,
            MCMarketApi.CommandType.RENEWAL
    );

    public PurchasesTask(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getProxy().getScheduler().runAsync(plugin, this::updatePurchases);
    }

    public void updatePurchases() {
        if (MCMarket.isAuthenticated()) {
            for (MCMarketApi.CommandType commandType : commandTypes) {
                for (Command command : MCMarket.getApi().getCommands(commandType, 1, 2)) {
                    runCommand(command);
                }
            }
        }
    }

    private void runCommand(Command command) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(command.getPlayer().getName());
        if (command.isRequiredOnline() && (player == null || !player.isConnected())) {
            return;
        }

        if (MCMarket.getApi().setExecuted(command.getId())) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.getCommand());
                if (command.isRepeat()) {
                    long period = command.getRepeatPeriod() > 0 ? 60 * 60 * command.getRepeatPeriod() : 1;
                    new BungeeRunnable() {
                        int executed = 0;

                        @Override
                        public void run() {
                            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.getCommand());
                            executed++;

                            if (executed >= command.getRepeatCycles()) {
                                cancel();
                            }
                        }
                    }.schedule(plugin, period, period, TimeUnit.SECONDS);
                }
            }, command.getDelay() > 0 ? command.getDelay() : 1, TimeUnit.SECONDS);
        }
    }
}