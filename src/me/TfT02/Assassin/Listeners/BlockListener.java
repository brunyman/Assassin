package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.ItemChecks;
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

	private ItemChecks itemcheck = new ItemChecks(plugin);
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack itemHand = event.getItemInHand();
		if (itemHand.getType().equals(Material.WOOL)) {
			if (itemcheck.isMask(itemHand)) {
				event.setCancelled(true);
			}
		}
	}
	//TODO Perhaps prevent block breaks from Assassins?
	// This might be a simple way to prevent griefers to become anonymous and destroying stuff
}
