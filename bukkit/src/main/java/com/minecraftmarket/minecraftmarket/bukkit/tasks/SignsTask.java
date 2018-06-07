package com.minecraftmarket.minecraftmarket.bukkit.tasks;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.configs.SignsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.utils.items.SkullUtils;
import com.minecraftmarket.minecraftmarket.common.api.models.Currency;
import com.minecraftmarket.minecraftmarket.common.api.models.Item;
import com.minecraftmarket.minecraftmarket.common.api.models.Market;
import com.minecraftmarket.minecraftmarket.common.api.models.Purchase;

public class SignsTask implements Runnable {
    private final MCMarket plugin;
    private final DateFormat mcmDateFormat;
    private final DateFormat dateFormat;
    
    public SignsTask(MCMarket plugin) {
        this.plugin = plugin;
        this.mcmDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.dateFormat = new SimpleDateFormat(plugin.getMainConfig().getDateFormat());
    }

    @Override
    public void run() {
        updateSigns();
    }

    public void updateSigns() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (MCMarket.isAuthenticated()) {
            	List<Purchase> purchases = MCMarket.getApi().getPurchases(1, 1);
                Map<Integer, Set<SignsConfig.DonorSign>> donorSigns = plugin.getSignsConfig().getDonorSigns();
                plugin.getServer().getScheduler().runTask(plugin, () -> {
	                for (Integer key : donorSigns.keySet()) {
	                    for (SignsConfig.DonorSign donorSign : donorSigns.get(key)) {
	                        if (donorSign.getBlock().getState() instanceof Sign) {
	                            Sign sign = (Sign) donorSign.getBlock().getState();
	                            if (key <= purchases.size()) {
	                                Purchase purchase = purchases.get(key - 1);
	                                List<String> lines = plugin.getSignsLayoutConfig().getActiveDonorLayout();
	                                if (lines.size() == 1) {
	                                    sign.setLine(0, replaceVars(lines.get(0), purchase));
	                                } else if (lines.size() == 2) {
	                                    sign.setLine(0, replaceVars(lines.get(0), purchase));
	                                    sign.setLine(1, replaceVars(lines.get(1), purchase));
	                                } else if (lines.size() == 3) {
	                                    sign.setLine(0, replaceVars(lines.get(0), purchase));
	                                    sign.setLine(1, replaceVars(lines.get(1), purchase));
	                                    sign.setLine(2, replaceVars(lines.get(2), purchase));
	                                } else if (lines.size() == 4) {
	                                    sign.setLine(0, replaceVars(lines.get(0), purchase));
	                                    sign.setLine(1, replaceVars(lines.get(1), purchase));
	                                    sign.setLine(2, replaceVars(lines.get(2), purchase));
	                                    sign.setLine(3, replaceVars(lines.get(3), purchase));
	                                } else {
	                                    sign.setLine(0, "");
	                                    sign.setLine(1, "");
	                                    sign.setLine(2, "");
	                                    sign.setLine(3, "");
	                                }
	                                
	                                    Block attached = getAttachedBlock(donorSign.getBlock());
	                                    if (attached != null) {
	                                        Block up = attached.getRelative(BlockFace.UP);
	                                        if (up != null && up.getState() instanceof Skull) {
	                                            if (purchase.getPlayer().getSkinUrl().length() > 0) {
	                                                SkullUtils.setSkullWithNonPlayerProfile(purchase.getPlayer().getSkinUrl(), purchase.getPlayer().getName(), up);
	                                            } else {
	                                                SkullUtils.setSkullWithNonPlayerProfile(plugin.getMainConfig().getDefaultHeadSkin(), purchase.getPlayer().getName(), up);
	                                            }
	                                        }
	                                    }
	                            } else {
	                                List<String> lines = plugin.getSignsLayoutConfig().getWaitingLayout();
	                                if (lines.size() == 1) {
	                                    sign.setLine(0, lines.get(0));
	                                } else if (lines.size() == 2) {
	                                    sign.setLine(0, lines.get(0));
	                                    sign.setLine(1, lines.get(1));
	                                } else if (lines.size() == 3) {
	                                    sign.setLine(0, lines.get(0));
	                                    sign.setLine(1, lines.get(1));
	                                    sign.setLine(2, lines.get(2));
	                                } else if (lines.size() == 4) {
	                                    sign.setLine(0, lines.get(0));
	                                    sign.setLine(1, lines.get(1));
	                                    sign.setLine(2, lines.get(2));
	                                    sign.setLine(3, lines.get(3));
	                                } else {
	                                    sign.setLine(0, "");
	                                    sign.setLine(1, "");
	                                    sign.setLine(2, "");
	                                    sign.setLine(3, "");
	                                }
	
	                                    Block attached = getAttachedBlock(donorSign.getBlock());
	                                    if (attached != null) {
	                                        Block up = attached.getRelative(BlockFace.UP);
	                                        if (up != null && up.getState() instanceof Skull) {
	                                            SkullUtils.setSkullWithNonPlayerProfile(plugin.getMainConfig().getDefaultHeadSkin(), "Steve", up);
	                                        }
	                                    }
	                            }
	                                try {
	                                    sign.update();
	                                    sign.update(true, true);
	                                } catch (NullPointerException ignored) {
	                                }
	                        } else {
	                        	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
	                        		plugin.getSignsConfig().removeDonorSign(donorSign.getBlock());
	                        	});
	                        }
	                    }
	                }
                });
                List<Item> items = MCMarket.getApi().getItems();
                Market market = MCMarket.getApi().getMarket();
                if(market != null){
                	Currency currency = market.getCurrency();
                    Map<String, Set<SignsConfig.BuySign>> buySigns = plugin.getSignsConfig().getBuySigns();
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
    	                for (String url : buySigns.keySet()) {
    	                    for (SignsConfig.BuySign buySign : buySigns.get(url)) {
    	                        if (buySign.getBlock().getState() instanceof Sign) {
    	                            Sign sign = (Sign) buySign.getBlock().getState();
    	                            Item item = getItemByUrl(items, url);
    	                            if (item != null) {
    	                                List<String> lines = plugin.getSignsLayoutConfig().getActiveBuyLayout();
    	                                if (lines.size() == 1) {
    	                                    sign.setLine(0, replaceVars(lines.get(0), item, currency));
    	                                } else if (lines.size() == 2) {
    	                                    sign.setLine(0, replaceVars(lines.get(0), item, currency));
    	                                    sign.setLine(1, replaceVars(lines.get(1), item, currency));
    	                                } else if (lines.size() == 3) {
    	                                    sign.setLine(0, replaceVars(lines.get(0), item, currency));
    	                                    sign.setLine(1, replaceVars(lines.get(1), item, currency));
    	                                    sign.setLine(2, replaceVars(lines.get(2), item, currency));
    	                                } else if (lines.size() == 4) {
    	                                    sign.setLine(0, replaceVars(lines.get(0), item, currency));
    	                                    sign.setLine(1, replaceVars(lines.get(1), item, currency));
    	                                    sign.setLine(2, replaceVars(lines.get(2), item, currency));
    	                                    sign.setLine(3, replaceVars(lines.get(3), item, currency));
    	                                } else {
    	                                    sign.setLine(0, "");
    	                                    sign.setLine(1, "");
    	                                    sign.setLine(2, "");
    	                                    sign.setLine(3, "");
    	                                }
    	                            } else {
    	                                List<String> lines = plugin.getSignsLayoutConfig().getBokenLayout();
    	                                if (lines.size() == 1) {
    	                                    sign.setLine(0, lines.get(0));
    	                                } else if (lines.size() == 2) {
    	                                    sign.setLine(0, lines.get(0));
    	                                    sign.setLine(1, lines.get(1));
    	                                } else if (lines.size() == 3) {
    	                                    sign.setLine(0, lines.get(0));
    	                                    sign.setLine(1, lines.get(1));
    	                                    sign.setLine(2, lines.get(2));
    	                                } else if (lines.size() == 4) {
    	                                    sign.setLine(0, lines.get(0));
    	                                    sign.setLine(1, lines.get(1));
    	                                    sign.setLine(2, lines.get(2));
    	                                    sign.setLine(3, lines.get(3));
    	                                } else {
    	                                    sign.setLine(0, "");
    	                                    sign.setLine(1, "");
    	                                    sign.setLine(2, "");
    	                                    sign.setLine(3, "");
    	                                }
    	                            }
    	                                try {
    	                                    sign.update();
    	                                    sign.update(true, true);
    	                                } catch (NullPointerException ignored) {
    	                                }
    	                        } else {
    	                        	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
    	                        		plugin.getSignsConfig().removeBuySign(buySign.getBlock());
    	                        	});
    	                        }
    	                    }
    	                }
                    });
                }
            }
        });
    }
    
    private Item getItemByUrl(List<Item> items, String url) {
    	for (int i = 0; i < items.size(); i++){
    		if(items.get(i).getUrl().equals(url))
    			return items.get(i);
    	}
    	return null;
    }

    private Block getAttachedBlock(Block block) {
        MaterialData data = block.getState().getData();
        if (data instanceof Attachable) {
            return block.getRelative(((Attachable) data).getAttachedFace());
        }
        return null;
    }

    private String replaceVars(String msg, Purchase purchase) {
        msg = msg.replace("{purchase_id}", "" + purchase.getId())
                .replace("{purchase_name}", purchase.getName())
                .replace("{purchase_price}", purchase.getPrice())
                .replace("{purchase_currency}", purchase.getCurrency().getCode())
                .replace("{player_name}", purchase.getPlayer().getName());
        try {
            msg = msg.replace("{purchase_date}", dateFormat.format(mcmDateFormat.parse(purchase.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return msg;
    }
    
    private String replaceVars(String msg, Item item, Currency currency) {
        msg = msg.replace("{item_id}", "" + item.getId())
                .replace("{item_name}", item.getName())
                .replace("{item_price}", item.hasSale() ? "" + ChatColor.RED + ChatColor.STRIKETHROUGH
						+ item.getPrice() + ChatColor.WHITE + " " + item.getSalePrice() : item.getPrice())
                .replace("{shop_currency}", currency.getCode());
        return msg;
    }
}