package com.minecraftmarket.minecraftmarket.bukkit.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecraftmarket.minecraftmarket.bukkit.utils.config.ConfigFile;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;

public class SignsConfig extends ConfigFile {
	private final Map<Integer, Set<DonorSign>> donorSigns = new HashMap<>();
	private final Map<String, Set<BuySign>> buySigns = new HashMap<>();
	
	public SignsConfig(JavaPlugin plugin) {
		super(plugin, "signs");

		// update old signs.yml
		if (config.getConfigurationSection("DonorSigns") == null && config.getConfigurationSection("BuySigns") == null) {
			for (String key : config.getKeys(false)) {
				if (config.getConfigurationSection("DonorSigns") == null) {
					config.createSection("DonorSigns");
				}
				config.getConfigurationSection("DonorSigns").set(key, config.getStringList(key));
				config.set(key, null);
			}
			saveConfig();
		}

		if (config.getConfigurationSection("DonorSigns") != null) {
			for (String key : config.getConfigurationSection("DonorSigns").getKeys(false)) {
				if (Utils.isInt(key)) {
					Set<DonorSign> signs = new HashSet<>();
					for (Location loc : stringsToLocArray(config.getStringList("DonorSigns." + key))) {
						if (loc != null) {
							Block block = loc.getWorld().getBlockAt(loc);
							if (block.getState() instanceof Sign) {
								signs.add(new DonorSign(Utils.getInt(key), block));
							}
						}
					}
					donorSigns.put(Utils.getInt(key), signs);
				}
			}
		}
		if (config.getConfigurationSection("BuySigns") != null) {
			for (String url : config.getConfigurationSection("BuySigns").getKeys(false)) {
				Set<BuySign> signs = new HashSet<>();
				for (Location loc : stringsToLocArray(config.getStringList("BuySigns." + url))) {
					if (loc != null) {
						Block block = loc.getWorld().getBlockAt(loc);
						if (block.getState() instanceof Sign) {
							signs.add(new BuySign(url.replace('#', '.'), block));
						}
					}
				}
				buySigns.put(url.replace('#', '.'), signs);
			}
		}
	}

	public Map<Integer, Set<DonorSign>> getDonorSigns() {
		return donorSigns;
	}

	public boolean addDonorSign(Integer key, Block block) {
		DonorSign donorSign = getDonorSignFor(block);
		if (donorSign == null) {
			Set<DonorSign> signs;
			if (donorSigns.containsKey(key)) {
				signs = donorSigns.get(key);
			} else {
				signs = new HashSet<>();
			}
			signs.add(new DonorSign(key, block));
			donorSigns.put(key, signs);
			List<String> locs = new ArrayList<>();
			for (DonorSign ds : signs) {
				locs.add(locToString(ds.getBlock().getLocation()));
			}
			if (locs.size() > 0) {
				config.set("DonorSigns." + String.valueOf(key), locs);
			} else {
				config.set("DonorSigns." + String.valueOf(key), null);
			}
			saveConfig();
			return true;
		}
		return false;
	}

	public boolean removeDonorSign(Block block) {
		DonorSign donorSign = getDonorSignFor(block);
		if (donorSign != null) {
			Set<DonorSign> signs = donorSigns.get(donorSign.getKey());
			signs.remove(donorSign);
			List<String> locs = new ArrayList<>();
			for (DonorSign ds : signs) {
				locs.add(locToString(ds.getBlock().getLocation()));
			}
			if (locs.size() > 0) {
				config.set("DonorSigns." + String.valueOf(donorSign.getKey()), locs);
			} else {
				config.set("DonorSigns." + String.valueOf(donorSign.getKey()), null);
			}
			saveConfig();
			return true;
		}
		return false;
	}

	public DonorSign getDonorSignFor(Block block) {
		for (Set<DonorSign> donorSigns : donorSigns.values()) {
			for (DonorSign donorSign : donorSigns) {
				if (donorSign.isFor(block)) {
					return donorSign;
				}
			}
		}
		return null;
	}

	public class DonorSign {
		private final Integer key;
		private final Block block;

		DonorSign(Integer key, Block block) {
			this.key = key;
			this.block = block;
		}

		public Integer getKey() {
			return key;
		}

		public Block getBlock() {
			return this.block;
		}

		public boolean isFor(Block block) {
			Location loc = this.block.getLocation();
			Location bLoc = block.getLocation();
			return loc.getBlockX() == bLoc.getBlockX() && loc.getBlockY() == bLoc.getBlockY()
					&& loc.getBlockZ() == bLoc.getBlockZ();
		}
	}

	public Map<String, Set<BuySign>> getBuySigns() {
		return buySigns;
	}

	public boolean addBuySign(String url, Block block) {
		BuySign buySign = getBuySignFor(block);
		if (buySign == null) {
			Set<BuySign> signs;
			if (buySigns.containsKey(url)) {
				signs = buySigns.get(url);
			} else {
				signs = new HashSet<>();
			}
			signs.add(new BuySign(url, block));
			buySigns.put(url, signs);
			List<String> locs = new ArrayList<>();
			for (BuySign bs : signs) {
				locs.add(locToString(bs.getBlock().getLocation()));
			}
			if (locs.size() > 0) {
				config.set("BuySigns." + url.replace('.', '#'), locs);
			} else {
				config.set("BuySigns." + url.replace('.', '#'), null);
			}
			saveConfig();
			return true;
		}
		return false;
	}

	public boolean removeBuySign(Block block) {
		BuySign buySign = getBuySignFor(block);
		if (buySign != null) {
			Set<BuySign> signs = buySigns.get(buySign.getUrl());
			signs.remove(buySign);
			List<String> locs = new ArrayList<>();
			for (BuySign bs : signs) {
				locs.add(locToString(bs.getBlock().getLocation()));
			}
			if (locs.size() > 0) {
				config.set("BuySigns." + buySign.getUrl().replace('.', '#'), locs);
			} else {
				config.set("BuySigns." + buySign.getUrl().replace('.', '#'), null);
			}
			saveConfig();
			return true;
		}
		return false;
	}

	public BuySign getBuySignFor(Block block) {
		for (Set<BuySign> buySigns : buySigns.values()) {
			for (BuySign buySign : buySigns) {
				if (buySign.isFor(block)) {
					return buySign;
				}
			}
		}
		return null;
	}

	public class BuySign {
		private final String url;
		private final Block block;

		BuySign(String url, Block block) {
			this.url = url;
			this.block = block;
		}

		public String getUrl() {
			return url;
		}

		public Block getBlock() {
			return this.block;
		}

		public boolean isFor(Block block) {
			Location loc = this.block.getLocation();
			Location bLoc = block.getLocation();
			return loc.getBlockX() == bLoc.getBlockX() && loc.getBlockY() == bLoc.getBlockY()
					&& loc.getBlockZ() == bLoc.getBlockZ();
		}
	}

	private String locToString(Location loc) {
		return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
	}

	private Location stringToLoc(String str) {
		String[] a = str.split(",");
		if (a.length < 4) {
			return null;
		}

		World w = Bukkit.getServer().getWorld(a[0]);
		if (w == null) {
			return null;
		}

		double x = Double.parseDouble(a[1]);
		double y = Double.parseDouble(a[2]);
		double z = Double.parseDouble(a[3]);

		return new Location(w, x, y, z);
	}

	private List<Location> stringsToLocArray(List<String> strings) {
		List<Location> locs = new ArrayList<>();
		for (String string : strings) {
			locs.add(stringToLoc(string));
		}
		return locs;
	}
}