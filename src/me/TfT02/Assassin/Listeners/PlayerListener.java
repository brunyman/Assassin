package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.AssassinMode;
import me.TfT02.Assassin.runnables.EndCooldownTimer;
import me.TfT02.Assassin.util.BlockChecks;
import me.TfT02.Assassin.util.PlayerData;
import me.TfT02.Assassin.util.itemNamer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
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

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!data.isAssassin(player)) {
			data.setNeutral(player);
		}
		else if (data.isAssassin(player)){
			assassin.applyTraits(player);
//			data.addLoginTime(player);
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
		HumanEntity player = event.getWhoClicked();
		if (data.isAssassin((Player) player)) {
			ItemStack currentitem = event.getCurrentItem();
			int id = currentitem.getTypeId();//TODO NPE in creativemode
			if (id != 35) {
			}
			else {
				String item = itemNamer.getName(currentitem);
				String mask = ChatColor.DARK_RED + "Assassin Mask";
				if (item == null) {
				} else if (item.equalsIgnoreCase(mask)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		String name = player.getDisplayName();
		EntityDamageEvent de = player.getLastDamageCause();

		boolean isEntityInvolved = false;
		if (de instanceof EntityDamageByEntityEvent) {
			isEntityInvolved = true;
		}
		if (isEntityInvolved) {
			EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) de;
			Entity damager = edbe.getDamager();
			if (!data.bothNeutral(player, (Player) damager)) {
				String deathmessage = event.getDeathMessage();
				String damagername = ((Player) damager).getDisplayName();
				if (deathmessage.contains(damagername)) {
					damagername = ChatColor.DARK_RED + "[ASSASSIN]";
				}
				if (deathmessage.contains(name)) {
					name = ChatColor.DARK_RED + "[ASSASSIN]";
				}
			}
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
			break;
		default:
			break;
		}
	}
}