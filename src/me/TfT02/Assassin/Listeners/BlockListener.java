package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.itemNamer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
	Assassin plugin;

	public BlockListener(Assassin instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack itemHand = event.getItemInHand();
		if (itemHand.getType().equals(Material.WOOL)) {
			String item = itemNamer.getName(itemHand);
			String mask = ChatColor.DARK_RED + "Assassin Mask";
			if (item != null && item.equalsIgnoreCase(mask)) {
				event.setCancelled(true);
			}
		}
	}
	//TODO Perhaps prevent block breaks from Assassins?
	// This might be a simple way to prevent griefers to become anonymous and destroying stuff
}
