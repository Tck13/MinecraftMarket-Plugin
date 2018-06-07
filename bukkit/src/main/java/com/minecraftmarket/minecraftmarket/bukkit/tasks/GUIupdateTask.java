package com.minecraftmarket.minecraftmarket.bukkit.tasks;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;

public class GUIupdateTask implements Runnable {
	private final MCMarket plugin;

	public GUIupdateTask(MCMarket plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		plugin.getInventoryManager().load();
	}

}
