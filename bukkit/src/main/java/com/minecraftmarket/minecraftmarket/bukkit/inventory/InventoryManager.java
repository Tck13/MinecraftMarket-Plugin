package com.minecraftmarket.minecraftmarket.bukkit.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.configs.GUILayoutConfig;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.bukkit.utils.inventories.InventoryGUI;
import com.minecraftmarket.minecraftmarket.bukkit.utils.items.ItemStackBuilder;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.Category;
import com.minecraftmarket.minecraftmarket.common.api.models.Item;
import com.minecraftmarket.minecraftmarket.common.api.models.Market;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;

public class InventoryManager {
	private final List<InventoryGUI> mainInventories;
	private final Map<String, InventoryGUI> inventories;
	private final MCMarket plugin;
	private ItemStack fillItem;
	private ItemStack closeItem;
	private ItemStack backItem;
	private ItemStack previousPageItem;
	private ItemStack nextPageItem;
	private Market market;
	
    public HashMap<UUID, Block> buySignSetup = new HashMap<>();

	public InventoryManager(MCMarket plugin) {
		this.mainInventories = new ArrayList<>();
		this.inventories = new HashMap<>();
		this.plugin = plugin;
		load();
	}

	private void setDefaultItems(GUILayoutConfig guiLayoutConfig) {
		ItemStackBuilder fillItemBuilder = getItemFromString(guiLayoutConfig.getBottomFillItem());
		if (fillItemBuilder == null)
			fillItemBuilder = new ItemStackBuilder(Material.IRON_FENCE);
		fillItem = fillItemBuilder.withName("&f").build();

		ItemStackBuilder closeItemBuilder = getItemFromString(guiLayoutConfig.getBottomCloseItem());
		if (closeItemBuilder == null)
			closeItemBuilder = new ItemStackBuilder(Material.REDSTONE_BLOCK);
		closeItem = closeItemBuilder.withName(Colors.color(I18n.tl("gui_close"))).build();

		ItemStackBuilder backItemBuilder = getItemFromString(guiLayoutConfig.getBottomBackItem());
		if (backItemBuilder == null)
			backItemBuilder = new ItemStackBuilder(Material.REDSTONE_BLOCK);
		backItem = backItemBuilder.withName(Colors.color(I18n.tl("gui_back"))).build();

		ItemStackBuilder previousPageItemBuilder = getItemFromString(guiLayoutConfig.getBottomPreviousPageItem());
		if (previousPageItemBuilder == null)
			previousPageItemBuilder = new ItemStackBuilder(Material.PAPER);
		previousPageItem = previousPageItemBuilder.withName(Colors.color(I18n.tl("gui_previous_page"))).build();

		ItemStackBuilder nextPageItemBuilder = getItemFromString(guiLayoutConfig.getBottomNextPageItem());
		if (nextPageItemBuilder == null)
			nextPageItemBuilder = new ItemStackBuilder(Material.PAPER);
		nextPageItem = nextPageItemBuilder.withName(Colors.color(I18n.tl("gui_next_page"))).build();
	}

