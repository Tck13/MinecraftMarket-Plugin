package com.minecraftmarket.minecraftmarket.nukkit.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.NukkitRunnable;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.Command;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;

import java.util.Arrays;
import java.util.List;

public class PurchasesTask extends AsyncTask {
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
    public void onRun() {
        updatePurchases();
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
        Player player = Server.getInstance().getPlayerExact(command.getPlayer().getName());
        if (command.isRequiredOnline() && (player == null || !player.isOnline())) {
            return;
        }

        if (command.getRequiredSlots() > 0 && player != null) {
            if (getEmptySlots(player.getInventory()) < command.getRequiredSlots()) {
                return;
            }
        }

        if (MCMarket.getApi().setExecuted(command.getId())) {
            plugin.getServer().getScheduler().scheduleDelayedTask(plugin, new AsyncTask() {
                @Override
                public void onRun() {
                    plugin.getServer().getScheduler().scheduleTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                    if (command.isRepeat()) {
                        long period = command.getRepeatPeriod() > 0 ? 20 * 60 * 60 * command.getRepeatPeriod() : 1;
                        new NukkitRunnable() {
                            int executed = 0;

                            @Override
                            public void run() {
                                plugin.getServer().getScheduler().scheduleTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                                executed++;

                                if (executed >= command.getRepeatCycles()) {
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(plugin, (int) period, (int) period);
                    }
                }
            }, command.getDelay() > 0 ? (int) (20 * command.getDelay()) : 1, true);
        }
    }

    private int getEmptySlots(PlayerInventory inventory) {
        int amount = 0;
        for (Item item : inventory.getContents().values()) {
            if (item == null || item.getId() < 1) {
                amount++;
            }
        }
        return amount;
    }
}