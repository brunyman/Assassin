package me.TfT02.Assassin.Listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.PlayerData;

public class PlayerListener implements Listener {
	Assassin plugin;

	public PlayerListener(final Assassin instance) {
		plugin = instance;
	}
	private final PlayerData data = new PlayerData(plugin);

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if (!data.isAssassin(player)){
			data.setNeutral(player);
			}
		String status = data.getStatus(player);
		player.sendMessage("Current status: " + status);
	}
	@EventHandler
	void onInventoryClick(InventoryClickEvent event) {
		HumanEntity player = event.getWhoClicked();
		if (data.isAssassin((Player) player)){
			SlotType slotType = event.getSlotType();
			switch (slotType) {
			case ARMOR:
				event.setCancelled(true);
			default:
				break;
			}
		}
	}
}