	public void load() {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			GUILayoutConfig guiLayoutConfig = plugin.getGUILayoutConfig();

			int size = Math.max(guiLayoutConfig.getGuiRows() * 9, 9);
			setDefaultItems(guiLayoutConfig);

			if (MCMarket.isAuthenticated()) {
				MCMarketApi marketApi = MCMarket.getApi();
				market = marketApi.getMarket();

				List<Category> categories = marketApi.getCategories();
				int invs = Utils.roundUp(categories.size(), size) / size;

				for (int i = 1; i <= invs; i++) {
					InventoryGUI inventory = mainInventories.size() >= i ? mainInventories.get((i - 1)) : new InventoryGUI(guiLayoutConfig.getCategoryListTile(), size + 9, true);

					for (int pos = (size * i) - size; pos < categories.size() && pos < size * i; pos++) {
						Category category = categories.get(pos);

						inventory.setItem(pos, createCategoryInv(category, null), (player, slot, item) -> {
							inventories.get(category.getId() + "|1").open(player);
							return true;
						});
					}

					for(int b = categories.size(); b < size; b++){
						inventory.setItem(b, null);
					}
					
					if (i > 1) {
						int invNr = i - 1;
						inventory.setItem(size, previousPageItem, (player, slot, item) -> {
							mainInventories.get(invNr).open(player);
							return true;
						});
					}

					if (i != invs) {
						int invNr = i + 1;
						inventory.setItem(size + 8, nextPageItem, (player, slot, item) -> {
							mainInventories.get(invNr).open(player);
							return true;
						});
					}

					if (mainInventories.size() >= i) {
						// page already exist needs update
						inventory.getViewers().forEach(player -> ((Player) player).updateInventory());
					} else {
						// page is new, need to be added
						mainInventories.add(i - 1, inventory);
					}
				}

				for (int i = 0; i < mainInventories.size(); i++) {
					if (i >= invs) {
						// main inventory no longer exist
						mainInventories.get(i).getViewers().forEach(player -> {
							((Player) player).closeInventory();
							open((Player) player);
						});
						mainInventories.remove(i);
					}
				}

				List<String> exist = new ArrayList<>();
				for (int i = 0; i < categories.size(); i++) {
					Category category = categories.get(i);
					int s = Math.max(plugin.getGUILayoutConfig().getGuiRows() * 9, 9);
					int totalSlots = category.getSubCategories().size() > 0 ? Utils.roundUp(category.getSubCategories().size(), 9) + category.getItems().size() : category.getItems().size();
					int in = Math.max(Utils.roundUp(totalSlots, s) / s, 1);

					for (int a = 1; a <= in; a++) {
						exist.add(category.getId() + "|" + a);
					}
				}

				List<String> toRemove = new ArrayList<>(inventories.keySet());
				toRemove.removeAll(exist);
				for (int i = 0; i < toRemove.size(); i++) {
					String key = (String) toRemove.get(i);
					inventories.get(key).getViewers().forEach(player -> {
						((Player) player).closeInventory();
						open((Player) player);
					});
					inventories.remove(key);
				}
				inventories.values().forEach(i -> i.getViewers().stream().forEach(player -> {
					((Player) player).updateInventory();
				}));				
			} else {
				if (mainInventories != null && mainInventories.size() != 0) {
					mainInventories.forEach(i -> i.getViewers().stream().forEach(player -> {
						player.closeInventory();
						player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
					}));
					mainInventories.clear();
				}

				if (inventories != null && inventories.size() != 0) {
					inventories.values().forEach(i -> i.getViewers().stream().forEach(player -> {
						player.closeInventory();
						player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
					}));
					inventories.clear();
				}
			}
		});
	}

	public void addItems(InventoryGUI inventory, int size) {
		for (int i = size; i < size + 9; i++) {
			if (inventory.getItem(i) == null) {
				inventory.setItem(i, fillItem);
			}
		}

		inventory.setItem(size + 4, closeItem, (player, slot, item) -> {
			player.closeInventory();
			return true;
		});
	}

	public boolean isloading(){
		return !(mainInventories.size() > 0);
	}
	
	public void open(Player player) {
		if (MCMarket.isAuthenticated()) {
			if (isloading()) {
				player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("gui_loading")));
			} else {
				mainInventories.get(0).open(player);
			}
		} else {
			player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
		}
	}

	private ItemStack createCategoryInv(Category category, String parent) {
		GUILayoutConfig guiLayoutConfig = plugin.getGUILayoutConfig();

		int size = Math.max(guiLayoutConfig.getGuiRows() * 9, 9);

		int totalSlots = category.getSubCategories().size() > 0 ? Utils.roundUp(category.getSubCategories().size(), 9) + category.getItems().size() : category.getItems().size();
		int invs = Utils.roundUp(totalSlots, size) / size;

		if (invs > 0) {
			for (int i = 1; i <= invs; i++) {
				InventoryGUI inventory = inventories.get(category.getId() + "|" + i) != null ? inventories.get(category.getId() + "|" + i) : new InventoryGUI(replaceVars(guiLayoutConfig.getItemListTile(), category, null), size + 9, true);

				for (int pos = (size * i) - size; pos < totalSlots && pos < size * i; pos++) {
					if (category.getSubCategories().size() > 0 && pos < Utils.roundUp(category.getSubCategories().size(), 9)) {
						if (pos < category.getSubCategories().size()) {
							Category subCategory = category.getSubCategories().get(pos);
							inventory.setItem(pos, createCategoryInv(subCategory, category.getId() + "|" + i),
									(player, slot, item) -> {
										inventories.get(subCategory.getId() + "|1").open(player);
										return true;
									});
						}
						continue;
					}

					int itemPos = pos;
					int itemSlot = pos;
					if (category.getSubCategories().size() > 0) {
						itemPos = pos - Utils.roundUp(category.getSubCategories().size(), 9);
						if (i > (Utils.roundUp(Utils.roundUp(category.getSubCategories().size(), 9), size) / size)) {
							itemSlot = pos - ((i - 1) * size);
						}
					}

					Item item = category.getItems().get(itemPos);
					ItemStackBuilder itemIcon = getItemFromString(item.getIcon());
					if (itemIcon == null)
						itemIcon = new ItemStackBuilder(Material.CHEST);
					itemIcon.withName(replaceVars(guiLayoutConfig.getItemName(), category, item));
					for (String lines : guiLayoutConfig.getItemLore()) {
						for (String line : replaceVars(lines, category, item).split("\r\n")) {
							itemIcon.withLore(line);
						}
					}

					inventory.setItem(itemSlot, itemIcon.build(), (player, slot, itemStack) -> {
						if(buySignSetup.containsKey(player.getUniqueId())){
							if (plugin.getSignsConfig().addBuySign(item.getUrl(), buySignSetup.get(player.getUniqueId()))) {
								player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_added")));
								plugin.getSignsTask().updateSigns();
								buySignSetup.remove(player.getUniqueId());
							}
						} else {
							player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("gui_item_url", item.getUrl())));
						}
						player.closeInventory();
						return true;
					});
				}
				
				for(int b = totalSlots; b < size; b++){
					inventory.setItem(b, null);
				}

				if (i > 1) {
					int invNr = i - 1;
					inventory.setItem(size, previousPageItem, (player, slot, item) -> {
						inventories.get(category.getId() + "|" + invNr).open(player);
						return true;
					});
				}

				if (i != invs) {
					int invNr = i + 1;
					inventory.setItem(size + 8, nextPageItem, (player, slot, item) -> {
						inventories.get(category.getId() + "|" + invNr).open(player);
						return true;
					});
				}

				for (int pos = size; pos < size + 9; pos++) {
					if (inventory.getItem(pos) == null) {
						inventory.setItem(pos, fillItem);
					}
				}

				inventory.setItem(size + 4, backItem, (player, slot, item) -> {
					if (parent != null) {
						inventories.get(parent).open(player);
					} else {
						open(player);
					}
					return true;
				});

				inventories.put(category.getId() + "|" + i, inventory);
			}
		} else {
			InventoryGUI inventory = inventories.get(category.getId() + "|1") != null ? inventories.get(category.getId() + "|1") : new InventoryGUI(replaceVars(guiLayoutConfig.getItemListTile(), category, null), size + 9, true);
			
			for(int b = totalSlots; b < size; b++){
				inventory.setItem(b, null);
			}
			
			for (int pos = size; pos < size + 9; pos++) {
				if (inventory.getItem(pos) == null) {
					inventory.setItem(pos, fillItem);
				}
			}

			inventory.setItem(size + 4, backItem, (player, slot, item) -> {
				if (parent != null) {
					inventories.get(parent).open(player);
				} else {
					open(player);
				}
				return true;
			});
			inventories.put(category.getId() + "|1", inventory);
		}

		ItemStackBuilder catItem = getItemFromString(category.getIcon());
		if (catItem == null)
			catItem = new ItemStackBuilder(Material.ENDER_CHEST);
		catItem.withName(replaceVars(guiLayoutConfig.getCategoryName(), category, null));
		for (String lines : guiLayoutConfig.getCategoryLore()) {
			for (String line : replaceVars(lines, category, null).split("\r\n")) {
				catItem.withLore(line);
			}
		}

		return catItem.build();
	}

	private ItemStackBuilder getItemFromString(String item) {
		ItemStackBuilder itemBuilder = null;
		if (item.contains(":")) {
			String[] splitedItem = item.split(":");
			Material itemMat = Material.matchMaterial(splitedItem[0]);
			if (itemMat != null && Utils.isInt(splitedItem[1])) {
				itemBuilder = new ItemStackBuilder(itemMat).withData(Utils.getInt(splitedItem[1]));
			}
		} else {
			Material itemMat = Material.matchMaterial(item);
			if (itemMat != null) {
				itemBuilder = new ItemStackBuilder(itemMat);
			}
		}
		return itemBuilder;
	}

	private String replaceVars(String msg, Category category, Item item) {
		if (market != null) {
			msg = msg.replace("{market_id}", "" + market.getId()).replace("{market_name}", market.getName())
					.replace("{market_currency}", market.getCurrency().getCode())
					.replace("{market_url}", market.getUrl());
		}
		if (category != null) {
			msg = msg.replace("{category_id}", "" + category.getId()).replace("{category_name}", category.getName())
					.replace("{category_description}", category.getDescription());
		}
		if (item != null) {
			msg = msg.replace("{item_id}", "" + item.getId()).replace("{item_name}", item.getName())
					.replace("{item_description}", item.getDescription()).replace("{item_url}", item.getUrl())
					.replace("{item_price}", item.hasSale() ? "" + ChatColor.RED + ChatColor.STRIKETHROUGH
							+ item.getPrice() + ChatColor.WHITE + " " + item.getSalePrice() : item.getPrice());
		}
		return msg;
	}
}