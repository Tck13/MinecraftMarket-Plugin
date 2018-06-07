package com.minecraftmarket.minecraftmarket.bukkit.utils.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;

public class ItemStackBuilder {
    private final ItemStack itemStack;

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStackBuilder(Material mat) {
        this.itemStack = new ItemStack(mat);
    }

    public ItemStackBuilder withName(String name) {
        if (itemStack.getType() == Material.AIR) return this;
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Colors.color(name));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(String name) {
        if (itemStack.getType() == Material.AIR) return this;
        final ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(Colors.color(name));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    public List<String> getLore() {
        if (itemStack.getType() == Material.AIR) return new ArrayList<>();
        final ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        return lore;
    }

    public ItemStackBuilder withItemFlag(ItemFlag flag) {
        if (itemStack.getType() == Material.AIR) return this;
        final ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        if (itemStack.getType() == Material.AIR) return this;
        itemStack.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withDurability(int durability) {
        if (itemStack.getType() == Material.AIR) return this;
        itemStack.setDurability((short) durability);
        return this;
    }

    public ItemStackBuilder withData(int data) {
        if (itemStack.getType() == Material.AIR) return this;
        itemStack.setDurability((short) data);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, int level) {
        if (itemStack.getType() == Material.AIR) return this;
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        if (itemStack.getType() == Material.AIR) return this;
        itemStack.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder withType(Material material) {
        if (material != null) {
            itemStack.setType(material);
        }
        return this;
    }

    public ItemStackBuilder clearLore() {
        if (itemStack.getType() == Material.AIR) return this;
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(new ArrayList<>());
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        if (itemStack.getType() == Material.AIR) return this;
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            itemStack.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}