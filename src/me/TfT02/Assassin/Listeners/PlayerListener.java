package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.AssassinMode;
import me.TfT02.Assassin.runnables.EndCooldownTimer;
import me.TfT02.Assassin.util.BlockChecks;
import me.TfT02.Assassin.util.ItemChecks;
import me.TfT02.Assassin.util.PlayerData;
import me.TfT02.Assassin.util.itemNamer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	Assassin plugin;

	public PlayerListener(Assassin instance) {
		plugin = instance;
	}

	private AssassinMode assassin = new AssassinMode(plugin);
	private PlayerData data = new PlayerData(plugin);
	private ItemChecks itemcheck = new ItemChecks(plugin);

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!data.isAssassin(player)) {
			data.setNeutral(player);
		}
		else if (data.isAssassin(player)){
			assassin.applyTraits(player);
			assassin.applyMaskForce(player);
//			data.addLoginTime(player);
		}
		if (!data.cooledDown(player)) {
			long cooldowntime = 1200L;
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new EndCooldownTimer(player.getName()), cooldowntime);
		}
		String status = data.getStatus(player);
		player.sendMessage(ChatColor.YELLOW + "Your status = " + ChatColor.RED + status);
	}

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if (data.isAssassin(player)){
			data.addLogoutTime(player);
			data.saveActiveTime(player);
		}
	}

	@EventHandler
	void onInventoryClick(InventoryClickEvent event) {
		/*
		HumanEntity player = event.getWhoClicked();
		if (data.isAssassin((Player) player)) {
			ItemStack currentitem = event.getCurrentItem();
			int id = currentitem.getTypeId();
			if (id == 35) {
				SlotType slotType = event.getSlotType();
				switch (slotType) {
				case ARMOR:
//					String item = itemNamer.getName(currentitem);
					String item = new NamedItemStack(new ItemStack(currentitem)).getName();
					String mask = ChatColor.DARK_RED + "Assassin Mask";
					if (item == null) {
					} else if (item.equalsIgnoreCase(mask)) {
						event.setCancelled(true);
					}
				default:
					break;
				}
			}
			else {
				//NOTHING HERE
			}
		}*/
		HumanEntity player = event.getWhoClicked();
		if (data.isAssassin((Player) player)) {
			ItemStack itemstack = event.getCurrentItem();
			SlotType slotType = event.getSlotType();
			switch (slotType) {
			case ARMOR:
				if (itemcheck.isMask(itemstack)) {
					event.setCancelled(true);
				}
			default:
				break;
			}
		}
		else {
			//NOT AN ASSASSIN
			//TODO Perhaps if neutral and clicked on mask in armor slot, delete this mask
		}
	}

	/**
	 * Monitor PlayerInteract events.
	 * 
	 * @param event The event to watch
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		ItemStack inHand = player.getItemInHand();
		@SuppressWarnings("unused")
		Material material;

		/* Fix for NPE on interacting with air */
		if (block == null) {
			material = Material.AIR;
		}
		else {
			material = block.getType();
		}

		switch (action) {
		case RIGHT_CLICK_BLOCK:
		case RIGHT_CLICK_AIR:
			int inHandID = inHand.getTypeId();
			if ((inHandID == 35) && BlockChecks.abilityBlockCheck(block)) {
				ItemStack itemHand = player.getInventory().getItemInHand();
				String item = itemNamer.getName(itemHand);
				String mask = ChatColor.DARK_RED + "Assassin Mask";
				if (item == null) {
				} else if (item.equalsIgnoreCase(mask)) {
					if (!player.hasPermission("assassin.assassin")) {
						player.sendMessage(ChatColor.RED + "You haven't got permission.");
					}
				else{
					if(!data.cooledDown(player)){
						player.sendMessage(ChatColor.RED + "You need to wait before you can use that again...");
					}
					else {
						if (data.isAssassin(player)) {
							player.sendMessage(ChatColor.RED + "You already are an Assassin.");
						} else {
							System.out.println("Activating assassin");
							assassin.activateAssassin(player);
							long cooldowntime = 2400L;
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new EndCooldownTimer(player.getName()), cooldowntime);
						}
					}
					}
				}
			}
			break;
		default:
			break;
		}
	}
}