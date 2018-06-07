package com.minecraftmarket.minecraftmarket.sponge.listeners;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import com.minecraftmarket.minecraftmarket.sponge.utils.chat.Colors;

public class SignsListener {
	private final MCMarket plugin;

	public SignsListener(MCMarket plugin) {
		this.plugin = plugin;
	}

	@Listener
	public void onChangeSignEvent(ChangeSignEvent e, @First Player player) {
		if (plugin.getMainConfig().isUseSigns()) {
			Optional<Text> optionalLine1 = e.getText().get(0);
			Optional<Text> optionalLine2 = e.getText().get(1);
			if (optionalLine1.isPresent()) {
				String line1 = optionalLine1.get().toPlain();
				if (line1.toLowerCase().equals("[recentdonor]")) {
					if (player.hasPermission("minecraftmarket.signs")) {
						if (optionalLine2.isPresent()) {
							String line2 = optionalLine2.get().toPlain();
							int order = Utils.getInt(line2);
							if (order > 0) {
								if (plugin.getSignsConfig().addDonorSign(order, e.getTargetTile().getLocation())) {
									player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_added")));
									plugin.getSignsTask().updateSigns();
								}
							} else {
								player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_order_not_right")));
								e.setCancelled(true);
							}
						} else {
							player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_order_not_right")));
							e.setCancelled(true);
						}
					} else {
						player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("missing_perm", "minecraftmarket.signs")));
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break e, @First Player player) {
		if (plugin.getMainConfig().isUseSigns()) {
			if (e.getTransactions().size() > 0) {
				BlockSnapshot blockSnapshot = e.getTransactions().get(0).getOriginal();
				Optional<Location<World>> optionalLocation = blockSnapshot.getLocation();
				if (optionalLocation.isPresent()) {
					if (plugin.getSignsConfig().getDonorSignFor(optionalLocation.get()) != null) {
						if (player.hasPermission("minecraftmarket.signs")) {
							if (plugin.getSignsConfig().removeDonorSign(optionalLocation.get())) {
								player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_removed")));
								plugin.getSignsTask().updateSigns();
							}
						} else {
							player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("missing_perm", "minecraftmarket.signs")));
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
}