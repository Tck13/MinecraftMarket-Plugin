package com.minecraftmarket.minecraftmarket.bukkit.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.configs.SignsConfig.BuySign;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.bukkit.utils.inventories.InventoryGUI;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;

public class SignsListener implements Listener {
	private final List<BlockFace> blockFaces = Arrays.asList(BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST);
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
						}
					} else {
						e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_order_not_right")));
					}
				} else {
					e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("missing_perm", "minecraftmarket.signs")));
					e.setCancelled(true);
				}
			} else if (lines.get(0).toLowerCase().equals("[mmbuy]")) {
				if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
					if (MCMarket.isAuthenticated()) {
						if (plugin.getInventoryManager().isloading()) {
							e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("gui_loading")));
							e.setCancelled(true);
						} else {
							plugin.getInventoryManager().buySignSetup.put(e.getPlayer().getUniqueId(), e.getBlock());
							plugin.getInventoryManager().open(e.getPlayer());
						}
					} else {
						e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
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
			} else if (plugin.getSignsConfig().getBuySignFor(e.getBlock()) != null) {
				if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
					if (plugin.getSignsConfig().removeBuySign(e.getBlock())) {
						e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_removed")));
						plugin.getSignsTask().updateSigns();
					}
				} else {
					e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("missing_perm", "minecraftmarket.signs")));
					e.setCancelled(true);
				}
			} else {
				for (BlockFace blockFace : blockFaces) {
					Block block = e.getBlock().getRelative(blockFace);
					if (block != null && block.getState() instanceof Sign && Objects.equals(getAttachedBlock(block), e.getBlock())) {
						if (plugin.getSignsConfig().getDonorSignFor(block) != null) {
							if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
								if (plugin.getSignsConfig().removeDonorSign(block)) {
									e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_removed")));
									plugin.getSignsTask().updateSigns();
								}
							} else {
								e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("missing_perm", "minecraftmarket.signs")));
								e.setCancelled(true);
							}
						} else if (plugin.getSignsConfig().getBuySignFor(block) != null) {
							if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
								if (plugin.getSignsConfig().removeBuySign(block)) {
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
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
        Player player = (Player)e.getPlayer();
        if(e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Sign) {
        	BuySign buySign = plugin.getSignsConfig().getBuySignFor(e.getClickedBlock());
        	if(buySign != null){
        		player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("gui_item_url", buySign.getUrl())));
        	}
        }
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(plugin.getInventoryManager().buySignSetup.containsKey(e.getPlayer().getUniqueId())) {
			plugin.getInventoryManager().buySignSetup.get(e.getPlayer().getUniqueId()).setType(Material.AIR);
			plugin.getInventoryManager().buySignSetup.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		Bukkit.getScheduler().runTask(plugin, () -> {
			if(!(e.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof InventoryGUI)) {
				if(plugin.getInventoryManager().buySignSetup.containsKey(e.getPlayer().getUniqueId())) {
					plugin.getInventoryManager().buySignSetup.get(e.getPlayer().getUniqueId()).setType(Material.AIR);
					plugin.getInventoryManager().buySignSetup.remove(e.getPlayer().getUniqueId());
				}			
			}
		});
		
	}
	
	private Block getAttachedBlock(Block block) {
		MaterialData data = block.getState().getData();
		if (data instanceof Attachable) {
			return block.getRelative(((Attachable) data).getAttachedFace());
		}
		return null;
	}
}