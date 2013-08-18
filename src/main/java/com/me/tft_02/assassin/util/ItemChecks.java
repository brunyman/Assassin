package com.me.tft_02.assassin.util;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.me.tft_02.assassin.Assassin;

public class ItemChecks {
    Assassin plugin;

    public ItemChecks(Assassin instance) {
        plugin = instance;
    }

    public boolean isMask(ItemStack is) {
        int id = is.getTypeId();
        if (id == 35) {
            ItemMeta im = is.getItemMeta();
            String name = im.getDisplayName();
            String mask = ChatColor.DARK_RED + "Assassin Mask";
            if (name == null) {
                return false;
            }

            if (name.equalsIgnoreCase(mask)) {
                return true;
            }
        }
        return false;
    }
}
