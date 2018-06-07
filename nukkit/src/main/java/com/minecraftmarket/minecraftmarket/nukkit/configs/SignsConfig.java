package com.minecraftmarket.minecraftmarket.nukkit.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import com.minecraftmarket.minecraftmarket.nukkit.utils.config.ConfigFile;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;

public class SignsConfig extends ConfigFile {
    private final Map<Integer, Set<DonorSign>> donorSigns = new HashMap<>();

    public SignsConfig(PluginBase plugin) {
        super(plugin, "signs");

        for (String key : config.getKeys(false)) {
            if (Utils.isInt(key)) {
                Set<DonorSign> signs = new HashSet<>();
                for (Location loc : stringsToLocArray(config.getStringList(key))) {
                    if (loc != null) {
                        BlockEntity block = loc.getLevel().getBlockEntity(loc);
                        if (block != null && block instanceof BlockEntitySign) {
                            signs.add(new DonorSign(Utils.getInt(key), block.getBlock()));
                        }
                    }
                }
                donorSigns.put(Utils.getInt(key), signs);
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
                config.set(String.valueOf(key), locs);
            } else {
                config.set(String.valueOf(key), null);
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
                config.set(String.valueOf(donorSign.getKey()), locs);
            } else {
                config.set(String.valueOf(donorSign.getKey()), null);
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
            return loc.getFloorX() == bLoc.getFloorX() && loc.getFloorY() == bLoc.getFloorY() && loc.getFloorZ() == bLoc.getFloorZ();
        }
    }

    private String locToString(Location loc) {
        return loc.getLevel().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    private Location stringToLoc(String str) {
        String[] a = str.split(",");
        if (a.length < 4) {
            return null;
        }

        Level w = Server.getInstance().getLevelByName(a[0]);
        if (w == null) {
            return null;
        }

        double x = Double.parseDouble(a[1]);
        double y = Double.parseDouble(a[2]);
        double z = Double.parseDouble(a[3]);

        return new Location(x, y, z, w);
    }

    private List<Location> stringsToLocArray(List<String> strings) {
        List<Location> locs = new ArrayList<>();
        for (String string : strings) {
            locs.add(stringToLoc(string));
        }
        return locs;
    }
}