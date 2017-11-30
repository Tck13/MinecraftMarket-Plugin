package com.minecraftmarket.minecraftmarket.bukkit.tasks;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.Command;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

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
        Player player = Bukkit.getPlayerExact(command.getPlayer().getName());
        if (command.isRequiredOnline() && (player == null || !player.isOnline())) {
            return;
        }

        if (command.getRequiredSlots() > 0 && (player != null && player.isOnline())) {
            if (getEmptySlots(player.getInventory()) < command.getRequiredSlots()) {
                return;
            }
        }

        if (MCMarket.getApi().setExecuted(command.getId())) {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                if (command.isRepeat()) {
                    long period = command.getRepeatPeriod() > 0 ? 20 * 60 * 60 * command.getRepeatPeriod() : 1;
                    new BukkitRunnable() {
                        int executed = 0;

                        @Override
                        public void run() {
                            plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                            executed++;

                            if (executed >= command.getRepeatCycles()) {
                                cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(plugin, period, period);
                }
            }, command.getDelay() > 0 ? 20 * command.getDelay() : 1);
        }
    }

    private int getEmptySlots(PlayerInventory inventory) {
        int amount = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                amount++;
            }
        }
        return amount;
    }
}