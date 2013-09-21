package com.me.tft_02.assassin.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemChecks {

    public boolean isMask(ItemStack is) {
        Material material = is.getType();

        if (material != Material.WOOL) {
            return false;
        }

        ItemMeta im = is.getItemMeta();
        String name = im.getDisplayName();
        String mask = ChatColor.DARK_RED + "Assassin Mask";

        if (name == null) {
            return false;
        }

        if (name.equalsIgnoreCase(mask)) {
            return true;
        }

        return false;
    }
}
