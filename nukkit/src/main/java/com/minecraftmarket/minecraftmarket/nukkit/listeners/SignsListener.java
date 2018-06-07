package com.minecraftmarket.minecraftmarket.nukkit.listeners;

import java.util.Arrays;
import java.util.List;

import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.nukkit.utils.chat.Colors;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.SignChangeEvent;

public class SignsListener implements Listener {
	private final MCMarket plugin;

	public SignsListener(MCMarket plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignChangeEvent(SignChangeEvent e) {
		if (plugin.getMainConfig().isUseSigns()) {
			List<String> lines = Arrays.asList(e.getLines());
			if (lines.get(0).toLowerCase().equals("[recentdonor]")) {
				if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
					if (lines.size() > 1 && Utils.isInt(lines.get(1))) {
						int order = Utils.getInt(lines.get(1));
						if (order > 0) {
							if (plugin.getSignsConfig().addDonorSign(order, e.getBlock())) {
								e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_added")));
								plugin.getSignsTask().updateSigns();
							}
						} else {
							e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_order_not_right")));
							e.setCancelled(true);
						}
					} else {
						e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_order_not_right")));
						e.setCancelled(true);
					}
				} else {
					e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("missing_perm", "minecraftmarket.signs")));
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		if (plugin.getMainConfig().isUseSigns()) {
			if (plugin.getSignsConfig().getDonorSignFor(e.getBlock()) != null) {
				if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
					if (plugin.getSignsConfig().removeDonorSign(e.getBlock())) {
						e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_removed")));
						plugin.getSignsTask().updateSigns();
					}
				} else {
					e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("missing_perm", "minecraftmarket.signs")));
					e.setCancelled(true);
				}
			}
		}
	}
}