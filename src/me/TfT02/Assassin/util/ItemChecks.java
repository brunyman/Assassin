package me.TfT02.Assassin.util;

import me.TfT02.Assassin.Assassin;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ItemChecks {
	Assassin plugin;

	public ItemChecks(Assassin instance) {
		plugin = instance;
	}
	public boolean isMask(ItemStack itemstack){
		int id = itemstack.getTypeId();
		if (id == 35) {
			String item = itemNamer.getName(itemstack);
			String mask = ChatColor.DARK_RED + "Assassin Mask";
			if (item == null) {
			} else if (item.equalsIgnoreCase(mask)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMask1(ItemStack itemstack){
		int id = itemstack.getTypeId();
		if (id == 35) {
			String item = Namer.getName(itemstack);
			String mask = ChatColor.DARK_RED + "Assassin Mask";
			if (item == null) {
			} else if (item.equalsIgnoreCase(mask)) {
				return true;
			}
		}
		return false;
	}
}
