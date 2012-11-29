package me.TfT02.Assassin.Listeners;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.AssassinMode;
import me.TfT02.Assassin.util.PlayerData;
import me.TfT02.Assassin.util.itemNamer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	Assassin plugin;

	public PlayerListener(final Assassin instance) {
		plugin = instance;
	}

	private final AssassinMode assassin = new AssassinMode(plugin);
	private final PlayerData data = new PlayerData(plugin);

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!data.isAssassin(player)) {
			data.setNeutral(player);
		}
		data.addCurrentTimestamp(player);
		String status = data.getStatus(player);
		player.sendMessage(ChatColor.YELLOW + "Your status = " + ChatColor.RED + status);
	}

	@EventHandler
	void onInventoryClick(InventoryClickEvent event) {
		HumanEntity player = event.getWhoClicked();
		if (data.isAssassin((Player) player)) {
			ItemStack currentitem = event.getCurrentItem();
			int id = currentitem.getTypeId();
			if (id == 35) {
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
		DamageCause cause = de.getCause();
		if (isEntityInvolved) {
			EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) de;
			Entity damager = edbe.getDamager();
			Projectile projectile = (Projectile) damager;

			boolean isAssassinInvolved = false;
			if (!data.bothNeutral(player, (Player) damager)) {
				isAssassinInvolved = true;
			}
			if (isAssassinInvolved) {

				String deathmessage = event.getDeathMessage();
				String damagername = ((Player) damager).getDisplayName();
				if (deathmessage.contains(damagername)) {
					damagername = ChatColor.DARK_RED + "[ASSASSIN]";
				}

//				if (cause == DamageCause.ENTITY_ATTACK && damager instanceof Player) {
////					event.setDeathMessage("Player " + name + " killed by" + damager);
//					event.setDeathMessage(damager + " Assassin has killed" + name);
//				}
//				else if (cause == DamageCause.PROJECTILE && projectile instanceof Arrow) {
//					if (projectile.getShooter() instanceof Player) {
//						Player shooter = (Player) projectile.getShooter();
////						event.setDeathMessage("Player " + name + " got shot by " + other.getDisplayName());
//						event.setDeathMessage(shooter.getDisplayName() + " Assassin has killed" + name);
//					}
//				}
			}
		}
	}

	/**
	 * Monitor PlayerInteract events.
	 * 
	 * @param event The event to watch
	 */
	@SuppressWarnings({ "unused" })
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Action action = event.getAction();
		final Block block = event.getClickedBlock();
		final ItemStack inHand = player.getItemInHand();
		Material material;

		/* Fix for NPE on interacting with air */
		if (block == null) {
			material = Material.AIR;
		} else {
			material = block.getType();
		}

		switch (action) {
		case LEFT_CLICK_AIR:
		case LEFT_CLICK_BLOCK:
			break;
		case RIGHT_CLICK_BLOCK:
		case RIGHT_CLICK_AIR:
			int inHandID = player.getItemInHand().getTypeId();
			if (inHandID == 35) {
				ItemStack itemHand = player.getInventory().getItemInHand();
				String item = itemNamer.getName(itemHand);
				String mask = ChatColor.DARK_RED + "Assassin Mask";
				if (item == null) {
				} else if (item.equalsIgnoreCase(mask)) {
					if (data.isAssassin(player)) {
						player.sendMessage(ChatColor.RED + "You already are an Assassin.");
					} else {
						assassin.activateAssassin(player);
					}
				}
			}
			break;
		default:
			break;
		}
	}
}